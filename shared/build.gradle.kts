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
        val commonMain by getting {
            dependencies {
//                implementation("com.strategyobject.substrateclient:scale:0.1.0-SNAPSHOT")
//                implementation(project(":substrate-client"))
//                implementation("com.github.strategyobject:substrate-client-java:-SNAPSHOT")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
//                implementation(project(":substrate-client:scale"))
//                implementation(project(":substrate-client:scale"))
//                implementation("com.strategyobject.substrateclient:substrate-client-java:0.1.0")
//                    implementation(project(":substrate-client"))
//                implementation(project(":substrate-client:scale"))

//                git {
//                    implementation("https://github.com/strategyobject/substrate-client-java.git") {
//                        name("substrate-client")
//                        branch("develop")
//                        projectPath("substrate-client")
//                    }
//                }
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
        minSdk = 16
        targetSdk = 31
    }
    namespace = "gg.interstellar.wallet"
}