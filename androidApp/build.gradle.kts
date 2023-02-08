plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "gg.interstellar.wallet.android"
        // Compose Jetpack: requires >= 21?
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

        // "You should specify a missingDimensionStrategy property for each
        // dimension that exists in a local dependency but not in your app."
        // TODO? missingDimensionStrategy("abi", "armeabi-v7a", "arm64", ), or "fat"?
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
            }
        }
    }
    signingConfigs {
        // https://github.com/ilharp/sign-android-release#usage
        // TODO? signing, cf CI yml
//        create("keystore") {
//            storeFile = file("/home/pratn/.keystores/android-keystores.jks")
//            storePassword = "xxQZQYvzdblrmVxx6sqDWw8q4CxUPFxy"
//            keyPassword = "xxQZQYvzdblrmVxx6sqDWw8q4CxUPFxy"
//            keyAlias = "upload"
//        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            // TODO? signing, cf CI yml
//            signingConfig = signingConfigs.getByName("keystore")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose_version"] as String
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "gg.interstellar.wallet.android"
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.compose.material:material-icons-extended:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling-preview:${rootProject.extra["compose_version"]}")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")
    debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
}