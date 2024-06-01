plugins {
    java
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
    }
}


