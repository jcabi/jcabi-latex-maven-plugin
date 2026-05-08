/*
 * SPDX-FileCopyrightText: Copyright (c) 2009-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.latex.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Compiler}.
 *
 * @since 1.0
 */
final class CompilerTest {

    @Test
    @Timeout(120)
    void compilesSimpleLatexSourceIntoPng(@TempDir final Path dir)
        throws Exception {
        Assumptions.assumeTrue(
            CompilerTest.hasBinary("pdflatex")
                && CompilerTest.hasBinary("gs")
                && CompilerTest.hasBinary("pnmtopng"),
            "pdflatex, ghostscript or netpbm not installed, skipping"
        );
        final File sources = dir.resolve("src").toFile();
        sources.mkdirs();
        Files.writeString(
            sources.toPath().resolve("hello.tex"),
            "\\documentclass{article}\\begin{document}Hi\\end{document}",
            StandardCharsets.UTF_8
        );
        final Output output = new Compiler(
            dir.resolve("temp").toFile()
        ).compile(
            new Source(sources, "hello.tex", Collections.emptyList())
        );
        final File save = dir.resolve("out").toFile();
        save.mkdirs();
        output.saveTo(save);
        MatcherAssert.assertThat(
            "PNG file is missing after compiling a simple LaTeX source",
            new File(save, "hello.png").exists(),
            Matchers.is(true)
        );
    }

    private static boolean hasBinary(final String name) {
        final String osname = System.getProperty("os.name");
        final String which;
        if (osname != null && osname.startsWith("Windows")) {
            which = "where";
        } else {
            which = "which";
        }
        boolean found;
        try {
            found = new ProcessBuilder(which, name).start().waitFor() == 0;
        } catch (final IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            found = false;
        }
        return found;
    }
}
