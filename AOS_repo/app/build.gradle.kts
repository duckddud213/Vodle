
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ktlintLibrary)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.tes.vodle"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.tes.vodle"
        minSdk = libs.versions.minSDK.get().toInt()
        targetSdk = libs.versions.targetSDK.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField(
            "String",
            "BASE_URL",
            "\"${properties["BASE_URL"]}\""
        )

        buildConfigField(
            "String",
            "NAVER_LOGIN_CLIENT_ID",
            "\"${properties["NAVER_LOGIN_CLIENT_ID"]}\""
        )
        buildConfigField(
            "String",
            "NAVER_LOGIN_CLIENT_SECRET",
            "\"${properties["NAVER_LOGIN_CLIENT_SECRET"]}\""
        )

        buildConfigField(
            "String",
            "NAVER_LOGIN_CLIENT_NAME",
            "\"${properties["NAVER_LOGIN_CLIENT_NAME"]}\""
        )

        buildConfigField(
            "String",
            "S3_URL",
            "\"${properties["S3_URL"]}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    configurations.implementation {
        exclude(group = "com.intellij", module = "annotations")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofitGson)

    implementation(libs.okhttp)
    implementation(libs.okhttpLogging)

    implementation("com.navercorp.nid:oauth:5.9.0")
    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.3.0")
    implementation("androidx.media3:media3-ui:1.3.0")

    implementation(project(":presentation"))
    implementation(project(":domain"))
    implementation(project(":data"))
}
