# jcabi-latex-maven-plugin

[![mvn](https://github.com/jcabi/jcabi-latex-maven-plugin/actions/workflows/mvn.yml/badge.svg)](https://github.com/jcabi/jcabi-latex-maven-plugin/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-latex-maven-plugin)](http://www.0pdd.com/p?name=jcabi/jcabi-latex-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-latex-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-latex-maven-plugin)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-latex-maven-plugin.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-latex-maven-plugin)

Maven plugin for generating PNG images from LaTeX
  sources during `mvn site` phase.

Make sure the following tools are installed on your machine and
available on `PATH`:

- `pdflatex` (from any LaTeX distribution, e.g. TeX Live)
- `gs` (Ghostscript)
- `pnmcrop`, `pnmscale`, and `pnmtopng`
(from [Netpbm](http://netpbm.sourceforge.net/))

On Debian or Ubuntu, this is enough:

```bash
sudo apt-get install -y texlive-latex-base ghostscript netpbm
```

Configure the plugin pointing it to your LaTeX sources:

```xml
<project>
  <build>
    <plugins>
      <plugin>
        <groupId>com.jcabi</groupId>
        <artifactId>jcabi-latex-maven-plugin</artifactId>
        <version>1.2.0</version>
        <executions>
          <execution>
            <goals>
              <goal>compile</goal>
            </goals>
            <configuration>
              <sources>
                <file>picture.tex</file>
              </sources>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

Run `mvn site` and a PNG file will be created in `target/site/latex/picture.png`

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```bash
mvn clean install -Pqulice
```
