// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("com.google.gms.google-services") version "4.3.14" apply false
    // Apply other plugins as needed
}

buildscript {
    dependencies {
        // Correct syntax for adding classpath in Kotlin DSL
        classpath("com.google.gms:google-services:4.4.1") // Verify the version is up-to-date
        // Include other classpath dependencies as needed
    }
}

allprojects {
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
