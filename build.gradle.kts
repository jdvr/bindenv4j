plugins {
    java
    `maven-publish`
}

group = "dev.juanvega"
version = "1.0-SNAPSHOT"



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
