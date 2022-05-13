pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "InterstellarWallet"

// FAIL: BOTH substrate-client-java and polkaj CAN NOT be used at the same time
//# downgrade to 7.1+ b/c shared/3rd_party/polkaj: "Plugin with id 'maven' not found."
//# BUT substrate-client-java/build.gradle using "'io.freefair.lombok' version '6.1.0'" which requires gradle 7.1+
//# FAIL: BOTH substrate-client-java and polkaj CAN NOT be used at the same time
// Also also:
//> Failed to apply plugin 'com.android.internal.version-check'.
//> Minimum supported Gradle version is 7.3.3. Current version is 6.9.2. If using the gradle wrapper, try editing the distributionUrl in C:\Users\nat\Documents\workspace\interstellar\InterstellarWallet\gradle\wrapper\g
//    radle-wrapper.properties to gradle-7.3.3-all.zip
//includeBuild("./shared/3rd_party/polkaj")
//includeBuild("./shared/3rd_party/substrate-client-java")
// -> So compile the deps manually and use the .jar directly
// TODO if the libs are doing what we want them to do; fork and fix gradle version compat?

include(":androidApp")
include(":shared")