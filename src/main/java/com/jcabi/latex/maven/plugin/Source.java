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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * Source of one file.
 *
 * <p>This class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
final class Source {

    /**
     * Name of the source (main file).
     */
    private final transient String main;

    /**
     * Collection of files (this Map is updated only in ctor and never
     * ever touched/changed again).
     */
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final transient Map<String, URL> inputs =
        new HashMap<String, URL>(0);

    /**
     * Public ctor.
     * @param dir Directory with sources
     * @param name Name of source
     * @param closures List of closures
     * @throws IOException If some error inside
     */
    Source(final File dir, final String name,
        final Iterable<String> closures) throws IOException {
        if (dir == null || name == null || closures == null) {
            throw new IllegalArgumentException(
                "NULL is not allowed in Source"
            );
        }
        if (!dir.exists()) {
            throw new IllegalArgumentException(
                String.format("Directory '%s' doesn't exist", dir)
            );
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                "Empty name of source is not allowed"
            );
        }
        this.main = FilenameUtils.getBaseName(name);
        this.append(dir, name);
        for (final String closure : closures) {
            this.append(dir, closure);
        }
    }

    /**
     * Build and return names of files and their actual locations.
     * @return Names and locations
     */
    public Map<String, URL> files() {
        Logger.debug(
            this,
            "#files(): %d files found for '%s': %[list]s",
            this.inputs.size(),
            this.main,
            this.inputs.keySet()
        );
        return Collections.unmodifiableMap(this.inputs);
    }

    /**
     * Short name of it (without extension and path).
     * @return The name
     */
    public String name() {
        return this.main;
    }

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * Append one new source to the list.
     * @param dir Directory with sources
     * @param name The name of source
     * @throws IOException If some IO problem
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void append(final File dir, final String name) throws IOException {
        final File file;
        final String path;
        if (name.charAt(0) == '/') {
            file = new File(this.getClass().getResource(name).getFile());
            path = FilenameUtils.getName(file.getPath());
        } else {
            file = new File(dir, name);
            if (!file.exists()) {
                throw new IOException(String.format("file %s not found", file));
            }
            path = name;
        }
        if (file.isDirectory()) {
            for (final String sub : Source.subs(file)) {
                this.inputs.put(sub, new File(file, sub).toURI().toURL());
            }
        } else {
            this.inputs.put(path, file.toURI().toURL());
        }
    }

    /**
     * Find all sub-files in this directory (excluding folders).
     * @param dir Directory with sources
     * @return List of files (recursively)
     */
    private static Iterable<String> subs(final File dir) {
        final IOFileFilter filter = new RegexFileFilter("[^\\.].*");
        final Collection<File> files = FileUtils.listFiles(dir, filter, filter);
        final Collection<String> subs = new ArrayList<String>(files.size());
        for (final File file : files) {
            subs.add(file.getPath().substring(dir.getPath().length() + 1));
        }
        return subs;
    }

}
