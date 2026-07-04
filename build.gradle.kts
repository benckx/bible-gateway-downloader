plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.shadow)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor HTTP client.
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    // HTML parsing.
    implementation(libs.jsoup)

    // PDF generation.
    implementation(libs.openhtmltopdf.core)
    implementation(libs.openhtmltopdf.pdfbox)

    // EPUB generation.
    implementation(libs.epub4j.core)

    // CLI parsing.
    implementation(libs.clikt)

    // Coroutines (runBlocking).
    implementation(libs.kotlinx.coroutines.core)

    // Interactive terminal prompts.
    implementation(libs.jline)

    // Logging backend.
    implementation(libs.slf4j.simple)

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

application {
    mainClass = "biblegatewaydownloader.AppKt"
}
