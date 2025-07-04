pluginManagement {
    repositories {
        google()
        gradlePluginPortal() // ✅ Important: google() should be first
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "ToDoList"
include(":app")
