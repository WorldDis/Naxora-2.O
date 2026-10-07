pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
}

rootProject.name = "Nexora"
include(":app")