# LeaguesFromSportDB

Small app using the SportDB to get a list of leagues and show a list of teams from the selected
league. The teams list is sorted anti-alphabetically and only displays one in two.

## Featuring:

- Clean architecture + MVVM
- Jetpack Compose
- Kotlin Coroutines
- Retrofit
- Dependency injection with Hilt
- Unit tests with Mockito

## How to launch

Define in your local gradle.properties the sportsDB's url with your own API key:

sportsdbUrl="https://www.thesportsdb.com/api/v1/json/{API_KEY}/"

## Remarks:

- There is no equivalent to AutoCompleteTextView in Compose, so I've done a simple replacement with
  a list.
- Ideally, we should use latest libraries and gradle versions. To go faster I did not update them.
- Ideally, we want unit tests on everything testable: repositories, use cases, viewModels... I've
  only created one to test the specific rules of team sorting and filtering.
- We could add a placeholder when loading images and handle image errors.
- We could further explicit the error message by checking the type of error (IOException for network
  issues, etc.)
- We could do the clean architecture by modules instead of packages, modules are better for bigger
  projects.
- We should use UI data classes in the ViewModel and Compose views instead of using directly the
  domain models (for League and Team). In this case it was faster to use the same.
- UX-wise, it could be good to show the list of leagues available even without entering a text, to
  help people who don't know where to start. If the list is long, we could add an alphabetical
  scrollbar.
- Design wise, we could improve a bunch of things, with a better theme, an app icon, animations,
  etc.