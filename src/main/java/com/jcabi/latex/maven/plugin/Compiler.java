/**
 * Copyright (c) 2009-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.latex.maven.plugin;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Compiler.
 *
 * <p>This class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
final class Compiler {

    /**
     * Temp dir to use.
     */
    private final transient File temp;

    /**
     * Public ctor.
     * @param dir Temporary directory to use
     */
    Compiler(final File dir) {
        this.temp = dir;
        if (!this.temp.exists() && this.temp.mkdir()) {
            Logger.info(Compiler.class, "directory created: %s", this.temp);
        }
    }

    /**
     * Compile source and produce output.
     * @param src The source to compile
     * @return The output
     * @throws IOException If some error
     */
    public Output compile(final Source src) throws IOException {
        final File dir = new File(this.temp, src.name());
        if (dir.exists()) {
            Logger.info(this, "Source '%s' doesn't require re-compiling", src);
        } else {
            if (dir.mkdir()) {
                Logger.info(this, "directory %s created", dir);
            }
            this.copy(src, dir);
            this.process(src, dir);
        }
        return new Output(new File(dir, String.format("%s.png", src.name())));
    }

    /**
     * Copy files from source to temp directory.
     * @param src The source to compile
     * @param dir The directory to copy to
     * @throws IOException If some error
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private void copy(final Source src, final File dir) throws IOException {
        final Map<String, URL> files = src.files();
        for (final Map.Entry<String, URL> entry : files.entrySet()) {
            final File file = new File(dir, entry.getKey());
            if (file.getParentFile().mkdirs()) {
                Logger.info(this, "directories created for %s", file);
            }
            FileUtils.copyURLToFile(entry.getValue(), file);
        }
        Logger.debug(
            this, "#copy('%s', '%s'): copied %d files: %[list]s",
            src, dir, files.size(), files.keySet()
        );
    }

    /**
     * Process files in the directory.
     * @param src The source to use
     * @param dir The directory to process
     * @throws IOException If some error
     */
    private void process(final Source src, final File dir) throws IOException {
        final String cmd = StringUtils.join(
            String.format(
                "'%s' -halt-on-error -interaction=nonstopmode '%s.tex'",
                this.bin("latex"), src.name()
            ),
            String.format(
                " && '%s' -o %s.ps %2$s.dvi",
                this.bin("dvips"), src.name()
            ),
            " && echo quit",
            String.format(
                // @checkstyle LineLength (1 line)
                "| '%s' -q -dNOPAUSE -sDEVICE=ppmraw -sOutputFile=- -r300 %s.ps",
                this.bin("gs"), src.name()
            ),
            String.format(
                // @checkstyle LineLength (1 line)
                "| '%s' -bgcolor rgb:ff/ff/ff -falias -fgcolor rgb:00/00/00 -weight 0.6",
                this.bin("pnmalias")
            ),
            String.format(
                "| '%s' -white",
                this.bin("pnmcrop")
            ),
            String.format(
                "| '%s' 0.5",
                this.bin("pnmscale")
            ),
            String.format(
                "| '%s' -interlace > '%s.png'",
                this.bin("pnmtopng"), src.name()
            )
        );
        final ProcessBuilder builder =
            new ProcessBuilder("/bin/sh", "-c", cmd);
        builder.directory(dir);
        Logger.debug(
            this, "#process('%s', '%s'): running: '%s'",
            src, dir, cmd
        );
        final Process process = builder.start();
        FileUtils.write(
            new File(dir, "_output.log"),
            IOUtils.toString(process.getInputStream())
        );
        final int status;
        try {
            status = process.waitFor();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IOException(ex);
        }
        if (status != 0) {
            final File error = new File(dir, "_error.log");
            FileUtils.write(
                error,
                IOUtils.toString(process.getErrorStream())
            );
            Logger.error(
                this,
                "Compilation failed with code #%d:\n%s",
                status,
                FileUtils.readFileToString(error)
            );
            throw new IOException(
                String.format(
                    "Failed in '%s', see %s/_error.log for more details",
                    src,
                    dir
                )
            );
        }
    }

    /**
     * Find binary and return its full name.
     * @param name Short name
     * @return Full name
     * @throws IOException If some error
     */
    private File bin(final String name) throws IOException {
        final String[] paths = {
            "/bin",
            "/usr/bin",
            "/usr/local/bin",
            "/opt/local/bin",
            "/sbin",
            "/usr/sbin",
            "/usr/local/sbin",
            "/opt/local/sbin",
        };
        File file = null;
        for (final String path : paths) {
            final File bin = new File(path, name);
            if (bin.exists() && bin.isFile()) {
                file = bin;
                break;
            }
        }
        if (file == null) {
            final ProcessBuilder builder =
                new ProcessBuilder("/usr/bin/which", name);
            final Process process = builder.start();
            try {
                if (process.waitFor() == 0) {
                    file = new File(
                        IOUtils.toString(process.getInputStream()).trim()
                    );
                }
            } catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException(ex);
            }
        }
        if (file == null) {
            throw new IOException(
                String.format(
                    "Failed to find executable of '%s'",
                    name
                )
            );
        }
        return file;
    }

}
