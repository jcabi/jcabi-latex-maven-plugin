/*
 * SPDX-FileCopyrightText: Copyright (c) 2009-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.latex.maven.plugin;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * Output.
 *
 * <p>This class is immutable and thread-safe.
 *
 * @since 1.0
 */
final class Output {

    /**
     * The file.
     */
    private final transient File file;

    /**
     * Public ctor.
     * @param path Location of the file
     */
    Output(final File path) {
        this.file = path;
    }

    /**
     * Save to this directory.
     * @param dir The folder to save to
     * @throws IOException If failed
     */
    public void saveTo(final File dir) throws IOException {
        FileUtils.copyFileToDirectory(this.file, dir);
    }

    @Override
    public String toString() {
        return this.file.getPath();
    }

}
