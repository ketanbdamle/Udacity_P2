# Latest Android SDK Configuration (API 34)

This file contains the most current Android SDK configuration for upgrading to the latest available version.

## For Latest Android SDK (API 34 - Android 14)

### Root build.gradle
```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.2'
        
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

### gradle-wrapper.properties
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-all.zip
```

### app/build.gradle (Latest Configuration)
```gradle
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.flixeek'
    compileSdk 34

    defaultConfig {
        applicationId "com.flixeek"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig true
    }

    buildTypes.each {
        it.buildConfigField 'String', 'TMDB_API_KEY', RegisteredTmdbApiKey
        it.buildConfigField 'String', 'YOUTUBE_DEVELOPER_KEY', YoutubeDeveloperKey
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    
    // AndroidX Core
    implementation 'androidx.core:core:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.activity:activity:1.8.2'
    implementation 'androidx.fragment:fragment:1.6.2'
    
    // UI Components
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.android.material:material:1.11.0'
    
    // Network & Data
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.picasso:picasso:2.8'
    
    // Utilities
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    implementation 'com.jakewharton:butterknife:10.2.3'
    annotationProcessor 'com.jakewharton:butterknife-compiler:10.2.3'
    
    // YouTube API (local JAR)
    implementation files('libs/YouTubeAndroidPlayerApi.jar')
}
```

### gradle.properties (Latest)
```properties
# Project-wide Gradle settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# AndroidX package structure
android.useAndroidX=true
android.enableJetifier=true

# Enable newer optimizations
android.enableR8.fullMode=true
android.enableAppBundle=true

# API Keys - Replace with your actual API keys
RegisteredTmdbApiKey="PUT_YOUR_TMDB_API_KEY_HERE"
YoutubeDeveloperKey="PUT_YOUR_YOUTUBE_API_KEY_HERE"
```

## Key Improvements in Latest Configuration

### 1. Latest SDK Versions
- **Compile SDK**: 34 (Android 14)
- **Target SDK**: 34 (Android 14)
- **Min SDK**: 24 (Android 7.0) - Modern baseline

### 2. Modern Build Configuration
- **Gradle**: 8.5 (latest)
- **Android Gradle Plugin**: 8.2.2 (latest)
- **Java**: 11 (modern standard)
- **Build Features**: Enabled BuildConfig

### 3. Latest Dependencies
All dependencies updated to their most recent stable versions as of 2024.

### 4. Modern Build Optimizations
- **R8 Full Mode**: Enabled for better code optimization
- **App Bundle**: Enabled for Google Play Store

### 5. Enhanced Testing
- Latest testing frameworks
- Espresso UI testing support

## Migration Steps to Latest

1. **Backup your current project**

2. **Update Gradle files** with the configurations above

3. **Update Java version**:
   - Install Java 11 or higher
   - Update IDE settings to use Java 11

4. **Run migration**:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

5. **Test thoroughly**:
   - All app functionality
   - On different Android versions
   - Performance and memory usage

## Android 14 (API 34) New Features You Can Use

1. **Enhanced Privacy Controls**
2. **Improved Performance**
3. **Better User Experience APIs**
4. **Advanced Security Features**
5. **Updated Material Design 3**

## Considerations for API 34

1. **Permissions**: Review new permission requirements
2. **Privacy Changes**: Update for enhanced privacy features
3. **Behavior Changes**: Test app behavior on Android 14
4. **Storage**: Ensure compatibility with latest storage access patterns
5. **Notifications**: Update notification handling if needed

This configuration represents the most modern Android development setup available as of 2024.