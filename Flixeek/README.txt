-------------------------------------------------------------------
Flixeek
-------------------------------------------------------------------
Flixeek is an app that obtains a list of most popular/highest rated movies using the TMDP API. 
The movies can be sorted on basis of popularity or their rating.

The movies can also be marked as favorites by clicking the star icon in details view.
The Favorites are available offline.

The app also allows you to view the trailers and reviews for the movies.

Flixeek is part of the Udacity Android Nanodegree portfolio and corresponds to P1 and P2 Popular movies project

### UPDATED FOR JAVA 17 COMPATIBILITY (2025)
This project has been updated to work with modern development environments:
- Gradle 7.6.4 (supports Java 17+)
- Updated repositories (Google & Maven Central)
- Modern Android Gradle Plugin 7.4.2

### REQUIREMENTS

#### 1. TMDB API Key is required.
In order for the Flixeek app to function properly an API key must be included with the build.

Include the unique key for the build by adding the following line to gradle.properties:

`RegisteredTmdbApiKey="<UNIQUE_API_KEY>"`

Get your API key from: https://www.themoviedb.org/settings/api

#### 2. YouTube Developer Key is required. 
For viewing the trailers, youtube player api developer key is required. 
Once provided, the trailer will be viewed in youtube player activity view or the youtube app or in a web view, depending on configuration.

Include the unique key for the build by adding the following line to gradle.properties:

`YoutubeDeveloperKey="<UNIQUE_API_KEY>"`

Get your API key from: https://developers.google.com/youtube/android/player/setup

### BUILD INSTRUCTIONS

1. Configure API keys in `gradle.properties` (see above)
2. Build the project:
   ```
   cd Flixeek
   ./gradlew assembleDebug
   ```
3. Run on Android device/emulator with API level 16+

### DEVELOPMENT ENVIRONMENT
- Java 17+ required
- Android SDK with API level 23
- Gradle 7.6.4 (included via wrapper)

--------------------------------------------------------------------
Credits & Acknowledgments
--------------------------------------------------------------------
Flixeek Logo -  https://romannurik.github.io/AndroidAssetStudio/
Flixeek Icons - https://materialdesignicons.com/
Color Pallete - http://mcg.mbitson.com/
JsonToJava - http://www.jsonschema2pojo.org/