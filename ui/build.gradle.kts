@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

val pkg: String = providers.gradleProperty("wireguardPackageName").get()
val ver: String = providers.gradleProperty("wireguardVersionName").get()

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    compileSdk = 34
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
    namespace = pkg
    defaultConfig {
        applicationId = pkg
        minSdk = 21
        targetSdk = 34
        versionCode = providers.gradleProperty("wireguardVersionCode").get().toInt()
        versionName = ver
        buildConfigField("int", "MIN_SDK_VERSION", minSdk.toString())
        setProperty("archivesBaseName", "wireguard-" + ver)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-android-optimize.txt")
            packaging {
                resources {
                    excludes += "DebugProbesKt.bin"
                    excludes += "kotlin-tooling-metadata.json"
                    excludes += "META-INF/*.version"
                }
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        create("googleplay") {
            initWith(getByName("release"))
            matchingFallbacks += "release"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
    lint {
        disable += "LongLogTag"
        warning += "MissingTranslation"
        warning += "ImpliedQuantity"
    }
    splits {
        abi {
            reset()
            isEnable = true
            isUniversalApk = false
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
        }
    }
}

dependencies {
    implementation(project(":tunnel"))
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.google.material)
    implementation(libs.zxing.android.embedded)
    implementation(libs.kotlinx.coroutines.android)
    coreLibraryDesugaring(libs.desugarJdkLibs)
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:unchecked")
    options.isDeprecation = true
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

android.apply {
    var keystorePwd: String? = null
    var alias: String? = null
    var pwd: String? = null
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) {
        val props = Properties()
        props.load(propsFile.inputStream())
        keystorePwd = props.getProperty("KEYSTORE_PASS")
        alias = props.getProperty("ALIAS_NAME")
        pwd = props.getProperty("ALIAS_PASS")
    }

    if (keystorePwd != null && alias != null && pwd != null) {
        signingConfigs {
            create("release") {
                storeFile = rootProject.file("release.keystore")
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
            }
        }

        buildTypes {
            val key = signingConfigs.findByName("release")
            getByName("release").signingConfig = key
        }
    }
}
