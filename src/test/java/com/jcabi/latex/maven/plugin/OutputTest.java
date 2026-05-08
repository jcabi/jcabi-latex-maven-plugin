/*
 * SPDX-FileCopyrightText: Copyright (c) 2009-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.latex.maven.plugin;

import java.io.File;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Output}.
 *
 * @since 1.0
 */
final class OutputTest {

    @Test
    void rendersFilePathInToString() {
        final File file = new File("foo/bar.png");
        MatcherAssert.assertThat(
            new Output(file).toString(),
            Matchers.equalTo(file.getPath())
        );
    }
}
