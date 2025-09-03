# Code Migration Examples for AndroidX

This document shows practical examples of code changes needed after the Android SDK upgrade and AndroidX migration.

## Import Statement Updates

### MainActivity Example
**Before (Support Library):**
```java
package com.flixeek.ui;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
```

**After (AndroidX):**
```java
package com.flixeek.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
```

## Common Import Mappings

| Support Library Import | AndroidX Import |
|------------------------|-----------------|
| `android.support.v7.app.AppCompatActivity` | `androidx.appcompat.app.AppCompatActivity` |
| `android.support.v4.app.Fragment` | `androidx.fragment.app.Fragment` |
| `android.support.v7.widget.RecyclerView` | `androidx.recyclerview.widget.RecyclerView` |
| `android.support.v7.widget.LinearLayoutManager` | `androidx.recyclerview.widget.LinearLayoutManager` |
| `android.support.v7.widget.Toolbar` | `androidx.appcompat.widget.Toolbar` |
| `android.support.design.widget.FloatingActionButton` | `com.google.android.material.floatingactionbutton.FloatingActionButton` |
| `android.support.design.widget.Snackbar` | `com.google.android.material.snackbar.Snackbar` |
| `android.support.v4.content.ContextCompat` | `androidx.core.content.ContextCompat` |
| `android.support.v4.app.ActivityCompat` | `androidx.core.app.ActivityCompat` |

## Files That Need Updates

Based on the project structure, these files likely need import updates:

1. **Activities:**
   - `MovieListActivity.java` - AppCompatActivity import
   - `MovieDetailActivity.java` - AppCompatActivity import
   - `SettingsActivity.java` - Preference imports
   - `YoutubePlayerViewActivity.java` - Activity imports
   - `TrailerWebViewActivity.java` - Activity imports

2. **Fragments:**
   - Any Fragment classes will need `androidx.fragment.app.Fragment`

3. **Adapters:**
   - RecyclerView adapters need `androidx.recyclerview.widget.RecyclerView`

## Automated Migration

Android Studio provides an automated migration tool:

1. Open the project in Android Studio
2. Go to **Refactor > Migrate to AndroidX**
3. Click **Do Refactor**
4. Review and accept the changes

## Manual Migration Steps

If doing manual migration:

1. **Find and Replace** in your IDE:
   - `android.support.v7.app` → `androidx.appcompat.app`
   - `android.support.v4.app` → `androidx.fragment.app`
   - `android.support.v7.widget` → `androidx.recyclerview.widget` (for RecyclerView)
   - `android.support.v7.widget` → `androidx.appcompat.widget` (for Toolbar)
   - `android.support.design` → `com.google.android.material`

2. **Update XML layouts** if they reference support library views:
   - `android.support.v7.widget.RecyclerView` → `androidx.recyclerview.widget.RecyclerView`
   - `android.support.design.widget.FloatingActionButton` → `com.google.android.material.floatingactionbutton.FloatingActionButton`

## Build Configuration Updates Already Done

✅ **gradle.properties:**
```properties
android.useAndroidX=true
android.enableJetifier=true
```

✅ **app/build.gradle dependencies:**
```gradle
implementation 'androidx.appcompat:appcompat:1.3.0'
implementation 'androidx.recyclerview:recyclerview:1.2.1'
implementation 'com.google.android.material:material:1.4.0'
// ... other AndroidX dependencies
```

## Testing After Migration

1. **Build the project:**
   ```bash
   ./gradlew clean build
   ```

2. **Run tests:**
   ```bash
   ./gradlew test
   ```

3. **Test on device/emulator:**
   - Install and run the app
   - Test all major features
   - Pay attention to UI components that may have changed behavior

## Troubleshooting Common Issues

### 1. Duplicate Class Errors
If you see duplicate class errors, ensure all dependencies use AndroidX:
- Remove any remaining support library dependencies
- Use `./gradlew dependencies` to check for conflicts

### 2. Missing Classes
If classes are missing after migration:
- Check the AndroidX mapping guide
- Ensure correct AndroidX dependencies are included

### 3. UI Issues
Some Material Design components may have slightly different behavior:
- Test all UI interactions
- Update theme configurations if needed

This migration brings the codebase to modern Android development standards and ensures compatibility with current Android versions.