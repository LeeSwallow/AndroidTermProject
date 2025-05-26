import de.undercouch.gradle.tasks.download.Download
import org.gradle.kotlin.dsl.register

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("de.undercouch.download") version "5.5.0"
}

// ASSET_DIR, TEST_ASSET_DIR 프로젝트 속성 지정
extra.set("ASSET_DIR", "$projectDir/src/main/assets")
extra.set("TEST_ASSET_DIR", "$projectDir/src/androidTest/assets")

val assetDir = extra["ASSET_DIR"] as String
val testAssetDir = extra["TEST_ASSET_DIR"] as String

android {
    namespace = "com.pnu.aidbtdiary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pnu.aidbtdiary"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

tasks.register<Download>("downloadWorkVecTextClassifierModel") {
    src("https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/text_classification/android/text_classification_v2.tflite")
    dest(file("$assetDir/wordvec.tflite"))
    overwrite(false)
}

tasks.register<Download>("downloadTestWorkVecTextClassifierModel") {
    src("https://storage.googleapis.com/download.tensorflow.org/models/tflite/task_library/text_classification/android/text_classification_v2.tflite")
    dest(file("$testAssetDir/wordvec.tflite"))
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
        "downloadWorkVecTextClassifierModel",
        "downloadTestWorkVecTextClassifierModel",
        "downloadTestMobileBERTTextClassifierModel"
    )
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // tensorflow 를 위한 라이브러리
    implementation(libs.tensorflow.lite.gpu.delegate.plugin)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.task.text)

    // 외부 api 호출을 위한 라이브러리
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}