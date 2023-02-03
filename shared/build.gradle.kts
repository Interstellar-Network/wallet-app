// Will be deprecated in Grable 8, but there is no public replacement...
import groovy.lang.Closure
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

plugins {
    kotlin("multiplatform")
    id("com.android.library")
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
        val androidMain by getting {
            dependencies {
                api("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
                api("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
            }
        }
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
        // Compose Jetpack: requires >= 21?
        minSdk = 21
        targetSdk = 31

        // TODO? used by our own CargoTask NOT by externalNativeBuild
        ndk {
            abiFilters += listOf("x86_64", "armeabi-v7a", "arm64-v8a")
        }
    }
    namespace = "gg.interstellar.wallet"

    // MUST match the version installed with SDK Manager
    ndkVersion = "25.1.8937393"

    flavorDimensions += listOf("abi")
    productFlavors {
        create("armv7") {
            // Assigns this product flavor to the "mode" flavor dimension.
            dimension = "abi"
            ndk {
                abiFilters += listOf("armeabi-v7a")
            }
        }

        create("arm64") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("arm64-v8a")
            }
        }

        create("x86_64") {
            dimension = "abi"
            ndk {
                abiFilters += listOf("x86_64")
            }

            // Currently the emulator does not fully support Vulkan??
            // So we use a wrap.sh to force OpenGL backend
            packagingOptions {
                jniLibs.useLegacyPackaging = true
            }
        }
    }

}



/**
 * First try was using https://github.com/mozilla/rust-android-gradle
 * BUT:
 * - dependsOn(tasks.named("cargoBuild")) FAILS with
 *   Caused by: org.gradle.api.UnknownTaskException: Task with name 'cargoBuild' not found in project ':shared'
 *   Which required to call cargoBuild using Exec to have "proper" dependency
 * - this plugin is NOT supported for other plateform than Android(logical) so we still need to call "cargo" manually for iOs
 *
 * ARCHIVE/HISTORY:
 * https://github.com/mozilla/rust-android-gradle/issues/91#issuecomment-1114916433
 * cf https://github.com/briansmith/ring/blob/main/mk/cargo.sh#L69
 * and https://github.com/briansmith/ring/issues/1488
 * and https://github.com/briansmith/ring/issues/1206
 * - had to copy paste https://github.com/mozilla/rust-android-gradle/blob/master/plugin/src/main/kotlin/com/nishtahir/CargoBuildTask.kt#L176
 * - had to set:
 *  spec.environment("CC_${toolchain.target}", target_cc)
 *  spec.environment("AR_${toolchain.target}", target_ar)
 *
 *
// TODO? CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_LINKER etc
// WARNING: if you change any env var, CLEAN EVERYTHING: cargo clean + gradle clean
// else you may have compilation error when it would work on a clean build
 */
// DO NOT use a base DefaultTask, else calling exec {} in TaskAction fails:
// Caused by: org.gradle.api.tasks.TaskInstantiationException: Could not create task of type 'CargoTask'.
// Caused by: java.lang.IllegalArgumentException: The constructor for type Build_gradle.CargoTask should be annotated with @Inject.
// FAIL: can not configure the args/executable etc via doFirst: https://discuss.gradle.org/t/dofirst-does-not-execute-first-but-only-after-task-execution/28129/5
// BUT "project.exec" DOES WORK
abstract class CargoTask : DefaultTask () {

    // cargo target:
    // - x86_64-apple-ios(SIMULATOR),aarch64-apple-ios
    // - armv7-linux-androideabi,aarch64-linux-android,x86_64-linux-android(SIMULATOR)
    // MUST have been added with eg: `rustup target add $TARGET [--toolchain nightly]`
    @get:Input
    abstract val target: Property<String>

    // will default to "target/"; RELATIVE to project_dir
//    @Input
//    var target_dir = "./target"
    @get:Input
    abstract val target_dir: Property<String>

    @get:Input
    abstract val project_dir: Property<File>

    @get:Input
    abstract val use_nightly: Property<Boolean>

    // comma separated list of features; will be passed directly as-is: --features=${features}
    @get:Input
    abstract val features: Property<String>

    // cargo target -> NDK ABI(ie /.../Sdk/ndk/24.0.8215888/toolchains/llvm/prebuilt/linux-x86_64/bin/$NDk_ABI31-clang
    @get:Input
    abstract val map_cargo_target_to_ndk: MapProperty<String,String>

