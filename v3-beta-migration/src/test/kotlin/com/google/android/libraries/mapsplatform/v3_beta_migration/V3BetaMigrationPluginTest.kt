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
import org.junit.Assert.assertEquals
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

    private fun expectedGroovyString(quote: String = "'") =
        """
            dependencies {
            implementation $quote$gmsMavenCoordinate$quote
            }
        """.trimIndent()
    private val expectedKtsString =
        """
            dependencies {
            implementation("$gmsMavenCoordinate")
            }
        """.trimIndent()

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
        assertEquals(expectedGroovyString(), buildGradle.readText())
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
        assertEquals(expectedGroovyString(), buildGradle.readText())
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
        assertEquals(expectedGroovyString(quote = "\""), buildGradle.readText())
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
        assertEquals(
            """
                dependencies {
                implementation '$placesMavenCoordinate'
                }
            """.trimIndent(),
            buildGradle.readText()
        )
    }

    @Test
    fun `groovy - maps ktx is corrected migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                    implementation 'com.google.maps.android:maps-v3-ktx:3.2.0'
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                dependencies {
                    implementation 'com.google.maps.android:maps-ktx:3.2.0'
                }
            """.trimIndent(),
            buildGradle.readText()
        )
    }

    @Test
    fun `groovy - maps utils ktx is corrected migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                    implementation 'com.google.maps.android:maps-utils-v3-ktx:3.2.0'
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                dependencies {
                    implementation 'com.google.maps.android:maps-utils-ktx:3.2.0'
                }
            """.trimIndent(),
            buildGradle.readText()
        )
    }

    @Test
    fun `groovy - places ktx is corrected migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                    implementation 'com.google.maps.android:places-v3-ktx:1.0.0'
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                dependencies {
                    implementation 'com.google.maps.android:places-ktx:1.0.0'
                }
            """.trimIndent(),
            buildGradle.readText()
        )
    }

    @Test
    fun `kts - maps ktx is corrected migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation("com.google.maps.android:maps-v3-ktx:3.2.0")
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                dependencies {
                implementation("com.google.maps.android:maps-ktx:3.2.0")
                }
            """.trimIndent(),
            buildGradle.readText()
        )
    }

    @Test
    fun `kts - maps utils ktx is corrected migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation("com.google.maps.android:maps-utils-v3-ktx:3.2.0")
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                dependencies {
                implementation("com.google.maps.android:maps-utils-ktx:3.2.0")
                }
            """.trimIndent(),
            buildGradle.readText()
        )
    }

    @Test
    fun `kts - places ktx is corrected migrated`() {
        val buildGradle = File(projectFolder, "build.gradle")
        buildGradle.writeText(
            """
                dependencies {
                implementation("com.google.maps.android:places-v3-ktx:1.0.0")
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                dependencies {
                implementation("com.google.maps.android:places-ktx:1.0.0")
                }
            """.trimIndent(),
            buildGradle.readText()
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
        assertEquals(
            """
                dependencies {
                implementation("$placesMavenCoordinate")
                }
            """.trimIndent(),
            buildGradle.readText()
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
        assertEquals(expectedKtsString, buildGradle.readText())
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
        assertEquals(expectedKtsString, buildGradle.readText())
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
        assertEquals(expectedKtsString, buildGradle.readText())
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
        val tempFile = File(projectFolder, "Test2.java")
        tempFile.writeText(
            """
                // This is a comment
                import ${v3BetaPackageName}.GoogleMaps
                // This is a comment
                class Test2 {
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                // This is a comment
                import ${gmsPackageName}.GoogleMaps
                // This is a comment
                class Test2 {
                }
            """.trimIndent(),
            tempFile.readText()
        )
    }

    @Test
    fun `kotlin files are correctly replaced`() {
        val tempFile = File(projectFolder, "Test2.kt")
        tempFile.writeText(
            """
                // This is a comment
                import ${v3BetaPackageName}.GoogleMaps
                // This is a comment
                class Test2 {
                }
            """.trimIndent()
        )
        runTasks()
        assertEquals(
            """
                // This is a comment
                import ${gmsPackageName}.GoogleMaps
                // This is a comment
                class Test2 {
                }
            """.trimIndent(),
            tempFile.readText()
        )
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
        runTasks()
        assertEquals(
            """
                <fragment xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:id="@+id/map"
                android:name="${gmsPackageName}.SupportMapFragment" />
            """.trimIndent(),
            tempFile.readText()
        )
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