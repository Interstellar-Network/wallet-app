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

    // MUST match the version installed with SDK Manager
    ndkVersion = "24.0.8215888"
}

// https://github.com/mozilla/rust-android-gradle#configuration
cargo {
    module  = "./rust"
    // MUST match the FINAL .so name(which is the rust package name with - replaced by _)
    // else the .so WILL NOT be moved into shared/build/rustJniLibs/android/ etc
    libname = "shared_rs"
    // TODO x86_64 only needed for the emulator? Are there any device out there in x86?
    // else: java.lang.UnsatisfiedLinkError: dlopen failed: "/data/app/~~SxnFG9dqcce7NvMj1jGM9Q==/gg.interstellar.wallet.android-FvyY5G2RhoZerZMtIpvTvA==/lib/x86_64/libshared_rs.so" is for EM_AARCH64 (183) instead of EM_X86_64 (62)
    targets = listOf("arm", "arm64", "x86_64")
    prebuiltToolchains = true
    // needed because without this it defaults to 16, and it ends up trying to call
    // /.../toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi16-clang
    // which does NOT exist
    // cf https://github.com/mozilla/rust-android-gradle/issues/66
    apiLevel = 31
    // https://github.com/scs/substrate-api-client only supports nightly, cf README
    rustupChannel = "nightly"

    features {
        defaultAnd(arrayOf("with-jni"))
    }

    // https://github.com/mozilla/rust-android-gradle/issues/91#issuecomment-1114916433
    exec = {spec, toolchain ->
        // cf https://github.com/briansmith/ring/blob/main/mk/cargo.sh#L69
        // and https://github.com/briansmith/ring/issues/1488
        // and https://github.com/briansmith/ring/issues/1206
        // eg /home/XXX/Android/Sdk/ndk/$ndkVersion
        // NOTE: below WILL NOT work on Windows!
        println("android.ndkDirectory: ${android.ndkDirectory}")
        if (toolchain.target == "x86_64-linux-android") {
            val target_cc = "${android.ndkDirectory}/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android31-clang"
            spec.environment("CC_x86_64-linux-android", target_cc)
            spec.environment("AR_x86_64-linux-android", "${android.ndkDirectory}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar")
        }
        if (toolchain.target == "armv7-linux-androideabi") {
            val target_cc = "${android.ndkDirectory}/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi31-clang"
            spec.environment("CC_armv7-linux-androideabi", target_cc)
            spec.environment("AR_armv7-linux-androideabi", "${android.ndkDirectory}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar")
        }
        if (toolchain.target == "aarch64-linux-android") {
            val target_cc = "${android.ndkDirectory}/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android31-clang"
            spec.environment("CC_aarch64-linux-android", target_cc)
            spec.environment("AR_aarch64-linux-android", "${android.ndkDirectory}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar")
            spec.environment("CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_LINKER", target_cc)
        }
    }
}

// dependsOn(tasks.named("cargoBuild")) FAILS with
// Caused by: org.gradle.api.UnknownTaskException: Task with name 'cargoBuild' not found in project ':shared'
// CHECK: println("tasks.names: ${tasks.names}")
// no task with name cargo in the list...
// probably due to the fact that this project is NOT android so "org.mozilla.rust-android-gradle.rust-android" does not add it at this point?
task<Exec>("cargoBuildExec") {
    workingDir = rootDir
    // for some reason on windows we need "cmd /c" else
    //    Execution failed for task ':shared:cargoBuildExec'.
    //    > A problem occurred starting process 'command './gradlew''
    // cf https://docs.gradle.org/current/dsl/org.gradle.api.tasks.Exec.html
    // TODO windows vs linux
//    commandLine("cmd", "/c", "gradlew", "--info", "cargoBuild")
    commandLine("./gradlew", "--info", "cargoBuild")
}

// https://github.com/mozilla/rust-android-gradle
tasks.whenTaskAdded {
    // TODO? https://github.com/mozilla/rust-android-gradle/issues/85
    //    if (name == "mergeDebugJniLibFolders" || name == "mergeReleaseJniLibFolders") {
    if (name == "javaPreCompileDebug" || name == "javaPreCompileRelease") {
        dependsOn(tasks.named("cargoBuildExec"))
    }
}