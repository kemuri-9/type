# Type Implementation

Java provides a powerful type introspection engine, but does not support the creation of a number of these types by the user at runtime.
Also when doing type introspection, in some circumstances the types are incomplete and require manual completion.
The type implementations here can support in this process.

## Building

The project uses gradle through the gradle wrapper to build.
As such,  _./gradlew_  or simply  _gradlew_ on some operating systems in the project root directory should be utilized for building.
Any Java 8+ JVM can be utilized to run gradle itself.

Building and test execution use different JVM releases which encompass all of versions 8 through 17.
If gradle does not detect a JVM for that release version, it will attempt to download it.
Running a build for the first time can take a considerable amount of time depending on bandwidth to download the JVMs.
Further details on this behavior can be found at [https://docs.gradle.org/current/userguide/toolchains.html](https://docs.gradle.org/current/userguide/toolchains.html)

## Javadoc

Javadoc for releases are available at [https://kemuri-9.github.io/type/](https://kemuri-9.github.io/type/)
