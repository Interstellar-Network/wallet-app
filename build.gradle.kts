buildscript {
    val compose_version by extra("1.1.1")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        // https://github.com/mozilla/rust-android-gradle
        maven{ setUrl("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        // compose_version 1.1.1: kotlin-gradle-plugin:1.6.10
        // cf https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.2.0")
        // https://github.com/mozilla/rust-android-gradle
        classpath("org.mozilla.rust-android-gradle:plugin:0.9.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}