// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.libraries.mapsplatform.v3_beta_migration

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

// Task names
const val v3BetaMigrationCleanup = "mapsV3BetaMigrationCleanup"
const val v3BetaRenameTask = "mapsV3BetaMigrationRename"
const val v3BetaMigrationTaskName = "mapsV3BetaMigration"

// Package names
const val v3BetaPackageName = "com.google.android.libraries.maps"
const val gmsPackageName = "com.google.android.gms.maps"

// Maven coordinates
const val v3BetaMavenCoordinate = "${v3BetaPackageName}:maps:3.1.0-beta"
// TODO update to latest version
const val gmsMavenCoordinate = "com.google.android.gms:play-services-maps:17.0.1"
const val placesMavenCoordinate = "com.google.android.libraries.places:places:2.4.0"

/**
 * A plugin to migrate from V3 BETA usage of the Maps SDK to the version released on Google Play
 * Services.
 *
 * Usage: `./gradlew mapsV3BetaMigration`
 */
class V3BetaMigrationPlugin : Plugin<Project> {

    private val tempFolderName = "migration_out"

    private val targetSourceFiles = listOf("**/*.java", "**/*.kt", "**/*.xml")
    private val targetBuildFiles = listOf("build.gradle", "build.gradle.kts")

    private val mapsAarName = "maps-sdk-3.0.0-beta"
    private val placesAarName = "places-maps-sdk-3.1.0-beta"

    private val v3MapsAarStringGroovyRegex = aarRegex(aarName = mapsAarName)
    private val v3MapsAarStringKtsRegex = aarRegex(aarName = mapsAarName, isKts = true)
    private val v3PlacesAarStringGroovyRegex = aarRegex(aarName = placesAarName)
    private val v3PlacesAarStringKtsRegex = aarRegex(aarName = placesAarName, isKts = true)

    private val v3MapsKtx = "maps-v3-ktx"
    private val v3MapsUtilsKtx = "maps-utils-v3-ktx"
    private val v3PlacesKtx = "places-v3-ktx"
    private val mapsKtx = "maps-ktx"
    private val mapsUtilsKtx = "maps-utils-ktx"
    private val placesKtx = "places-ktx"

    private val v3VersionStringRegex = Regex(
        "${v3BetaPackageName}:maps:\\$[a-zA-Z_][a-zA-Z_0-9]*"
    )

    private fun aarRegex(aarName: String, isKts: Boolean = false): Regex {
        return Regex(
            if (!isKts) "name:('|\")$aarName('|\").*"
            else "file\\(('|\").*$aarName.aar('|\")\\)"
        )
    }

    override fun apply(project: Project) {
        project.run {
            tasks.register(v3BetaRenameTask, Copy::class.java) { task ->
                task.from(".") {
                    it.include(targetSourceFiles)
                    it.filter { line ->
                        line.replace("${v3BetaPackageName}.", "${gmsPackageName}.")
                    }
                }
                task.from(".") {
                    it.include(targetBuildFiles)

                    it.filter { line ->
                        line
                            // Handle '...maps:maps:3.1.0-beta'
                            .replace(v3BetaMavenCoordinate, gmsMavenCoordinate)

                            // Handle '...maps:$versionString'
                            .replace(v3VersionStringRegex, gmsMavenCoordinate)

                            // Handle 'name: 'maps-sdk-3.0.0-beta...
                            .replace(v3MapsAarStringGroovyRegex, "'$gmsMavenCoordinate'")

                            // Handle 'file("maps-sdk-3.0.0-beta.aar")'
                            .replace(v3MapsAarStringKtsRegex, "\"$gmsMavenCoordinate\"")

                            // Handle 'places-maps-sdk-3.1.0-beta...
                            .replace(v3PlacesAarStringGroovyRegex, "'$placesMavenCoordinate'")

                            // Handle 'file("places-maps-sdk-3.0.0-beta.aar")'
                            .replace(v3PlacesAarStringKtsRegex, "\"$placesMavenCoordinate\"")

                            // Handle 'maps-v3-ktx'
                            .replace(v3MapsKtx, mapsKtx)

                            // Handle 'maps-utils-v3-ktx'
                            .replace(v3MapsUtilsKtx, mapsUtilsKtx)

                            // Handle 'places-v3-ktx'
                            .replace(v3PlacesKtx, placesKtx)
                    }
                }
                task.into("./$tempFolderName")
            }
            tasks.register(v3BetaMigrationTaskName, Copy::class.java) {
                it.group = "Maps V3 Beta Migration"
                it.description = "Migrates Maps SDK usage from V3 BETA to Google Play Services"
                it.from("./$tempFolderName")
                it.into(".")
                it.include(targetSourceFiles + targetBuildFiles)

                it.dependsOn(v3BetaRenameTask)
                it.finalizedBy(v3BetaMigrationCleanup)
            }
            tasks.register(v3BetaMigrationCleanup, Delete::class.java) {
                it.delete("./$tempFolderName")
                it.shouldRunAfter(v3BetaMigrationTaskName)
            }
        }
    }
}