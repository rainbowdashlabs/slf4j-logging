import org.gradle.internal.impldep.org.apache.commons.lang.CharEncoding

plugins {
    java
    `java-library`
    `maven-publish`
    id("de.chojo.publishdata") version "1.0.9"
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "de.chojo"
version = "1.0.1"

repositories {
    mavenCentral()
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}

publishData {
    useEldoNexusRepos(true)
    publishComponent("java")
}

dependencies {
    api("org.slf4j", "slf4j-api", "1.7.36")
    api("org.apache.logging.log4j", "log4j-core", "2.19.0")
    api("org.apache.logging.log4j", "log4j-slf4j-impl", "2.17.2")
    api("club.minnced", "discord-webhooks", "0.8.2")
    api("org.apache.commons", "commons-text", "1.9")
}

publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            name = "EldoNexus"
            setUrl(publishData.getRepository())
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain{
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    compileJava {
        options.encoding = CharEncoding.UTF_8
    }

    javadoc {
        val options = options as StandardJavadocDocletOptions
        options.encoding = CharEncoding.UTF_8
        options.links(
            "https://ci.dv8tion.net/job/JDA/javadoc/",
            "https://javadoc.io/doc/io.javalin/javalin/latest/",
            "https://javadoc.io/doc/com.google.guava/guava/latest/"
        )
    }
}
