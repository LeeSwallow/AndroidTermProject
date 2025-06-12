import de.undercouch.gradle.tasks.download.Download
import org.gradle.kotlin.dsl.register
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("de.undercouch.download") version "5.5.0"
    kotlin("plugin.serialization") version "2.0.21"
}

// ASSET_DIR, TEST_ASSET_DIR 프로젝트 속성 지정
extra.set("ASSET_DIR", "$projectDir/src/main/assets")
extra.set("TEST_ASSET_DIR", "$projectDir/src/androidTest/assets")

val assetDir = extra["ASSET_DIR"] as String
val testAssetDir = extra["TEST_ASSET_DIR"] as String

android {
    namespace = "com.pnu.aidbtdiary"
    compileSdk = 35

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.pnu.aidbtdiary"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(file("env.properties").inputStream())
        buildConfigField("String", "OPENAI_MODEL", "\"gpt-4.1-mini-2025-04-14\"")
        buildConfigField("String", "OPENAI_BASE_URL", "\"https://api.openai.com/v1/\"")
        buildConfigField("String", "OPENAI_API_KEY", '"' + properties.getProperty("OPENAI_API_KEY") + '"')
        buildConfigField("String", "SUPABASE_URL", '"' + properties.getProperty("SUPABASE_PROJECT_URL") + '"')
        buildConfigField("String", "SUPABASE_KEY", '"' + properties.getProperty("SUPABASE_ANON_KEY") + '"')
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}


tasks.register<Download>("downloadMobileBERTTextClassifierModel") {
    src("https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/text_classification/android/mobilebert.tflite")
    dest(file("$assetDir/mobilebert.tflite"))
    overwrite(false)
}


tasks.register<Download>("downloadTestMobileBERTTextClassifierModel") {
    src("https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/text_classification/android/mobilebert.tflite")
    dest(file("$testAssetDir/mobilebert.tflite"))
    overwrite(false)
}

// preBuild에 의존성 추가
tasks.named("preBuild") {
    dependsOn(
        "downloadMobileBERTTextClassifierModel",
        "downloadTestMobileBERTTextClassifierModel"
    )
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // 코루틴을 위한 라이브러리
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // tensorflow 를 위한 라이브러리
    implementation(libs.tensorflow.lite.gpu.delegate.plugin)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.task.text)

    // 외부 api 호출을 위한 라이브러리
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.scalars)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.translate)


    // 로컬 db를 위한 room 라이브러리
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx) // 코루틴 support

    // testing 라이브러리
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Supabase 라이브러리
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.ktor:ktor-client-okhttp:3.1.3")
}