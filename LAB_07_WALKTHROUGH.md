# Lab 7 walkthrough: navigation, races, and drivers

This walkthrough follows `lab-07-description.md` and completes its participation exercise in the `lab07` Android Studio project. The finished source is in `lab-7/` beside this guide. The package is `com.example.lab_07`; use your project's actual package if Android Studio generated a different one. The exercise keeps its lists only while their screens remain in composition, which is allowed because persistence is optional.

## 1. Start from the right project and sync Gradle

Open `lab-7/` in Android Studio, or apply these steps to an **Empty Activity** project made with **Kotlin**. The project folder may be called `lab07`, `lab-7`, or something else; the Kotlin `package` line, Android `namespace`, and test package must agree. Do not confuse the outer `lab-07-f26` repository with the Android project inside it.

In `app/build.gradle.kts`, add this inside `dependencies { ... }`:

```kotlin
implementation("androidx.navigation:navigation-compose:2.9.8")
```

Then select **Sync Project with Gradle Files**. This project uses the Compose dependencies and test dependencies supplied by its Empty Activity template. If `navigation-compose` shows as unresolved, check that the dependency is in the **app** module, that `google()` and `mavenCentral()` are configured in `settings.gradle.kts`, and that Gradle is not in offline mode when the version has not been downloaded. A missing network connection can stop sync before any Kotlin code is compiled. The lab specifies 2.9.8; [Android's Navigation release notes](https://developer.android.com/jetpack/androidx/releases/navigation) confirm that version exists.

This particular template initially mixed `compileSdk` 36 and Android Gradle Plugin 9.0.1 with `core-ktx` 1.19.1 and Lifecycle 2.11.0. Gradle rejected that combination because those libraries require API 37 and AGP 9.1 or later. In `gradle/libs.versions.toml`, I set `coreKtx = "1.18.0"` and `lifecycleRuntimeKtx = "2.10.0"` to keep the project's installed SDK 36 and AGP 9.0.1. If your template has a different set of versions, follow the specific AAR metadata error rather than copying these numbers blindly.

## 2. Make the activity display the navigation host

Replace the template's `Greeting`, `GreetingPreview`, and `Scaffold` content in `MainActivity.kt`. Inside the existing `setContent` and `Lab07Theme` calls, display `FormulaZeroApp()`:

```kotlin
setContent {
    Lab07Theme {
        FormulaZeroApp()
    }
}
```

The handout calls its theme `Lab7Theme`, while this Empty Activity template generated `Lab07Theme`. Keep the name that actually exists in `ui/theme/Theme.kt`; a mismatch produces an unresolved-reference error. Removing the template `Scaffold` also avoids carrying unused imports and inset padding into the new screens.

## 3. Register the three destinations

Create `FormulaZeroApp.kt` in the same package as `MainActivity.kt`. `rememberNavController()` holds the navigation controller; `NavHost` starts at `home`; each `composable` maps a route string to a screen:

```kotlin
@Composable
fun FormulaZeroApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("races") { UpcomingRacesScreen(navController) }
    }
}
```

Import `Composable` from `androidx.compose.runtime` and the three navigation functions from `androidx.navigation.compose`. Route strings must match exactly. A screen function with a `NavController` parameter also has to receive the controller in its destination call.

## 4. Build the home and races screens from the walkthrough

Create `HomeScreen.kt` with a centered `Column`, a `FormulaZero Home` title, and the walkthrough's **Upcoming Races** button. The button calls `navController.navigate("races")` and has `Modifier.testTag("raceButton")`.

Create `UpcomingRacesScreen.kt` with a Back button, title, race input, Add button, and `LazyColumn`. The Back button calls `navController.popBackStack()`. Keep the handout's test tags: `backButton`, `locationInput`, and `addRaceButton`. The input uses `var location by remember { mutableStateOf("") }`; the list uses `val races = remember { mutableStateListOf<String>() }`. Import `getValue` and `setValue` for the delegated `var`, plus `androidx.compose.foundation.lazy.items` for `items(races)`.

In both input rows, give the `TextField` `Modifier.weight(1f)` and place a `Spacer(width = 8.dp)` before Add. The handout uses a full width `TextField` alongside a button and a `height` spacer in a horizontal `Row`; that can crowd or clip controls on a narrow emulator. Use `width` for horizontal spacing. On Add, trim the text, ignore an empty result, append the name or location to the observable list, and clear the input. `mutableStateListOf` causes the `LazyColumn` to update when the list changes; a plain mutable list would not reliably trigger recomposition.

Run the app now. Confirm that **Upcoming Races** opens, a location appears after Add, and Back returns home. If Android Studio marks `Column`, `LazyColumn`, `TextField`, `Modifier.testTag`, `dp`, or `sp` as unresolved, use its import action and check that the import comes from Compose, not a similarly named Android View class. The supplied project files contain the complete imports.

> **Setup walkthrough ends here.** The home-to-races flow is complete. The remaining steps implement and test the Lab 7 participation exercise.

## Participation exercise starts here

## 5. Add the Drivers screen

First, register the new destination inside the existing `NavHost` in `FormulaZeroApp.kt`:

```kotlin
composable("drivers") { DriversScreen(navController) }
```

Add a second button to `HomeScreen.kt` that opens that route:

```kotlin
Button(
    onClick = { navController.navigate("drivers") },
    modifier = Modifier.testTag("driversButton")
) {
    Text("Drivers")
}
```

The `"drivers"` route must match in the host and the button's `navigate` call. A spelling mismatch causes a navigation error when tapped.

Then create `DriversScreen.kt` in `app/src/main/java/com/example/lab_07/`. It mirrors the races screen, with separate driver state:

```kotlin
var driverName by remember { mutableStateOf("") }
val drivers = remember { mutableStateListOf<String>() }
```

Inside its `Column`, put a Back button tagged `driversBackButton`, the title `FormulaZero Drivers`, and a `Row` containing a `TextField` tagged `driverInput` and an Add button tagged `addDriverButton`. Give the field `Modifier.weight(1f)`. The Add handler uses the trimmed input:

```kotlin
val name = driverName.trim()
if (name.isNotEmpty()) {
    drivers.add(name)
    driverName = ""
}
```

Below the row, add a `Clear all drivers` button tagged `clearDriversButton` whose click handler calls `drivers.clear()`. Render entries with `LazyColumn { items(drivers) { driver -> Text(driver) } }`. The state list makes both Add and Clear visible immediately. Calling `clear()` on the input string alone would leave list entries on screen; clearing an ordinary list might leave stale UI. Back uses `popBackStack()`, which returns to the existing home destination rather than adding another home destination to the stack.

Because `remember` holds screen-local state, navigating away and returning can start with an empty list, and process death will erase it. That meets this exercise's optional persistence rule. Use saved state or a ViewModel only if you decide to extend the lab beyond the requested behavior.

## 6. Verify the participation flow with a UI test

Put `FormulaZeroTest.kt` under `app/src/androidTest/java/com/example/lab_07/`, **not** `src/test`. `androidTest` runs on an emulator or device; a local JVM test cannot drive the activity's Compose UI. The class uses `@RunWith(AndroidJUnit4::class)`, `@get:Rule`, and `createAndroidComposeRule<MainActivity>()`. Keep the Compose UI test dependency in `androidTestImplementation` and `androidx.compose.ui:ui-test-manifest` in `debugImplementation`, as in the generated project.

The participation test follows the exact user path:

```kotlin
composeTestRule.onNodeWithTag("driversButton").performClick()
composeTestRule.onNodeWithText("FormulaZero Drivers").assertExists()
composeTestRule.onNodeWithTag("driverInput").performTextInput("Alex Morgan")
composeTestRule.onNodeWithTag("addDriverButton").performClick()
composeTestRule.onNodeWithText("Alex Morgan").assertExists()
composeTestRule.onNodeWithTag("clearDriversButton").performClick()
composeTestRule.onNodeWithText("Alex Morgan").assertDoesNotExist()
composeTestRule.onNodeWithTag("driversBackButton").performClick()
composeTestRule.onNodeWithText("FormulaZero Home").assertExists()
```

The same test class also covers the walkthrough's race flow. Use distinct test tags for the two Back buttons so a test cannot accidentally click the wrong control. The `assertDoesNotExist()` check is what proves Clear changed the rendered list; checking only that the Clear button was clickable would miss a broken handler. If an assertion finds no node, compare the tag or text character for character with the screen code and make sure the app has reached the expected destination.

Run `FormulaZeroTest` with an emulator or connected device. A Gradle build can compile the test APK, but it does not execute instrumented tests. If the IDE reports “no connected devices,” start an emulator in Device Manager or attach a device with USB debugging. If the build complains about the test runner, check `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"` in `defaultConfig`.

## 7. Check the finished project

The key files are `MainActivity.kt`, `FormulaZeroApp.kt`, `HomeScreen.kt`, `UpcomingRacesScreen.kt`, `DriversScreen.kt`, and `FormulaZeroTest.kt`. The first enters Compose, the second owns navigation, the next three draw the screens and manage their temporary input/list state, and the last checks navigation, add, clear, and return. The `app/build.gradle.kts` dependency lets the navigation imports resolve.

From the project root, `gradlew.bat assembleDebug assembleDebugAndroidTest` compiles the app and the UI test. To execute the test on a running emulator or device, run `gradlew.bat connectedDebugAndroidTest` or use Android Studio's **Run FormulaZeroTest** action. After changing Gradle dependencies, sync before treating red imports as code errors. If the Java runtime is too old for Gradle, select Android Studio's bundled JBR as the Gradle JDK in Settings.

For this implementation, `assembleDebug assembleDebugAndroidTest --offline` completed successfully after the dependencies were downloaded. An emulator was not available long enough to execute `connectedDebugAndroidTest`, so the UI assertions are written and compiled but still need a device run.

Before submission, fill in your own name and CCID in `README.md`, list any resources and collaborators honestly, update `LICENSE.md` as the handout requires, and follow its repository and Canvas instructions. Those identity fields are intentionally left for the student.
