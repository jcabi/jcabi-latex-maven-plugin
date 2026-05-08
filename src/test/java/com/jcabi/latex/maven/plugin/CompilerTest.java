/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 jcabi.com
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.jcabi.latex.maven.plugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
}
