/*
 * SPDX-FileCopyrightText: Copyright (c) 2009-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.latex.maven.plugin;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Compile PNG images and PDF documents from TeX/LaTeX sources.
 *
 * @since 1.0
 * @checkstyle MemberNameCheck (500 lines)
 */
@Mojo(
    name = "compile",
    defaultPhase = LifecyclePhase.PRE_SITE,
    threadSafe = true
)
@SuppressWarnings("PMD.ImmutableField")
public final class CompileMojo extends AbstractMojo {

    /**
     * Sources directory.
     */
    @Parameter(
        defaultValue = "${project.basedir}/src/main/latex",
        required = true
    )
    private transient File sourcesDir;

    /**
     * Destination directory.
     */
    @Parameter(
        defaultValue = "${project.build.directory}/site/latex",
        required = true
    )
    private transient File outputDir;

    /**
     * Temporary directory.
     */
    @Parameter(
        defaultValue = "${project.build.directory}/latex-temp",
        required = true
    )
    private transient File tempDir;

    /**
     * Sources.
     */
    @Parameter(required = true)
    private transient Set<String> sources = new HashSet<>(0);

    /**
     * Closures.
     */
    @Parameter(required = true)
    private transient Set<String> closures = new HashSet<>(0);

    /**
     * Shall we skip execution?
     */
    @Parameter(property = "latex.skip", defaultValue = "false")
    private transient boolean skip;

    @Override
    public void execute() throws MojoFailureException {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());
        if (this.skip) {
            Logger.info(this, "execution skipped because of 'skip' option");
        } else {
            if (SystemUtils.IS_OS_WINDOWS) {
                throw new MojoFailureException(
                    "Sorry, this plugin cannot run on Windows system!"
                );
            }
            if (this.outputDir.mkdirs()) {
                Logger.info(this, "directories created for %s", this.outputDir);
            }
            for (final String src : this.sources) {
                this.compile(new Compiler(this.tempDir), src);
            }
        }
    }

    /**
     * Compile one source.
     * @param compiler The compiler to use
     * @param name The name of the source
     * @throws MojoFailureException If some problem
     */
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    private void compile(final Compiler compiler, final String name)
        throws MojoFailureException {
        try {
            final Source source = new Source(
                this.sourcesDir,
                name,
                this.closures
            );
            final long started = System.nanoTime();
            final Output output = compiler.compile(source);
            output.saveTo(this.outputDir);
            Logger.info(
                this,
                "'%s' compiled and saved as '%s', in %[nano]s",
                source,
                output,
                System.nanoTime() - started
            );
        } catch (final IOException ex) {
            throw new MojoFailureException(
                String.format("Failed to compile '%s'", name),
                ex
            );
        }
    }
}
