import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    jacoco
    id("com.github.ben-manes.versions")
}

val applicationName = "DmsExplorer"
val versionMajor = 0
val versionMinor = 7
val versionPatch = 59

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "net.mm2d.dmsexplorer"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
        base.archivesName.set("${applicationName}-${versionName}")
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "d"
            isTestCoverageEnabled = true
        }
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName = "${applicationName}-${versionName}.apk"
            }
        }
    }
    lintOptions {
        isAbortOnError = false
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.browser:browser:1.3.0")
    implementation("androidx.exifinterface:exifinterface:1.3.2")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.android.play:core:1.10.0")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("net.mm2d.mmupnp:mmupnp:3.1.3")
    implementation("net.mm2d.preference:preference:0.2.7")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("org.robolectric:robolectric:4.6")
    testImplementation("androidx.test.ext:junit:1.1.3")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    debugImplementation("com.facebook.flipper:flipper:0.96.1")
    debugImplementation("com.facebook.soloader:soloader:0.10.1")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:0.96.1")
    debugImplementation("com.facebook.flipper:flipper-leakcanary2-plugin:0.96.1")
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.create<JacocoReport>("jacocoTestReport") {
    group = "verification"
    dependsOn("testDebugUnitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    sourceDirectories.setFrom("${projectDir}/src/main/java")
    classDirectories.setFrom(fileTree("${buildDir}/tmp/kotlin-classes/debug"))
    executionData.setFrom("${buildDir}/jacoco/testDebugUnitTest.exec")
}

fun isStable(version: String): Boolean {
    val versionUpperCase = version.toUpperCase()
    val hasStableKeyword = listOf("RELEASE", "FINAL", "GA").any { versionUpperCase.contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return hasStableKeyword || regex.matches(version)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf { !isStable(candidate.version) }
}
