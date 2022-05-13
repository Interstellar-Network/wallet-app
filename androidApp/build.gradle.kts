plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "gg.interstellar.wallet.android"
        // Compose Jetpack: requires >= 21?
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling-preview:${rootProject.extra["compose_version"]}")
    implementation("androidx.navigation:navigation-compose:2.4.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.activity:activity-compose:1.4.0")

    // TODO commonMain? but even there we get "Unresolved Reference"???
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-api-base-0.5.0-SNAPSHOT.jar"))
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-api-http-0.5.0-SNAPSHOT.jar"))
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-common-types-0.5.0-SNAPSHOT.jar"))
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-json-types-0.5.0-SNAPSHOT.jar"))
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-scale-0.5.0-SNAPSHOT.jar"))
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-scale-types-0.5.0-SNAPSHOT.jar"))
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-ss58-0.5.0-SNAPSHOT.jar"))
    implementation(files("${rootProject.projectDir}/shared/3rd_party/polkaj/build/libs/polkaj-tx-0.5.0-SNAPSHOT.jar"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")
    debugImplementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
}