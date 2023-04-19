plugins {
    id("kotlin.kotlin-application-conventions")
}

dependencies {
    implementation(project(":client"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

application {
    // Define the main class for the application.
    mainClass.set("org.libsql.app.AppKt")
}
