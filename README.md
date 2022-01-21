[forks]: https://img.shields.io/github/forks/demkom58/springram
[stars]: https://img.shields.io/github/stars/demkom58/springram
[issues]: https://img.shields.io/github/issues/demkom58/springram
[license]: https://img.shields.io/github/license/demkom58/springram
[jitpack]: https://jitpack.io/v/demkom58/springram.svg

[ ![forks][] ](https://github.com/demkom58/springram/network/members)
[ ![stars][] ](https://github.com/demkom58/springram/stargazers)
[ ![issues][] ](https://github.com/demkom58/springram/issues)
[ ![license][] ](https://github.com/demkom58/springram/blob/master/LICENSE)
[ ![jitpack][] ](https://jitpack.io/#demkom58/springram)

<img align="right" src="https://i.ibb.co/mGrR6V5/logo.png" height="100" width="100">

# Springram
Springram is a library for working with telegram using the spring
framework and spring boot. This library gives you the ability to
use controllers like in spring web-mvc.

## How to use

To understand how to work with this library, you can see the [wiki](https://github.com/demkom58/springram/wiki) section.

### Maven
Add jitpack repository to your `pom.xml`, like this:
```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
```

Then add dependency
```xml
    <dependency>
        <groupId>com.github.demkom58</groupId>
        <artifactId>springram</artifactId>
        <version>0.3-SNAPSHOT</version>
    </dependency>
```

### Gradle

Add jitpack repository to your `build.gradle`, like this:
```groovy
    repositories {
        maven { url 'https://jitpack.io' }
    }
```

Then add dependency
```groovy
    dependencies {
        implementation 'com.github.demkom58:springram:0.3-SNAPSHOT'
    }
```