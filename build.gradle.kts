buildscript {
    // compose_version MUST MATCH kotlin-gradle-plugin
    val composeVersion by extra("1.5.2")
    val composeUiVersion by extra("1.4.3")
    val composeMaterialVersion by extra("1.5.0")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        // kotlin-gradle-plugin MUST MATCH compose_version
        // cf https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.android.tools.build:gradle:8.1.1")
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