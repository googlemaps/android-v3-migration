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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class V3BetaMigrationPluginTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    lateinit var root: Project
    lateinit var project: Project
    lateinit var projectFolder: File

    @Before
    fun setUp() {
        root = ProjectBuilder.builder()
            .withProjectDir(tempFolder.root)
            .withName("root")
            .build()
        project = ProjectBuilder.builder()
            .withProjectDir(tempFolder.root.resolve("project"))
            .withName("project")
            .withParent(root)
            .build()
        projectFolder = tempFolder.newFolder("project")
        project.pluginManager.apply("com.google.android.libraries.mapsplatform.v3-beta-migration")
    }

    @Test
    fun `test that temp folder is deleted`() {
        val tempFile = File(projectFolder, "Test.java")
        tempFile.writeText(
            """
                // This is a comment
                import ${v3BetaPackageName}.GoogleMaps
                // This is a comment
                class Test {
                }
            """.trimIndent()
        )
        runTasks()
        assertFalse(File(projectFolder, "output").exists())
    }

    @Test
    fun `groovy - maps 3 dot 0 is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation name:'maps-sdk-3.0.0-beta', ext:'aar'
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(gmsMavenCoordinate)
        )
    }

    @Test
    fun `groovy - maps 3 dot 1 is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation 'com.google.android.libraries.maps:maps:3.1.0-beta'
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(gmsMavenCoordinate)
        )
    }

    @Test
    fun `groovy - maps version string is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation "com.google.android.libraries.maps:maps:${'$'}versionString"
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(gmsMavenCoordinate)
        )
    }

    @Test
    fun `groovy - places maps beta is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation name:'places-maps-sdk-3.1.0-beta', ext:'aar'
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(placesMavenCoordinate)
        )
    }

    @Test
    fun `kts - places maps beta is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation(file("libs/places-maps-sdk-3.1.0-beta.aar"))
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(placesMavenCoordinate)
        )
    }

    @Test
    fun `kts - maps 3 dot 0 is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation(file("libs/maps-sdk-3.0.0-beta.aar"))
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(gmsMavenCoordinate)
        )
    }

    @Test
    fun `kts - maps 3 dot 1 is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle.kts")
        buildGradle.writeText(
            """
                dependencies {
                implementation("com.google.android.libraries.maps:maps:3.1.0-beta")
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(gmsMavenCoordinate)
        )
    }

    @Test
    fun `kts - maps version string is correctly migrated`() {
        val buildGradle = File(projectFolder, "build.gradle.kts")
        buildGradle.writeText(
            """
                dependencies {
                implementation("com.google.android.libraries.maps:maps:${'$'}versionString")
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(
            buildGradle.readText().contains(gmsMavenCoordinate)
        )
    }

    @Test
    fun `unrelated package is not replaced`() {
        val tempFile = File(projectFolder, "Test.java")
        tempFile.writeText(
            """
                // This is a comment
                import com.google.android.libraries.mapsplatform.AClass
                // This is a comment
                class Test2 {
                }
            """.trimIndent()
        )
        runTasks()
        assertFalse(tempFile.readText().contains(gmsPackageName))
    }

    @Test
    fun `java files are correctly replaced`() {
        val tempFile = File(projectFolder, "Test.java")
        tempFile.writeText(
            """
                // This is a comment
                class Test {
                }
            """.trimIndent()
        )

        val tempFile2 = File(projectFolder, "Test2.java")
        tempFile2.writeText(
            """
                // This is a comment
                import ${v3BetaPackageName}.GoogleMaps
                // This is a comment
                class Test2 {
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(tempFile2.readText().contains(gmsPackageName))
    }

    @Test
    fun `kotlin files are correctly replaced`() {
        val tempFile = File(projectFolder, "Test.kt")
        tempFile.writeText(
            """
                // This is a comment
                class Test {
                }
            """.trimIndent()
        )

        val tempFile2 = File(projectFolder, "Test2.kt")
        tempFile2.writeText(
            """
                // This is a comment
                import ${v3BetaPackageName}.GoogleMaps
                // This is a comment
                class Test2 {
                }
            """.trimIndent()
        )
        runTasks()
        assertTrue(tempFile2.readText().contains(gmsPackageName))
    }

    @Test
    fun `xml files are correctly replaced`() {
        val tempFile = File(projectFolder, "test.xml")
        tempFile.writeText(
            """
                <fragment xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:id="@+id/map"
                android:name="${v3BetaPackageName}.SupportMapFragment" />
            """.trimIndent()
        )

        val tempFile2 = File(projectFolder, "test2.xml")
        tempFile2.writeText(
            """
                <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_height="match_parent"
                android:id="@+id/map" />
            """.trimIndent()
        )
        runTasks()
        assertTrue(tempFile.readText().contains(gmsPackageName))
    }

    private fun runTasks() {
        runTask(v3BetaRenameTask)
        runTask(v3BetaMigrationTaskName)
        runTask(v3BetaMigrationCleanup)
    }

    private fun runTask(taskName: String) {
        val task = project.getTasksByName(taskName, true).firstOrNull()
        assertNotNull(task)
        task?.run {
            actions.first().execute(this)
        }
    }
}