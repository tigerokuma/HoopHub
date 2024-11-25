pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Avoid conflicts
    repositories {
        google() // Ensure the 'Google' repository is declared here
        mavenCentral() // Ensure 'Maven Central' is declared here
    }
}

rootProject.name = "HoopHubSkeleton"
include(":app")
 