import de.undercouch.gradle.tasks.download.Download
import org.gradle.kotlin.dsl.register
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
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

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.pnu.aidbtdiary"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(file("env.properties").inputStream())

        buildConfigField("String", "OPENAI_BASE_URL", "https://api.openai.com/v1")
        buildConfigField("String", "OPENAI_API_KEY", properties.getProperty("OPENAI_API_KEY"))
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
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.scalars)
    implementation(libs.retrofit2.converter.gson)

    // 로컬 db를 위한 room 라이브러리
    val room_version = "2.7.1"
    ksp("androidx.room:room-compiler:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // 코루틴 support

    // testing 라이브러리
    androidTestImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}