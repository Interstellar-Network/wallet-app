plugins {
    kotlin("multiplatform")
    id("com.android.library")
    // https://github.com/mozilla/rust-android-gradle
    id("org.mozilla.rust-android-gradle.rust-android")
}

kotlin {
    android()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 16
        targetSdk = 31
    }
    namespace = "gg.interstellar.wallet"

    ndkVersion = "22.1.7171670"
}

// https://github.com/mozilla/rust-android-gradle#configuration
cargo {
    module  = "./rust"
    libname = "shared-rs"
    // TODO add "arm64", but fails at linking stage?
    targets = listOf("arm")
    prebuiltToolchains = true
}

// dependsOn(tasks.named("cargoBuild")) FAILS with
// Caused by: org.gradle.api.UnknownTaskException: Task with name 'cargoBuild' not found in project ':shared'
// CHECK: println("tasks.names: ${tasks.names}")
// no task with name cargo in the list...
// probably due to the fact that this project is NOT android so "org.mozilla.rust-android-gradle.rust-android" does not add it at this point?
task<Exec>("cargoBuildExec") {
    // for some reason on windows we need "cmd /c" else
    //    Execution failed for task ':shared:cargoBuildExec'.
    //    > A problem occurred starting process 'command './gradlew''
    // cf https://docs.gradle.org/current/dsl/org.gradle.api.tasks.Exec.html
    commandLine("cmd", "/c", "gradlew", "--info", "cargoBuild")
    workingDir = rootDir
}

// https://github.com/mozilla/rust-android-gradle
tasks.whenTaskAdded {
    // TODO? https://github.com/mozilla/rust-android-gradle/issues/85
    //    if (name == "mergeDebugJniLibFolders" || name == "mergeReleaseJniLibFolders") {
    if (name == "javaPreCompileDebug" || name == "javaPreCompileRelease") {
        dependsOn(tasks.named("cargoBuildExec"))
    }
}