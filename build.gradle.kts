plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ben.manes.versions)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor HTTP client.
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
