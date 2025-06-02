
# ShortLink SDK for Android (Deep Linking Integration using Short.io)

This SDK allows you to create short links using the [Short.io](https://short.io/) API based on a public API key and custom parameters. It also supports Android deep linking integration.

## ✨ Features

- Generate short links via Short.io API
- Customize short links using parameters
- Integrate Deeplinking in Android
- Simple and clean API for developers


## 📦 Installation

You can integrate the SDK into your Android Studio project using **JitPack** 

To install the SDK via JitPack:

### Step 1. To add the JitPack repository to your build file, Add it in your root settings.gradle at the end of repositories:
#### For Gradle:
```java
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
#### For Gradle.kts
```kotlin
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```
### Step 2. Add the dependency
Open App level build.gradle file `build.gradle.kts (Module:app)`, Add the dependency:
Example
```kotlin
dependencies {
		implementation("com.github.User:Repo:Tag")
	}
```
It will be:
```kotlin
dependencies {
		implementation("com.github.AsadJav:ShortIOSDK:v1.0.1")
	}
```
### Step 3. Intallation
Sync the Project with Gradle file, So the SDK can be Installed.

### Step 4. Import the SDK
Import the SDK where it is needed by using:
```kotlin
import com.github.shortio.ShortIOParametersModel
import com.github.shortio.ShortioSdk
``` 

## 🔑 Getting Started


### Step 1: Get Public API Key from Short.io

1. Visit [Short.io](https://short.io/) and **sign up** or **log in** to your account.
   
2. In the dashboard, navigate to **Integrations & API**.

3. Click **CREATE API KEY** button.

4. Enable the **Public Key** toggle.

5. Click **CREATE** to generate your API key.

### 🔗 SDK Usage

```kotlin
import com.github.shortio.ShortIOParametersModel
import com.github.shortio.ShortioSdk
import com.github.shortiosdk.ShortIOResult

try {
    val params = ShortIOParametersModel(
      domain = "your_domain", // Replace with your Short.io domain
      originalURL = "your_originalURL"// Replace with your Short.io domain
    )
} catch (e: Exception) {
    Log.e("ShortIO", "Error: ${e.message}", e)
}
```
**Note**: Both `domain` and `originalURL` are the required parameters. You can also pass optional parameters such as `path`, `title`, `utmParameters`, etc.

```kotlin
let apiKey = "your_public_apiKey" // Replace with your Short.io Public API Key

thread {
    try {
        when (val result = ShortioSdk.shortenUrl(apiKey, params)) {
            is ShortIOResult.Success -> {
                println("Shortened URL: ${result.data.shortURL}")
            }
            is ShortIOResult.Error -> {
                val error = result.data
                println("Error ${error.statusCode}: ${error.message} (code: ${error.code})")
            }
        }
    } catch (e: Exception) {
        Log.e("ShortIO", "Error: ${e.message}", e)
    }
}       
```

## 🤖 Deep Linking Setup (App Links for Android)
To handle deep links via Short.io on Android, you'll need to set up Android App Links properly using your domain's Digital Asset Links and intent filters.

### 🔧 Step 1: Configure Intent Filter in AndroidManifest.xml
1. Open your Android project.

2. Navigate to android/app/src/main/AndroidManifest.xml.

3. Inside your MainActivity, add an intent filter to handle app links:
```
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:launchMode="singleTask">
    
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        
        <data
            android:scheme="https"
            android:host="yourshortdomain.short.gy" />
    </intent-filter>
</activity>
```
✅ Tip: Replace yourshortdomain.short.gy with your actual Short.io domain.

### 🛡️ Step 2: Configure Asset Links on Short.io

1. Go to Short.io.

2. Navigate to Domain Settings > Deep links for your selected domain.

3. In the Android Package Name field, enter your app's package name (e.g., com.example.app).

4. In the SHA-256 Certificate Fingerprint field, enter your release key’s SHA-256 fingerprint.
```
// Example Package:
com.example.app

// Example SHA-256:
A1:B2:C3:D4:E5:F6:...:Z9
```
You can retrieve the fingerprint using the following command:

```
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```
📌 Note: Use the SHA-256 of your release keystore for production builds.

5. Click Save to update the Digital Asset Links.

### 🚦 Step 3: Enable the Link in "Open by Default"

1. Build and install your app on the device.

2. Go to **App Settings > Open by Default**.

3. Tap on **“Add link”** under the **Open by Default** section.

4. Add your URL and make sure to enable the checkbox for your link.

### ✅ Final Checklist

* App is signed with the correct keystore.

* The domain is verified on Short.io.
  
* The intent-filter is added in AndroidManifest.xml.
  
* App is installed from Play Store or via direct install (for testing with ADB).

Once these steps are complete, clicking a Short.io link (with your domain) will open the app directly if installed.
