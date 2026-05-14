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
- Added Room dependency and KSP compiler setup.
- Added Room database shell with DAOs and entities for backlog, daily pages, daily tasks, recurring tasks, schedules, exercise templates/items, and reminders.
- Configured Room schema export directory.
- Added app lock settings Room entity/DAO.
- Added app lock service foundation and tests for timeout-based locking.
- Added optional calendar and notification permissions to the manifest.
- Added calendar local-state Room entity/DAO.
- Added calendar merge service for provider events plus local completion/hide/override state.
- Added notification scheduling planner foundation and tests.
- Added DataStore Preferences dependency and repository foundation for appearance, font, Today metadata, task source, project bucket, and calendar view settings.
- Added hidden-game Room entities/DAO for game access events and game save metadata.
- Added easter egg gate service requiring both puzzle completion and day completion before hidden game entry can be revealed.
- Re-ran `./gradlew test assembleDebug` successfully after Room, DataStore, calendar, notification, app lock, and hidden game foundations.
- Added `.hprgm` ZIP writer and tests that verify exported files are written into a valid zip package.
- Added PIN hashing/verification service for app lock foundations without storing raw PINs.
- Added project bucket service for unique names, virtual Unorganized display, and project deletion modes.
- Added app startup planner for overdue backlog cleanup, seven exercise templates, and Today page generation.
- Added Room entity/model mappers for backlog items, daily tasks, recurring tasks, and reminders.
- Re-ran `./gradlew test assembleDebug` successfully after export, security, project, startup, and Room mapper work.
- Added singleton Room database provider.
- Added repositories for Backlog, Daily Pages, Recurring Tasks, and Reminders.
- Added migration coordinator for copying the current app-private JSON snapshot into Room-backed repositories.
- Added Room-backed planning ViewModel skeleton for migrating UI screens from JSON snapshot storage to Room repositories.
- Added Settings copy for optional Calendar permission, offline behavior, Room/DataStore foundation, and app lock foundation.
- Added Today date navigation controls and read-only protection for past dates.
- Added Backlog Item/Project view switch.
- Added project bucket entry when creating backlog items.
- Added project-grouped Backlog display with virtual Unorganized group.
- Added App Lock Settings UI for setting/testing a hashed PIN without storing raw PIN text.
- Replaced the Routines placeholder with a persisted simple routines list and add-routine UI.
- Added hidden easter egg code detector foundation without exposing game UI.
- Re-ran `./gradlew test assembleDebug` successfully after repository, migration, Backlog project view, App Lock UI, Routines, and easter egg code work.
- Added Android reminder receiver, notification channel creation, AlarmManager scheduler, and safe handling when notification permission is denied.
- Added Android Calendar Provider reader that returns empty results unless calendar permission is granted.
- Added `.hprgm` ZIP import preview reader that validates manifest/planning files.
- Re-ran `./gradlew test assembleDebug` successfully after Android notifications, calendar provider reader, `.hprgm` import preview, and latest UI expansions.
- Connected Android notification and calendar permission request buttons to Settings and Calendar.
- Added `.hprgm` save/open picker flows in the app, with export writing and import preview validation.
- Added plain Settings status messages for notification permission, calendar permission, and `.hprgm` import/export results.
- Re-ran `JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home ./gradlew test assembleDebug` successfully after permission and `.hprgm` file-picker wiring.
- Changed `.hprgm` planning export from title-only data to a full planner snapshot JSON.
- Added `.hprgm` import apply behavior for valid planner snapshots.
- Connected saved reminders to Android AlarmManager scheduling after reminder creation or toggling.
- Added permission-gated device calendar event display in Today and Calendar.
- Added calendar-derived Today tasks so device calendar events count toward day completion.
- Added startup mirroring from the current JSON-backed planner snapshot into Room repositories.
- Added durable app-lock PIN hash storage through DataStore and reload on app startup.
- Added a ViewModel unit test for calendar events becoming required Today tasks.
- Added a simple PIN unlock screen that appears when app lock is enabled.
- Removed visible "comes next/later" copy from current user-facing UI sections.
- Added ViewModel tests for calendar-derived required tasks and stored PIN lock/unlock.
- Re-ran `JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home ./gradlew test assembleDebug` successfully after `.hprgm`, reminders, calendar tasks, Room mirroring, durable app-lock storage, and lock-screen work.
- Updated `ADD.md` with owner decisions for daily page timing, future/past page behavior, live calendar source selection, reminders, app lock, encrypted `.hprgm` export/import, delete undo/redo, hidden game entry, stats, reset, and v1 priorities.
- Corrected stale spec wording around import-later behavior, one-time/interval reminder-only modeling, and project deletion confirmation.
- Restored the hidden Sudoku-style gate as part of the easter egg path: Settings -> About -> double-tap developer name -> Sudoku-style gate -> game entry if today is complete.
- Added undo/redo support for reversible Today task and Backlog item deletes, with UI controls and ViewModel tests.
- Added calendar source selection backed by DataStore; only selected device calendars generate Today tasks.
- Added persisted/exported calendar local state for completion, hide, and title overrides.
- Added Calendar UI controls for local calendar event rename and hide.
- Added hidden Settings/About developer-label double-tap flow that reveals a Sudoku-style gate only after the day is complete.
- Added user-selectable app-lock timeout controls backed by DataStore.
- Changed `.hprgm` import to preview first, then apply through an explicit button.
- Re-ran `JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home ./gradlew test assembleDebug` successfully after undo/redo, calendar source/local state, hidden Sudoku gate, app-lock timeout, and preview-then-apply import work.

## In Progress

- First usable planner prototype.
- Core planning rules before database persistence.
- CSV import/export foundations.
- Emulator/manual UI testing.
- Schedule, exercise, backlog maintenance, and stats rule foundations.
- Temporary app-private persistence before Room/DataStore migration.
- Room database migration foundation.

## Next Steps

1. Run the app on an emulator from Android Studio.
2. Check that Today, Backlog, Calendar, Routines, and Settings open.
3. Check that tasks can be added and completed.
4. Replace temporary JSON-backed UI behavior with direct Room/DataStore reads and writes.
5. Split the large app UI file into screen-specific files after manual testing.
6. Add full daily page generation rules from `ADD.md`.
7. Add biometric unlock support.
8. Expand undo/redo beyond delete into edit, assignment, and completion actions.
9. Add encrypted full `.hprgm` export with optional game save checkbox.
10. Replace temporary JSON-backed UI behavior with direct Room/DataStore reads and writes.

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