    // cargo target -> ndkFilters(eg "x86_64", "armeabi-v7a", "arm64-v8a")
    // "android_abi" SHOULD match jniLibs/
    @get:Input
    abstract val map_cargo_target_to_android_abi: MapProperty<String,String>

    init {
        target_dir.convention("./target")
        // https://github.com/mozilla/rust-android-gradle/blob/master/plugin/src/main/kotlin/com/nishtahir/RustAndroidPlugin.kt
        // except armv7-linux-androideabi, those should be the identity function
        // armv7-linux-androideabi -> armv7a-linux-androideabi
        map_cargo_target_to_ndk.convention(mapOf(
            "armv7-linux-androideabi" to "armv7a-linux-androideabi",
            "aarch64-linux-android" to "aarch64-linux-android",
            "x86_64-linux-android" to "x86_64-linux-android"
        ))
        map_cargo_target_to_android_abi.convention(mapOf(
            "armv7-linux-androideabi" to "armeabi-v7a",
            "aarch64-linux-android" to "arm64-v8a",
            "x86_64-linux-android" to "x86_64"
        ))
    }

    @TaskAction
    fun doWork() {
        // Set a bunch of env vars needed for cross-compiling C++ projects
        // This is needed at least for secp256k1-sys, and possibly other crates
        // https://github.com/rust-lang/cc-rs#external-configuration-via-environment-variables
        println("### android.ndkDirectory: ${project.android.ndkDirectory}")

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

        // TODO? https://github.com/rust-lang/cc-rs/blob/f2e1b1c9ff92ad063957382ec445bc54e9570c71/src/lib.rs#L2296
        // just call clang --target=$TARGET instead of this messy env var settings?
        // FAIL: it works for MOST projects in Rust; ie all that use "rust-cc"
        // BUT sdl2-sys DOES NOT, and using "NDK clang" instead of the versionned one breaks detection:
        //        -- ANDROID_PLATFORM not set. Defaulting to minimum supported version 19.
        //        -- Android: Targeting API '19' with architecture 'arm', ABI 'armeabi-v7a', and processor 'armv7-a'
        //        -- Android: Selected unified Clang toolchain
        // TODO investigate, sdl2-sys seems to build-dep on cmake-rs, which means it should work
        // cf https://github.com/rust-lang/cmake-rs/issues/140 and if it works finda cleaner way
        val ndk_major = VersionNumber.parse(project.android.ndkVersion!!).major
        println("### ndk major: ${ndk_major}")
        val cargo_target = target.get()
        val ndk_target = map_cargo_target_to_ndk.get().get(cargo_target)
        println("### cargo_target: $cargo_target")
        println("### ndk_target: $ndk_target")
        val toolchains_prebuilt_path = "${project.android.ndkDirectory}/toolchains/llvm/prebuilt/$hostTag/bin"
        // eg with "--target=x86_64-linux-android" we want:
        // CC eg "/.../Sdk/ndk/24.0.8215888/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android31-clang"
        // CXX eg "/.../Sdk/ndk/24.0.8215888/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android31-clang++"
        // BUT AR is NOT versionned:
        // eg "/.../Sdk/ndk/24.0.8215888/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-ar"
        val target_cc = "${toolchains_prebuilt_path}/$ndk_target${project.android.compileSdk!!}-clang"
//        val target_cc = "${toolchains_prebuilt_path}/clang"
        val target_cxx = "${target_cc}++"
        val target_ar = "${toolchains_prebuilt_path}/llvm-ar"
        println("### target_cc: $target_cc")
        println("### target_cxx: $target_cxx")
        println("### target_ar: $target_ar")

        project.exec {
            workingDir = project_dir.get()
            println("### workingDir: $workingDir")

            // TODO? use args and executable? but that result in empty commandLine?
            val cmd = mutableListOf<String>("cargo")
            // toolchain: stable or +nightly?
            if (use_nightly.get()) {
                cmd.add("+nightly")
            }
            cmd.add("build")
            // TODO add "--release" based on CONFIGURATION env var?(adjust outputs if needed)
            // NOTE: breakpoints in Rust do not work(historically at least) so not sure if debug build is worth it?
            cmd.add("--release")
            cmd.add("--target=$cargo_target")
            if (features.get().isNotEmpty()) {
                cmd.add("--features=${features.get()}")
            }

            println("### cmd: $cmd")
            commandLine(cmd)

            // TODO? or move up top?
            //        inputs.files(fileTree("./rust/src"))
            //            .withPropertyName("sourceFiles")
            //            .withPathSensitivity(PathSensitivity.RELATIVE)
            //        outputs.files(File("./shared/rust/target/x86_64-apple-ios/debug/libshared_substrate_client.a"))

            // TODO? CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_LINKER etc
            // WARNING: if you change any env var, CLEAN EVERYTHING: cargo clean + gradle clean
            // else you may have compilation error when it would work on a clean build
             environment("CC_$cargo_target", target_cc)
             environment("CXX_$cargo_target", target_cxx)
//            environment("TARGET_CC", target_cc)

            environment("AR_$cargo_target", target_ar)
            // TODO? spec.environment("SDL2_TOOLCHAIN", "${android.ndkDirectory}/build/cmake/android.toolchain.cmake")
            // else:
            //        -- SDL2 was configured with the following options:
            //        --
            //        -- Platform: Linux-5.10.102.1-microsoft-standard-WSL2
            //        -- 64-bit:   TRUE
            //        -- Compiler: /home/pratn/Android/Sdk/ndk/24.0.8215888/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android31-clang
            //        -- Revision
            //        /usr/include/stdint.h:26:10: fatal error: 'bits/libc-header-start.h' file not found
            //
            // cf https://github.com/Rust-SDL2/rust-sdl2/blob/541f49af58fb55b678d0b209fc354a13a54318dd/sdl2-sys/build.rs#L94
            // But we might as well set the proper env var for all CMake projects
            environment("CMAKE_TOOLCHAIN_FILE", "${project.android.ndkDirectory}/build/cmake/android.toolchain.cmake")

            // that plays a part in cmake-rs detection of NDK builds
            // cf https://github.com/rust-lang/cmake-rs/blob/master/src/lib.rs#L401 "fn uses_android_ndk"
            //            -- ANDROID_PLATFORM not set. Defaulting to minimum supported version 19.
            //            -- Android: Targeting API '19' with architecture 'arm', ABI 'armeabi-v7a', and processor 'armv7-a'
            //            ...
            //            ld: error: cannot open crtbegin_dynamic.o: No such file or directory
            //            ld: error: cannot open crtend_android.o: No such file or directory
            environment("ANDROID_ABI", map_cargo_target_to_android_abi.get().get(target.get())!!)

            environment("ANDROID_PLATFORM", project.android.defaultConfig.minSdk)

            // https://github.com/mozilla/rust-android-gradle/blob/4fba4b9db16d56ba4e4f9aef2c028a4c2d6a9126/plugin/src/main/kotlin/com/nishtahir/CargoBuildTask.kt#L195
            // else:
            // "error: linking with `cc` failed: exit status: "
            // "/usr/bin/ld: cannot find -llog"
            //
            // https://doc.rust-lang.org/cargo/reference/environment-variables.html#configuration-environment-variables
            // "CARGO_TARGET_<triple>_LINKER — The linker to use, see target.<triple>.linker."
            // "The triple must be converted to uppercase and underscores."
            val target_triple_upper = cargo_target.toUpperCaseAsciiOnly().replace("-", "_")
            println("### target_triple_upper: $target_triple_upper")
            // TODO should we use ld or lld instead of clang?
            environment("CARGO_TARGET_${target_triple_upper}_LINKER", target_cc)

            // TEMP WORKAROUND for "note: ld: error: unable to find library -lgcc"
            // that is the same "fix" than in https://github.com/rust-windowing/android-ndk-rs/pull/189
            // or the last comment of: https://github.com/rust-lang/rust/pull/85806#issuecomment-1096266946
            if(ndk_major >= 23){
                val target_dir_resolved = project_dir.get().absoluteFile.resolve("${target_dir.get()}/${cargo_target}/WORKAROUND-RUST-LANG-85806")
                println("### target_dir_resolved: $target_dir_resolved")
                project.file(target_dir_resolved).mkdirs()
                File(target_dir_resolved, "libgcc.a").writeText("INPUT(-lunwind)")

                // TODO can we use CARGO_TARGET_<triple>_RUSTFLAGS instead of -L?
                // cmd.add("-L$target_dir_resolved") // error: Found argument '-L' which wasn't expected, or isn't valid in this context
                environment("CARGO_TARGET_${target_triple_upper}_RUSTFLAGS", "-L$target_dir_resolved")
            }

            // useful to debug why we keep recompiling from scratch when switching from host build to Android target
            // TODO remove
            environment("CARGO_LOG", "cargo::core::compiler::fingerprint=info")
        }

        val output_jnilibs_dir = project.android.sourceSets["main"].jniLibs.srcDirs.elementAt(1).resolve(map_cargo_target_to_android_abi.get().get(target.get())!!)

        project.copy {
            // TODO debug/release
            from(project_dir.get().absoluteFile.resolve("${target_dir.get()}/${cargo_target}/debug/"))
            // TODO project.android or project.kotlin.
            // TODO NOTE sourceSets["main"].jniLibs.srcDirs = [/.../shared/src/main/jniLibs, /.../shared/src/androidMain/jniLibs]
            // which one should we use
            into(output_jnilibs_dir)

            include("*.so")
            // TODO dylib?
        }

        // COPY libc++_shared.so
        // cf https://android.googlesource.com/platform/ndk/+/master/docs/BuildSystemMaintainers.md#libc
        // else "java.lang.UnsatisfiedLinkError: dlopen failed: library "libc++_shared.so" not found: needed by /data/app/~~OsRL7kQNuWmABqVyljXr9Q==/gg.interstellar.wallet.android-TLjOrv0NMAhDKJ2LSBX4Fw==/lib/x86_64/librenderer.so in namespace classloader-namespace"
        // even when using "protobuf_cmake_config.define("ANDROID_STL", "c++_static");"??
        project.copy {
            from(project.android.ndkDirectory.resolve("toolchains/llvm/prebuilt/$hostTag/sysroot/usr/lib/$ndk_target/libc++_shared.so"))
            into(output_jnilibs_dir)
            include("*.so")
        }
    }

}

