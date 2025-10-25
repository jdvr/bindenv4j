plugins {
    java
    `maven-publish`
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "dev.juanvega"
version = "1.0"



java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)

    }
}



repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.engine)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.pioneer)

    testImplementation(platform(libs.assertj.bom))
    testImplementation(libs.assertj.core)
}

testing {
    suites {
        getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit)
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        withType<JavaCompile> {
            options.compilerArgs.add("-parameters")
        }
        jvmArgs(
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED"
        )
    }
}


publishing {
     repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jdvr/bindenv4j")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.juanvega"
            artifactId = "bindenv4j"
            version = "${project.version}"

            from(components["java"])
        }
    }
}

dokka {
    moduleName.set("BindEnv4J")
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
        outputDirectory.set(layout.buildDirectory.dir("docs"))
    }
    dokkaSourceSets.named("javaMain") {
        includes.from("README.md")
        sourceLink {
            localDirectory.set(file("src/main/java"))
            // https://github.com/jdvr/bindenv4j/blob/main/dev/juanvega/bindenv4j/EnvBinder.java#L40
            // https://github.com/jdvr/bindenv4j/blob/main/src/main/java/dev/juanvega/bindenv4j/EnvBinder.java
            remoteUrl("https://github.com/jdvr/bindenv4j/blob/main/src/main/java")
            remoteLineSuffix.set("#L")
        }
    }
    pluginsConfiguration.html {
        footerMessage.set("(c) Juan Vega")
    }
}
