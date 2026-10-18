# Waypoint

Waypoint is an Android app for planning multi-city trips. Users create a trip, search for the cities they want to visit, set how many nights to spend in each, put the stops in order, and see every route on a map.

## App description

**Users:** Independent travellers planning a trip through several cities. They currently piece their plan together from notes apps, spreadsheets or saved Google Maps pins. None of these keep the order of stops, the nights per stop and the overall route together in one place.

**Core task:** Building a trip itinerary. The user creates a trip, adds cities found through online search, sets the nights for each city, and reorders the stops. The app keeps a running total of cities and nights, and draws the route on a map.

**Data:**
- `Trip`: a name, a start date and a created time.
- `Destination`: a city name, a latitude and longitude, a number of nights, and a position in the route.
- Each `Destination` belongs to one `Trip` through `tripId`. One trip has many destinations, ordered by `position`. Deleting a trip deletes its destinations.
- Two user preferences are stored in DataStore: the theme (light or dark) and the Home screen sort order (date, name or newest).

**Out of scope:**
1. Booking or pricing accommodation and transport between cities.
2. User accounts or syncing trips between devices or with other people.

## Requirements to build

| | |
|---|---|
| Android Studio | 2026.1 (build 261.25134.95) |
| JDK | 21 (the JetBrains Runtime bundled with Android Studio). JDK 17 or newer is required. |
| Gradle | 9.5.0, via the included Gradle wrapper |
| Android Gradle Plugin | 9.3.3 |
| Kotlin | 2.2.10 |
| compileSdk / targetSdk / minSdk | 37 / 36 / 24 |

### Google Maps API key

The Map screen uses the Google Maps SDK, which needs an API key. The key is not committed to the repository. To add it:

1. Create a key at https://console.cloud.google.com/ with **Maps SDK for Android** enabled.
2. Add this line to `local.properties` in the project root. 
   ```
   MAPS_API_KEY=your_key_here
   ```

Without a key the app still builds and runs, but the map appears blank. The other screens, city search, sharing and opening a city in Google Maps all work without it.

## Build steps

1. Extract the ZIP and open the project folder in Android Studio (**File → Open**).
2. Let Gradle sync finish.
3. Add `MAPS_API_KEY` to `local.properties` as described above.
4. Build with **Build → Make Project**

## Run steps

1. Start an emulator or connect a device running Android 7.0 or newer. An emulator with a **Google Play** system image is best, because the map and Google Maps hand-off need Google Play services.
2. Select the `app` run configuration and press **Run**.

## Using the app

1. **Home:** lists all trips. Tap **+** to create a trip with a name and start date. Use the chips to sort trips by date, name or newest.
2. **Trip (detail):** opened by tapping a trip card; the trip's ID is passed through navigation. Tap **Add city** to search for a city and add it. Use **−/+** to change the nights, the arrows to reorder stops, and the bin icon to remove a stop.
3. **Map:** the map icon on Home. Shows every trip's route in a different colour. Use the chips to show one trip at a time.
4. **Settings:** the gear icon on Home. Use the slider to switch between light and dark theme.

## Demonstrating the network feature

City search sends a live request to the https://nominatim.org/ API (`CitySearchService`), using coroutines on `Dispatchers.IO`. To demonstrate it, open a trip and tap **Add city**.

| State | How to trigger it |
|---|---|
| Loading | Search for any city. A spinner and "Searching…" appear while the request runs. |
| Success | Search `Perth`. A list of matching cities appears. Tap one to add it. |
| Empty | Search `zzqxv`. The request succeeds with no results, and the dialog shows "No cities found". |
| Error + Retry | Turn on aeroplane mode, then search. An error message appears with a **Retry** button. Turn aeroplane mode off and tap **Retry**. |
| Duplicate | Add a city that is already in the trip. The dialog shows "That city is already in this trip" and does not add it again. |

**Bad data:** `CitySearchService.parseCities` skips results whose latitude or longitude is missing, not a number, or out of range. It also skips results with no usable name, and removes duplicate results with the same name and region. A response that is not valid JSON shows an error state instead of crashing.

**Offline use:** trips and destinations are stored in Room. Creating trips, editing nights, reordering, deleting and changing settings all work without internet. Only city search needs a connection.

## Demonstrating the device / inter-app features

- **Sharing:** on Home, tap the share icon on a trip card. The Android share sheet opens with a short summary of the trip. Closing the share sheet without choosing an app does nothing. If no app can handle sharing, a message is shown instead of a crash.
- **Google Maps:** on the Map screen, tap a city marker, then tap its info window. The city opens in the Google Maps app, or in a browser if the app isn't installed. If nothing can open it, a message is shown.

Neither feature needs a runtime permission.

## Project structure

```
app/src/main/java/com/example/travelplanner_finalassignment/
├── MainActivity.kt          Creates the repositories and sets up the theme
├── data/                    CitySearchService (Nominatim network request and parsing)
├── model/                   Room entities, DAOs, AppDatabase, AppRepository,
│                            DataStore preference repositories, TripRules (validation)
├── viewmodel/               One ViewModel and Factory per screen
└── view/                    Composable screens, dialogs and AppNavHost (navigation)
```

## Group members

- Gabriel Pereira
- Henry Phan
- Trung Thanh Nguyen: 
