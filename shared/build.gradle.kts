import org.apache.tools.ant.taskdefs.condition.Os
// Will be deprecated in Grable 8, but there is no public replacement...
import org.gradle.util.VersionNumber

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
        println("### android.ndkDirectory: ${android.ndkDirectory}")
        println("### cargo.toolchainDirectory: ${toolchainDirectory}")
        // That is the same logic as: https://github.com/mozilla/rust-android-gradle/blob/master/plugin/src/main/kotlin/com/nishtahir/CargoBuildTask.kt#L176
        // NOTE: it is exposed publicly but we are NOT using the plugin correctly??
        // So toolchainDirectory = /tmp/rust-android-ndk-toolchains
        val hostTag = if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            if (Os.isArch("x86_64") || Os.isArch("amd64")) {
                "windows-x86_64"
            } else {
                "windows"
            }
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            "darwin-x86_64"
        } else {
            "linux-x86_64"
        }

        val ndk_major = VersionNumber.parse(android.ndkVersion!!).major
        val toolchains_prebuilt_path = "${android.ndkDirectory}/toolchains/llvm/prebuilt"
        // toolchain.cc() eg "bin/x86_64-linux-android31-clang"
        val target_cc = "${toolchains_prebuilt_path}/$hostTag/${toolchain.cc(android.compileSdk!!)}"
        // toolchain.ar() eg "bin/llvm-ar"
        val target_ar = "${toolchains_prebuilt_path}/$hostTag/${toolchain.ar(android.compileSdk!!, ndk_major)}"
        println("### ndk major: ${ndk_major}")
        println("### toolchain.cc: ${target_cc}")
        println("### toolchain.ar: ${target_ar}")
        println("### toolchain.target: ${toolchain.target}")
        // TODO? CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_LINKER etc
        spec.environment("CC_${toolchain.target}", target_cc)
        spec.environment("AR_${toolchain.target}", target_ar)
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

// CUSTOM task for iOs <-> cargo
// NOTE: using dependsOn is apparently not possible, cf the various tries below
// So instead those 2 tasks are called directly on the command line by XCode, right along "embedAndSignAppleFrameworkForXcode"
// and using eg:
//export PLATFORM_NAME\=iphonesimulator
//export PLATFORM_PREFERRED_ARCH\=x86_64
// We can skip build if not asked to cross-compile for the appropriate target
// LINKING: cf https://github.com/TimNN/cargo-lipo#maintenance-status for how to use the compiled Rust library in XCode
//
// TODO? if we need a custom task anyway for iOs, we can probably remove Mozilla plugin and use cargo-ndk directly?
//
// NOTE: can NOT have multiple "commandLine" in same block(the last one override the others)
// task<Exec> are never called using "dependsOn"(cf tasks.whenTaskAdded)? (but it works when using gradlew directly???)
//task<Exec>("cargoBuildIosSimulator") {
//    workingDir = projectDir.absoluteFile.resolve("./rust")
//    println("### workingDir: $workingDir")
//    // 64 bit targets (simulator):
//    commandLine("cargo", "+nightly", "build", "--target=x86_64-apple-ios")
//}
// TODO add "--release" based on CONFIGURATION env var?(adjust outputs if needed)
task("cargoBuildIosSimulator") {
    onlyIf {
        (System.getenv()["PLATFORM_NAME"] == "iphonesimulator") && (System.getenv()["PLATFORM_PREFERRED_ARCH"] == "x86_64")
    }

    inputs.files(fileTree("./rust/src"))
        .withPropertyName("sourceFiles")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.files(File("./shared/rust/target/x86_64-apple-ios/debug/libshared_rs.a"))

    doLast {
        exec {
            workingDir = projectDir.absoluteFile.resolve("./rust")
            println("### workingDir: $workingDir")
            // 64 bit targets (simulator):
            commandLine("cargo", "+nightly", "build", "--target=x86_64-apple-ios", "--features=with-cwrapper")
        }
    }
}
//task<Exec>("cargoBuildIosDevice") {
//    workingDir = projectDir.absoluteFile.resolve("./rust")
//    println("### workingDir: $workingDir")
//    // 64 bit targets (real device)
//    commandLine("cargo", "+nightly", "build", "--target=aarch64-apple-ios")
//}
task("cargoBuildIosDevice") {
    onlyIf {
        // TODO check the values with a real iPhone
        (System.getenv()["PLATFORM_NAME"] == "iphoneos") && (System.getenv()["PLATFORM_PREFERRED_ARCH"] == "arm64")
    }

    inputs.files(fileTree("./rust/src"))
        .withPropertyName("sourceFiles")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.files(File("./shared/rust/target/aarch64-apple-ios/debug/libshared_rs.a"))

    doLast {
        exec {
            workingDir = projectDir.absoluteFile.resolve("./rust")
            println("### workingDir: $workingDir")
            // 64 bit targets (real device)
            commandLine("cargo", "+nightly", "build", "--target=aarch64-apple-ios", "--features=with-cwrapper")
        }
    }
}
// TODO if needed add for "iOs simulator on ARM MACs"

