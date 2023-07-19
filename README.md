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

<img align="right" src="https://raw.githubusercontent.com/demkom58/springram/web/logo.svg" height="100" width="100" alt="Springram Logo">

# Springram
Springram is a telegram framework based on the Spring framework and Spring Boot.
It gives you the ability to use controllers like in spring web-mvc.


This framework based on [rubenlagus' TelegramBots](https://github.com/rubenlagus/TelegramBots) 
that is Java implementation of Telegram API.

## How to use

To understand how to work with this framework, you can see the [wiki](https://github.com/demkom58/springram/wiki) section.

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

Then add `springram-core` dependency and `spring-boot`, if you 
don't have it already.
```xml
<dependencies>
    <dependency>
        <groupId>com.github.demkom58</groupId>
        <artifactId>springram-core</artifactId>
        <version>0.5-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

If you want to use Spring security methods then you should 
add `springram-core` and `spring-security` dependencies.
```xml
<dependencies>
    <dependency>
        <groupId>com.github.demkom58</groupId>
        <artifactId>springram-security</artifactId>
        <version>0.5-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>
```

### Gradle

Add jitpack repository to your `build.gradle`, like this:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then add `springram-core` dependency and `spring-boot`, if you don't have it already.
```groovy
dependencies {
    implementation 'com.github.demkom58:springram-core:0.5-SNAPSHOT'
    implementation 'org.springframework.boot:spring-boot-starter'
}
```

If you want to use Spring security methods then you should
add `springram-core` and `spring-security` dependencies.
```groovy
dependencies {
    implementation 'com.github.demkom58:springram-security:0.5-SNAPSHOT'
    implementation 'org.springframework.boot:spring-boot-starter-security'
}
```
