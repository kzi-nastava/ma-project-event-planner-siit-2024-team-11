import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

fun getIpAddress(): String? {
    val properties = Properties()
    val localPropertiesFile = File("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { inputStream ->
            properties.load(inputStream)
        }
    }
    return properties.getProperty("ip_addr")
}

android {
    namespace = "com.example.eventy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.eventy"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "IP_ADDR", "\""+getIpAddress()+"\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("org.osmdroid:osmdroid-android:6.1.10")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.3.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.1")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}