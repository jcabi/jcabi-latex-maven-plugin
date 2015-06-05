<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />
 
[![Build Status](https://travis-ci.org/jcabi/jcabi-latex-maven-plugin.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-latex-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-latex-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-latex-maven-plugin)

Maven plugin for generating PNG images from LaTeX sources during `mvn site` phase.

Project website: [latex.jcabi.com](http://latex.jcabi.com/index.html)

## Usage

- Make sure LaTeX and [Netpbm](http://netpbm.sourceforge.net/) are installed on your machine;

- Configure the plugin pointing it to your LaTeX sources:

```xml
<project>
  <build>
    <plugins>
      <plugin>
        <groupId>com.jcabi</groupId>
        <artifactId>jcabi-latex-maven-plugin</artifactId>
        <version>1.1</version>
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

- Run `mvn site` and a PNG file will be created in `target/site/latex/picture.png`


## Known issues

Using this plugin on a Windows machine is not supported.

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-maven-plugin/issues/new).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
