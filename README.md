![Tests](https://github.com/googlemaps/android-v3-migration/actions/workflows/test.yml/badge.svg)
[![Discord](https://img.shields.io/discord/676948200904589322)](https://discord.gg/hYsWbmk)
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue)

Maps V3 BETA Migration Tool
===========================

A tool to assist with migrating from the deprecated [Beta version](https://developers.google.com/maps/documentation/android-sdk/v310-beta) of the Maps SDK for Android to the version distributed on Google Play services.

See the [release notes](https://developers.google.com/maps/documentation/android-sdk/releases#august_18_2021) for more context.

## Installation

_Note: This plugin is currently pending approval (See [#1](/../../issues/1))_

In your app-level `build.gradle` file, include this plugin:

```groovy
plugins {
    id 'com.google.android.libraries.mapsplatform.v3-beta-migration' version '0.1.0'
}
```

## Usage

1. Run the migration: `./gradlew mapsV3BetaMigration`
2. Build and run your app
3. Remove this plugin from your `build.gradle` file

## Support

Encounter an issue while using this library?

If you find a bug or have a feature request, please [file an issue].
Or, if you'd like to contribute, send us a [pull request] and refer to our [code of conduct].

You can also reach us on our [Discord channel].

[Discord channel]: https://discord.gg/hYsWbmk
[code of conduct]: CODE_OF_CONDUCT.md
[file an issue]: https://github.com/googlemaps/android-v3-migration/issues/new/choose
[pull request]: https://github.com/googlemaps/android-v3-migration/compare
