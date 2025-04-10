// Root build.gradle.kts
plugins {
    // Plugin ini diperlukan agar kotlin-android tersedia
    kotlin("android") version "1.9.22" apply false
    kotlin("kapt") version "1.9.22" apply false
    id("com.android.application") version "8.2.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
