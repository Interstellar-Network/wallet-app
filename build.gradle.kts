buildscript {
    // compose_version MUST MATCH kotlin-gradle-plugin
    val compose_version by extra("1.3.1")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        // kotlin-gradle-plugin MUST MATCH compose_version
        // cf https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.android.tools.build:gradle:7.4.1")
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