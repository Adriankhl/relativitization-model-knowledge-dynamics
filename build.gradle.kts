plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
