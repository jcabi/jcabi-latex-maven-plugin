/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 jcabi.com
 * SPDX-License-Identifier: BSD-3-Clause
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
