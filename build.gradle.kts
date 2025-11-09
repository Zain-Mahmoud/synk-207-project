plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

java {
    // Compile for Java 11 using current JDK; toolchain removed due to provisioning issues.
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    // Assuming the main class will be created under package org.example
    mainClass.set("org.example.CalendarQuickstart")
}

dependencies {
    // Google Calendar API client deps
    implementation("com.google.api-client:google-api-client:2.8.1")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.39.0")
    // Java 6+ helper for AuthorizationCodeInstalledApp
    implementation("com.google.oauth-client:google-oauth-client-java6:1.39.0")
    // Gson JSON factory used by GsonFactory
    implementation("com.google.http-client:google-http-client-gson:2.0.2")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20251028-2.0.0")

    // JUnit for tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Ensure consistent Java 11 target when using a newer host JDK
tasks.withType<JavaCompile> {
    options.release.set(17)
}

tasks.test {
    useJUnitPlatform()
}