import org.jetbrains.kotlin.gradle.dsl.JvmTarget

composeCompiler {
    featureFlags.add(org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag.StrongSkipping)
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    //alias(libs.plugins.composeCompiler)
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
    //alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    // Google Services plugin applied conditionally at bottom
}

kotlin {
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    // Add this line to create shared iOS source set automatically
    applyDefaultHierarchyTemplate()
    
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS targets - add back when ready
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
       // compilerOptions {
        //    target.set("es6")
        //}
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose & Lifecycle (Clean and readable!)
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.material)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.kotlinx.serialization.json)

                // Firebase (The Multiplatform way)
                implementation(libs.firebase.app)
                implementation(libs.firebase.auth)
                implementation(libs.firebase.firestore)
                implementation(libs.kotlinx.datetime)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.activity.compose) // change to 1.9.3?
                implementation(libs.play.services.location)
                implementation(libs.algolia.client)
                implementation(libs.firebase.auth.android)
                //implementation(libs.firebase.common.android)
                implementation(libs.ktor.client.okhttp)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.algolia.client)
                implementation(libs.ktor.client.darwin)
                implementation(libs.ktor.client.core)
            }
        }

        val jsMain by getting {
            dependencies {
               // implementation(compose.ui)

                // Use a BOM inside a KMP SourceSet
                //implementation(project.dependencies.platform("com.algolia:algoliasearch-client-kotlin-bom:3.38.1"))
                // Now you can declare the library without a version,
                // and the BOM will force it to the correct JS-compatible variant.
               // implementation(libs.ktor.client.js)
               // implementation(libs.firebase.app)
                implementation(libs.ktor.client.js)

                // Also add this for the JSON parsing
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                // Add these npm dependencies to provide the polyfills
                implementation(npm("browserify-zlib", "0.2.0"))
                implementation(npm("stream-browserify", "3.0.0"))
                implementation(npm("buffer", "6.0.3"))
                implementation(npm("process", "0.11.10"))
                implementation(npm("util", "0.12.5"))
            }
        }
    }
}

android {
    namespace = "org.communityday.navigation.events"  // Your actual package
    compileSdk = 35 

    defaultConfig {
        applicationId = "org.communityday.navigation.events"
        minSdk = 24
        targetSdk = 35
    }

    // ADD THIS BLOCK BELOW
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            // 1. Algolia logic
            if (requested.group == "com.algolia") {
                useVersion(if (requested.name.contains("instantsearch")) "4.0.0" else "3.37.0")
            }

            // 2. REFINED Kill Switch: Only force the core Kotlin language/compiler
            // We use .group == "org.jetbrains.kotlin" to avoid hitting "org.jetbrains.kotlinx"
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion("2.2.0")
            }

            // 3. Force Kotlinx libraries to their ACTUAL versions
            if (requested.group == "org.jetbrains.kotlinx") {
                if (requested.name.contains("serialization")) {
                    useVersion("1.8.0")
                }
                if (requested.name.contains("coroutines")) {
                    useVersion("1.10.1") // Or 1.10.2 based on your catalog
                }
            }
        }
    }

    if (!name.contains("android", ignoreCase = true)) {
        exclude(group = "com.google.firebase", module = "firebase-common-ktx")
        exclude(group = "com.google.firebase", module = "firebase-auth-ktx")
        exclude(group = "com.google.firebase", module = "firebase-firestore-ktx")
    }
}

// Apply Google Services plugin only for Android builds
    if (project.plugins.hasPlugin("com.android.application")) {
        apply(plugin = "com.google.gms.google-services")
    }

