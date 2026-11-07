/*
 * SPDX-FileCopyrightText: Copyright (c) 2009-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.latex.maven.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link Source}.
 *
 * @since 1.0
 */
final class SourceTest {

    @Test
    void collectsNestedMainAtCompilationRoot(@TempDir final Path dir)
        throws Exception {
        final Path main = Files.writeString(
            Files.createDirectories(dir.resolve("chapters/figures"))
                .resolve("picture.tex"),
            "main source"
        );
        final Source source = new Source(
            dir.toFile(), "chapters/figures/picture.tex", Collections.emptyList()
        );
        MatcherAssert.assertThat(
            "The compiler must find the selected main file beside its output",
            source.files(),
            Matchers.equalTo(
                Collections.singletonMap(
                    String.format("%s.tex", source.name()), main.toUri().toURL()
                )
            )
        );
    }

    @Test
    void preservesNestedClosurePaths(@TempDir final Path dir) throws Exception {
        final Path main = Files.writeString(
            Files.createDirectory(dir.resolve("chapters")).resolve("picture.tex"),
            "main source"
        );
        Files.writeString(
            Files.createDirectory(dir.resolve("includes")).resolve("header.tex"),
            "closure source"
        );
        MatcherAssert.assertThat(
            "Only the main file path must be flattened",
            new Source(
                dir.toFile(), "chapters/picture.tex",
                Collections.singletonList("includes/header.tex")
            ).files(),
            Matchers.equalTo(
                Map.of(
                    "picture.tex", main.toUri().toURL(),
                    "includes/header.tex",
                    dir.resolve("includes/header.tex").toUri().toURL()
                )
            )
        );
    }

    @Test
    void preservesDirectorySourcePaths(@TempDir final Path dir) throws Exception {
        final Path root = Files.createDirectories(dir.resolve("chapters/picture"));
        final Path main = Files.writeString(root.resolve("picture.tex"), "main");
        Files.writeString(
            Files.createDirectory(root.resolve("sections")).resolve("extra.tex"),
            "extra source"
        );
        MatcherAssert.assertThat(
            "Directory sources must retain paths relative to their own root",
            new Source(
                dir.toFile(), "chapters/picture", Collections.emptyList()
            ).files(),
            Matchers.equalTo(
                Map.of(
                    "picture.tex", main.toUri().toURL(),
                    dir.getFileSystem().getPath("sections", "extra.tex").toString(),
                    root.resolve("sections/extra.tex").toUri().toURL()
                )
            )
        );
    }
}