// https://github.com/scs/substrate-api-client only supports nightly, cf README
// BUT we use a rust-toolchain.toml file so we MUST NOT set it
// else: eg "toolchain 'nightly-x86_64-unknown-linux-gnu' is not installed"
val cargo_use_nightly = false
val cargo_project_dir = projectDir.absoluteFile.resolve("./rust")
val cargo_features_android = "with-jni"
tasks.register<CargoTask>("cargoBuildAndroidArm") {
    project_dir.set(cargo_project_dir)
    target.set("armv7-linux-androideabi")
    use_nightly.set(cargo_use_nightly)
    features.set(cargo_features_android)
}
tasks.register<CargoTask>("cargoBuildAndroidArm64") {
    project_dir.set(cargo_project_dir)
    target.set("aarch64-linux-android")
    use_nightly.set(cargo_use_nightly)
    features.set(cargo_features_android)
}
tasks.register<CargoTask>("cargoBuildAndroidX86") {
    project_dir.set(cargo_project_dir)
    target.set("x86_64-linux-android")
    use_nightly.set(cargo_use_nightly)
    features.set(cargo_features_android)
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
    outputs.files(File("./shared/rust/target/x86_64-apple-ios/debug/libshared_substrate_client.a"))

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
    outputs.files(File("./shared/rust/target/aarch64-apple-ios/debug/libshared_substrate_client.a"))

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


tasks.whenTaskAdded {
    // TODO? https://github.com/mozilla/rust-android-gradle/issues/85
    //    if (name == "mergeDebugJniLibFolders" || name == "mergeReleaseJniLibFolders") {
    // TODO is there a better target? cf // https://github.com/mozilla/rust-android-gradle
    // TODO Release variants
    if(name in arrayOf("javaPreCompileArmv7Debug", "javaPreCompileArmv7Release")) {
        dependsOn(tasks.named("cargoBuildAndroidArm"))
    }
    if(name in arrayOf("javaPreCompileArm64Debug","javaPreCompileArm64Release")) {
        dependsOn(tasks.named("cargoBuildAndroidArm64"))
    }
    if(name in arrayOf("javaPreCompileX86_64Debug", "javaPreCompileX86_64Release")) {
        dependsOn(tasks.named("cargoBuildAndroidX86"))
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
    // file or directory '/Users/XXX/Documents/workspace/interstellar/wallet-app/shared/src/iosSimulatorArm64Main/kotlin', not found
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