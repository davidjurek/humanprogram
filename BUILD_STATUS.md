# Human Program Development Progress

Use this file to track what has been built, what is next, and what decisions have been made.

## Current Status

- Repository exists at `https://github.com/davidjurek/humanprogram.git`.
- Full app blueprint exists in `ADD.md`.
- Persistent agent instructions exist in `AGENTS.md`.
- Android app foundation builds successfully.

## Completed

- Created project repository.
- Added Human Program Android master spec.
- Added persistent agent instruction file.
- Added this development progress tracker.
- Created first Android project foundation.
- Added a Compose app shell with Today, Backlog, Calendar, Routines, and Settings tabs.
- Added first in-memory planner state.
- Added interactive Today tasks with completion checkboxes.
- Added manual task creation on Today.
- Added interactive Backlog item creation.
- Added ability to assign Backlog items to Today.
- Added basic backlog completion sync when assigned Today tasks are checked.
- Added first game locked/unlocked status card.
- Added simple schedule and exercise sections on Today.
- Added pure daily completion service.
- Added pure daily page generator for recurring tasks and assigned backlog items.
- Added game access service bridge.
- Added first unit tests for completion, generation, and game access rules.
- Updated Gradle config for the installed Android 16 QPR2 SDK/API 36.1.
- Connected the ViewModel to the daily completion and game access services.
- Seeded Today from recurring task templates through the daily page generator.
- Hid completed backlog items from the normal active Backlog view.
- Added basic Settings stats for Today, Backlog, and game access.
- Removed visible game status from normal UI because the game is an easter egg.
- Added current backlog CSV export service.
- Added unit tests for backlog CSV export headers, active-only behavior, and escaping.
- Added backlog CSV import preview service.
- Added unit tests for CSV import accepted rows, missing titles, invalid dates, and quoted cells.
- Added Gradle project properties, including AndroidX support.
- Set Java and Kotlin compilation to Java 17 for consistent Android builds.
- Configured local Android SDK path in ignored `local.properties`.
- Ran unit tests successfully with `./gradlew test`.
- Built debug APK successfully with `./gradlew assembleDebug`.
- Added backlog overdue assignment cleanup service and tests.
- Added exercise template factory that guarantees seven weekday templates and tests.
- Added schedule conflict detection, sleep-block normalization, and tests.
- Added streak calculation service and tests.
- Added app-private JSON snapshot persistence for Today tasks and Backlog items.
- Connected `MainActivity` to a ViewModel factory that loads/saves the planner snapshot.
- Expanded Settings with Stats, Recurring Tasks, Schedule, Exercise, Import/Export, and Privacy sections.
- Added basic streak display in Settings.
- Added editable recurring task creation and active/inactive toggles in Settings.
- Added editable schedule block creation in Settings.
- Added editable exercise item creation in Settings.
- Persisted recurring tasks, schedule blocks, and exercise routine in the app-private snapshot.
- Added Settings UI for backlog CSV import and current backlog CSV preview/export text.
- Added notification reminder model, persisted reminder definitions, and Settings UI to add/toggle reminders.
- Added Calendar tab modes for Month, Week, Day, and Agenda with permission-safe placeholder states.
- Added first `.hprgm` export package builder with manifest and planning JSON foundations.
- Re-ran unit tests successfully with `./gradlew test` after this development pass.
- Rebuilt debug APK successfully with `./gradlew assembleDebug` after this development pass.

## In Progress

- First usable planner prototype.
- Core planning rules before database persistence.
- CSV import/export foundations.
- Emulator/manual UI testing.
- Schedule, exercise, backlog maintenance, and stats rule foundations.
- Temporary app-private persistence before Room/DataStore migration.

## Next Steps

1. Run the app on an emulator from Android Studio.
2. Check that Today, Backlog, Calendar, Routines, and Settings open.
3. Check that tasks can be added and completed.
4. Replace in-memory state with Room/DataStore persistence.
5. Split the large app UI file into screen-specific files after manual testing.
6. Add full daily page generation rules from `ADD.md`.
7. Connect CSV import/export services to Settings UI after persistence exists.

## Decisions

- Build as an offline-first Android app.
- Privacy and data security are core build values, even though the main product purpose is daily execution.
- Do not require Google login, Firebase, analytics, ads, or cloud services.
- Keep the future game separate from planner logic.
- Treat the game as an easter egg; do not show obvious game UI in normal app screens.
- Keep game lock/unlock logic hidden unless an intentional easter egg path reveals it.
- Treat `ADD.md` as the source of truth.
- Keep beginner-friendly communication as a standing rule.
- Compile against installed Android 16 QPR2 SDK/API 36.1 while keeping minimum Android 12/API 31.
- Codex/agents may update `ADD.md`, `BUILD_STATUS.md`, and `AGENTS.md` as needed.

## Notes For Future Agents

- Update this file after each meaningful development chunk.
- Keep entries short and plain.
- Do not use this file for detailed product requirements; use the master spec for that.
- Do not mark work completed until it has actually been implemented.
