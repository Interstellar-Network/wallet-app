// https://stackoverflow.com/questions/60474010/read-value-from-local-properties-via-kotlin-dsl
import java.io.File
import java.io.FileInputStream
import java.util.*
val prop = Properties().apply {
    load(FileInputStream(File(rootProject.rootDir, "local.properties")))
}

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

        // TRY GH REGISTRY
        maven {
            url = uri("https://maven.pkg.github.com/strategyobject/substrate-client-java")
            // Auth required even for public packages...
            // "maven-metadata.xml'. Received status code 401 from server: Unauthorized"
            // cf https://github.community/t/how-to-allow-unauthorised-read-access-to-github-packages-maven-repository/115517/14
            //TODO readd "? ?: System.getenv("USERNAME")"
            //TODO readd "? ?: System.getenv("TOKEN")"
            credentials {
                // TODO? why does this return null? should this be passed by cmd args?
//                username = project.property("gpr.user") as String
//                password = project.property("gpr.key") as String
                username = prop.getProperty("gpr.user") as String
                password = prop.getProperty("gpr.key") as String
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}