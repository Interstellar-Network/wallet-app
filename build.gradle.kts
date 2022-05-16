buildscript {
    val compose_version by extra("1.1.1")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        // compose_version 1.1.1: kotlin-gradle-plugin:1.6.10
        // cf https://developer.android.com/jetpack/androidx/releases/compose-kotlin#pre-release_kotlin_compatibility
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.2.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        // com.github.NodleCode:substrate-client-kotlin:1.0 > com.github.NodleCode:BIP39:484f9d5d588
        //   > Could not find io.github.novacrypto:ToRuntime:2019.01.27.
        // cf https://github.com/NovaCrypto/ToRuntime/tree/12100273dd123d187af30f94c182567d56235149
        maven { setUrl("https://dl.bintray.com/novacrypto/General/") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}