// https://github.com/mozilla/rust-android-gradle
tasks.whenTaskAdded {
    // TODO? https://github.com/mozilla/rust-android-gradle/issues/85
    //    if (name == "mergeDebugJniLibFolders" || name == "mergeReleaseJniLibFolders") {
    if (name == "javaPreCompileDebug" || name == "javaPreCompileRelease") {
        /// TODO add afterEvaluate { and remove "cargoBuildExec" -> replace by "cargoBuild"
        // cf https://github.com/mozilla/rust-android-gradle/blob/master/samples/unittest/build.gradle
        // for why it is "afterEvaluate": "The `cargoBuild` task isn't available until after evaluation."
        dependsOn(tasks.named("cargoBuildExec"))
    }

    // TODO cf https://kotlinlang.org/docs/multiplatform-dsl-reference.html#targets
    // TODO? replace this by direct? https://kotlinlang.org/docs/multiplatform-configure-compilations.htmlcreate-a-custom-compilation
    // TODO? set input and outputs to fix always UP-TO-DATE https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:up_to_date_checks Example 25. Ad-hoc task
    // NOTE: get the full lists of tasks using "Build Active Architecture Only: Off"
    // and compare with "Build Active Architecture Only: On"
    // NOTE2: ios specific tasks eg embedAndSignAppleFrameworkForXcode WILL NOT appears in tasks --all
    // cf https://stackoverflow.com/a/68649989/5312991
    // so print is the only option to show them
    //  println("### tasks name: $name")
    //    SIM ONLY; Build Active Architecture Only: On
    //    > Task :shared:compileKotlinIosX64 UP-TO-DATE
    //    > Task :shared:linkDebugFrameworkIosX64 UP-TO-DATE
    //    > Task :shared:assembleDebugAppleFrameworkForXcodeIosX64 UP-TO-DATE
    //    > Task :shared:embedAndSignAppleFrameworkForXcode UP-TO-DATE
    //    ALL; Build Active Architecture Only: Off
    //    > Task :shared:compileKotlinIosSimulatorArm64
    //    > Task :shared:linkDebugFrameworkIosSimulatorArm64
    //    > Task :shared:compileKotlinIosX64 UP-TO-DATE
    //    > Task :shared:linkDebugFrameworkIosX64 UP-TO-DATE
    //    > Task :shared:assembleDebugAppleFrameworkForXcode
    //    > Task :shared:embedAndSignAppleFrameworkForXcode
    //
    //    > Task :shared:compileKotlinIosSimulatorArm64 UP-TO-DATE
    //    > Task :shared:linkDebugFrameworkIosSimulatorArm64 UP-TO-DATE
    //    > Task :shared:compileKotlinIosX64 UP-TO-DATE
    //    > Task :shared:linkDebugFrameworkIosX64 UP-TO-DATE//
    //    > Task :shared:assembleDebugAppleFrameworkForXcode
    //
    // NOTE: all the link* etc are always considered UP-TO-DATE b/c they have no file(at least right now)
    // file or directory '/Users/pratn/Documents/workspace/interstellar/wallet-app/shared/src/iosSimulatorArm64Main/kotlin', not found
    // Skipping task ':shared:compileKotlinIosSimulatorArm64' as it is up-to-date.
    // Skipping task ':shared:linkReleaseFrameworkIosSimulatorArm64' as it is up-to-date.
    // Skipping task ':shared:iosSimulatorArm64Binaries' as it has no actions.
    // FAIL: still not called
    // TODO get a device and check the proper tasks; pretty sure the "compileKotlinIosSimulatorArm64" is used for both Simulator and Device
//    if (name == "compileKotlinIosSimulatorArm64") { // FAIL
//    if (name == "embedAndSignAppleFrameworkForXcode") {
//    if (name == "compileKotlinIosSimulatorArm64") {
//    if (name == "compileKotlinIosX64") {
    if(name.contains("compileKotlinIos")){
        throw GradleException("in tasks.whenTaskAdded: never called!")
        dependsOn(tasks.named("cargoBuildIosSimulator"))
        dependsOn(tasks.named("cargoBuildIosDevice"))
    }
}