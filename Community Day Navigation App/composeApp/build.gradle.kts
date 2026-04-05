import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget



plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    // Google Services plugin applied conditionally at bottom
}

kotlin {
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

    jvm() {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    js(IR) {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        // Use "val ... by getting" for ALL of them to stay safe
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
                


                
                // Firebase (The new Multiplatform way)
                implementation(libs.firebase.app)
                implementation(libs.firebase.auth)
                implementation(libs.firebase.firestore)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.activity.compose)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val iosMain by getting {
            dependencies {


            }
        }

        val jvmMain by getting {
            dependencies{
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(compose.ui) // This pulls in the Skiko JS dependency
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

compose.desktop {
    application {
        mainClass = "MainKt" // This must match the file where your fun main() is

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageVersion = "1.0.0"
        }
    }
}

// Apply Google Services plugin only for Android builds
if (project.plugins.hasPlugin("com.android.application")) {
    apply(plugin = "com.google.gms.google-services")
}
