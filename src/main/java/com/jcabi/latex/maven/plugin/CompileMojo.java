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
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Compile PNG images and PDF documents from TeX/LaTeX sources.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @goal compile
 * @phase pre-site
 * @threadSafe
 * @checkstyle MemberNameCheck (500 lines)
 */
@SuppressWarnings("PMD.ImmutableField")
public final class CompileMojo extends AbstractMojo {

    /**
     * Sources directory.
     * @parameter expression="${project.basedir}/src/main/latex"
     * @required
     */
    private transient File sourcesDir;

    /**
     * Destination directory.
     * @parameter expression="${project.build.directory}/site/latex"
     * @required
     */
    private transient File outputDir;

    /**
     * Temporary directory.
     * @parameter expression="${project.build.directory}/latex-temp"
     * @required
     */
    private transient File tempDir;

    /**
     * Sources.
     * @parameter
     * @required
     */
    private transient Set<String> sources = new HashSet<String>(0);

    /**
     * Closures.
     * @parameter
     * @required
     */
    private transient Set<String> closures = new HashSet<String>(0);

    /**
     * Shall we skip execution?
     * @parameter expression="${latex.skip}" default-value="false"
     * @required
     */
    private transient boolean skip;

    @Override
    public void execute() throws MojoFailureException {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());
        if (this.skip) {
            Logger.info(this, "execution skipped because of 'skip' option");
        } else {
            checkOS();
            if (this.outputDir.mkdirs()) {
                Logger.info(this, "directories created for %s", this.outputDir);
            }
            final Compiler compiler = new Compiler(this.tempDir);
            for (final String src : this.sources) {
                this.compile(compiler, src);
            }
        }
    }

    /**
     * Compile one source.
     * @param compiler The compiler to use
     * @param name The name of the source
     * @throws MojoFailureException If some problem
     */
    private void compile(final Compiler compiler, final String name)
        throws MojoFailureException {
        final long start = System.nanoTime();
        try {
            final Source source = new Source(
                this.sourcesDir,
                name,
                this.closures
            );
            final Output output = compiler.compile(source);
            output.saveTo(this.outputDir);
            Logger.info(
                this,
                "'%s' compiled and saved as '%s', in %[nano]s",
                source,
                output,
                System.nanoTime() - start
            );
        } catch (final IOException ex) {
            throw new MojoFailureException(
                String.format("Failed to compile '%s'", name),
                ex
            );
        }
    }

    /**
     * Checks the type of the operating system running and failing if it's an
     * unsupported one (like Windows) for this plugin.
     * @throws MojoFailureException If an unsupported operation system is detected
     */
    private void checkOS() throws MojoFailureException {
        final String osName = System.getProperty("os.name");
        if (StringUtils.startsWith(osName, "Windows")) {
            throw new MojoFailureException(
                "Sorry, this plugin cannot run on Windows system!"
            );
        }
    }
}
