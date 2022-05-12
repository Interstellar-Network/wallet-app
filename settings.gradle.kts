pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

//plugins {
//    id("com.alexvasilkov.git-dependencies") version "2.0.4"
//}

rootProject.name = "InterstellarWallet"
include(":androidApp")
include(":shared")

// NOTE: COMPILING FROM SOURCE REQUIRES CARGO else
// "A problem occurred starting process 'command 'cargo''"
//include(":shared:3rd_party:substrate-client-java")
// include one project whose project dir does not match the logical project path
//include("substrate-client:scale")
//project(":substrate-client:scale").projectDir = file("shared/3rd_party/substrate-client-java/scale")

//include("substrate-client:common")
//project(":substrate-client:common").projectDir = file("shared/3rd_party/substrate-client-java/common")

//include("substrate-client")
//project(":substrate-client").projectDir = file("shared/3rd_party/substrate-client-java")

// > Project with path ':common' could not be found in project ':substrate-client-scale'.
//include("substrate-client-scale")
//project(":substrate-client-scale").projectDir = file("shared/3rd_party/substrate-client-java/scale")

//include("substrate-client:common")
//project(":substrate-client:common").projectDir = file("shared/3rd_party/substrate-client-java/common")

//sourceControl {
//    // A real life example would use a remote git repository
//    gitRepository(uri("https://github.com/strategyobject/substrate-client-java.git")) {
//        producesModule("com.strategyobject.substrateclient:substrate-client-java")
//    }
//}
