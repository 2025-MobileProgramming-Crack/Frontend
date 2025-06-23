// 프로젝트 루트: build.gradle.kts
plugins {
    // AGP Alpha 버전 사용 (Android Studio가 지원하는 최대 버전)
    id("com.android.application") version "8.6.1" apply false
    id("com.android.library")    version "8.6.1" apply false
    // 코틀린 버전 1.9.0 -> 1.9.20
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}