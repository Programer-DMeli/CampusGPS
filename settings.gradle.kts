pluginManagement {
    repositories {

        //imporantamos dependencias y lebrerias de 2 repositorios google y maven genral
        //una libreria en un codigo  desarrollado por personas externas que podemos reutilizar  en nuestro proyecto, y una dependencia es algo
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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CampusGPS"
include(":app")


//poner api en local properties