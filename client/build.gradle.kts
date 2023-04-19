plugins {
    id("kotlin.kotlin-library-conventions")
    kotlin("plugin.serialization") version "1.8.20"
    `maven-publish`
}

val ktorVersion = "2.2.4"

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.libsql"
            artifactId = "client-kotlin"
            version = "0.1"

            from(components["java"])
        }
    }
}