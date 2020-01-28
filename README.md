jena-utils
===========
Utility classes for working with Apache Jena:
* A set of fluent helper classes for building SPARQL queries
* A transaction helper class that supports nested transactions
* Classes to help work with Java `Stream` APIs from Jena `StmtIterator` and `ResultSet`
* Utilities to work with RDF LangStrings and Java `Locale`s

Getting Started
---------------
You need to include my bintray maven repository in your build.

`https://dl.bintray.com/duck-asteroid/maven/`

Then use the maven co-ordinates:

```
<dependency>
    <groupId>com.asteroid.duck</groupId>
    <artifactId>jena-utils</artifactId>
    <version>0.0.7</version>
</dependency>
```

or in gradle: `com.asteroid.duck:jena-utils:0.0.7`


Download
-----------
This project is available via Maven repo at: https://dl.bintray.com/duck-asteroid/maven/

Build Process
-------------
A basic Java application using:
 * [Gradle](https://docs.gradle.org/current/userguide/userguide.html "Gradle User Guide") - Build is managed by Gradle
 * [GitHub](https://github.com/duckAsteroid/jena-utils) - Git source control
 * [Travis-CI](https://travis-ci.org/duckAsteroid/jena-utils) - Continuous integration build server
 * [SonarQube](https://sonarcloud.io/dashboard?id=com.asteroid.duck%3Ajena-utils) - Code quality and test coverage
 * [Bintray](https://dl.bintray.com/duck-asteroid/maven/) - Hosts binary releases (Maven2 repo)

![Build status](https://travis-ci.org/duckAsteroid/jena-utils.svg?branch=master)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FduckAsteroid%2Fjena-utils.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FduckAsteroid%2Fjena-utils?ref=badge_shield)




## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FduckAsteroid%2Fjena-utils.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FduckAsteroid%2Fjena-utils?ref=badge_large)