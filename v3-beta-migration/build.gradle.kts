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

plugins {
    id("java-gradle-plugin")
    id("kotlin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.14.0"
}

dependencies {
    compileOnly("com.android.tools.build:gradle:7.0.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.31")
    testImplementation("com.android.tools.build:gradle:7.0.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}

gradlePlugin {
    plugins {
        create(PluginInfo.name) {
            id = PluginInfo.group
            implementationClass = PluginInfo.implementationClass
            displayName = PluginInfo.displayName
            description = PluginInfo.description
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenPub") {
            group = PluginInfo.group
            artifactId = PluginInfo.artifactId
            version = PluginInfo.version

            pom {
                name.set(PluginInfo.artifactId)
                description.set(PluginInfo.description)
                url.set(PluginInfo.url)

                scm {
                    connection.set("scm:git@github.com:googlemaps/android-v3-migration.git")
                    developerConnection.set("scm:git@github.com:googlemaps/android-v3-migration.git")
                    url.set(PluginInfo.url)
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                organization {
                    name.set("Google Inc.")
                    url.set("https://developers.google.com/maps")
                }


                developers {
                    developer {
                        name.set("Google Inc.")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("build/repository")
        }
        mavenLocal()
    }
}

pluginBundle {
    website = PluginInfo.url
    vcsUrl = PluginInfo.url
    tags = listOf("kotlin", "android", "maps")
}

project(":v3-beta-migration") {
    version = PluginInfo.version
}

object PluginInfo {
    const val artifactId = "com.google.android.libraries.mapsplatform.v3-beta-migration"
    const val name = "v3BetaMigrationPlugin"
    const val group = "com.google.android.libraries.mapsplatform.v3-beta-migration"
    const val implementationClass = "com.google.android.libraries.mapsplatform.v3_beta_migration.V3BetaMigrationPlugin"
    const val displayName = "Maps SDK V3 BETA Migration Tool"
    const val description = "A Gradle plugin to migrate from Maps SDK V3 BETA to Maps SDK in Google Play Services."
    const val url = "https://github.com/googlemaps/android-v3-migration"
    const val version = "0.1.0"
}