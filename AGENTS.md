# Human Program Agent Instructions

This file is persistent working memory for AI coding agents working on Human Program.
The full product brief lives in `ADD.md`; treat that file as the source of truth for product behavior.

## Human Owner

- The owner is a beginner.
- Use short, plain explanations.
- Avoid programming jargon unless it is necessary, and explain it simply.
- Tell the owner exactly what changed and exactly what to test.
- Ask only questions that are truly needed.
- Do not give long walls of text unless the owner asks for detail.
- Avoid parallel terminal commands in this repo unless truly necessary. One command at a time keeps the session easier to follow and less likely to hang.

## Product

Human Program is a private, offline-first Android daily execution app.
Privacy and data security are not the main product purpose, but they are core build values.

Core idea:

- Each day has one generated daily page.
- The daily page combines recurring tasks, backlog items assigned to that date, calendar items, exercise information, and schedule blocks.
- Completing all required tasks unlocks a future game for that day.
- The planner and game must stay separate.
- The game is an easter egg and should not be visibly advertised in normal app UI.
- Game lock/unlock state may exist internally, but obvious game entry points should stay hidden until intentionally revealed.

## Android Defaults

Use these choices unless the owner explicitly changes them:

- Language: Kotlin
- UI: Jetpack Compose
- Base design system: Material 3 with custom theme
- Package name: `app.humanprogram.android`
- Minimum Android version: Android 12 / API 31
- Persistence: Room for structured data
- Preferences: DataStore
- Background work: WorkManager only where useful
- Reminders: Android notification APIs
- Calendar: optional Android Calendar Provider adapter
- Export extension: `.hprgm`

Do not add by default:

- Required internet access
- Required Google account
- Firebase
- Google Play Services dependency
- Analytics
- Ads
- Cloud backend
- Remote crash reporting

## Architecture Preferences

- Keep the app local-first and privacy-preserving.
- Screens should talk to ViewModels.
- ViewModels should talk to repositories/services.
- Repositories/services should talk to Room, DataStore, Android APIs, or file storage.
- Do not let screens directly write raw database records.
- Keep code organized so a single app module can later split into modules.
- Keep game access behind a small `GameAccessService` bridge.
- Do not mix game logic into planner task logic.
- Current app state includes a temporary app-private JSON snapshot plus Room/DataStore foundations. Keep migrating UI behavior toward Room repositories and DataStore, but do not remove the JSON fallback until replacement behavior is tested.
- Keep Room entities, DAOs, repositories, and mappers under clear `core/database`, `core/datastore`, and planning repository/service areas.

Recommended package areas:

```text
app/
core/
  database/
  datastore/
  model/
  time/
  export/
  notifications/
planning/
  daily/
  backlog/
  recurring/
  exercise/
  schedule/
  calendar/
  stats/
  settings/
gamebridge/
game/
testing/
```

## Navigation

The main app should use five bottom tabs:

```text
Today
Backlog
Calendar
Routines
Settings
```

Stats initially belongs inside Settings.
Past daily pages are reached from Today by changing the date.

## UI Preferences

- Calm, utilitarian, pleasant, human-friendly.
- Personal but not flashy.
- Use Material 3 where it fits.
- Avoid glossy translucent styling and large decorative effects.
- Avoid one-color palettes.
- Prefer a Georgia-like serif as the default reading font, with a future font setting.
- Do not make a marketing landing page as the main experience.
- The first screen should be the usable app.

## Data Rules

- Past daily pages are historical snapshots.
- Template changes can update today and future pages.
- Template changes must not rewrite past pages.
- There are required tasks only; no optional tasks.
- Empty Today task list does not count as complete.
- Exercise section does not count toward day completion unless exercise is also a normal required task.
- Calendar-derived Today entries count toward completion.
- Game access unlocks only when today is complete.
- Game progress must never be deleted just because the daily gate locks again.
- The game is hidden. Do not show game links, cards, labels, stats, settings, or normal UI text that advertises it. Hidden gate/easter-egg code and internal save metadata are allowed.

## Safety Rules

- Preserve user data.
- Treat privacy and data security as important implementation concerns.
- Ask before destructive changes.
- Do not wipe databases, generated files, or project history unless explicitly requested.
- Keep the app usable after each significant chunk of work.
- Do not make large unrelated changes.
- Update `BUILD_STATUS.md` after every meaningful development chunk.
- Work in tested checkpoints instead of piling up large unverified edits.
- Commit only after behavior is working and the owner approves or asks.

## Build Order

Preferred implementation order:

1. Project foundation: Kotlin, Compose, Material 3, package name, bottom tabs.
2. Core data: Room entities, DAOs, repositories, DataStore preferences.
3. Today screen: generated page, tasks, completion, schedule, exercise.
4. Backlog: items, projects, assignment, imports/exports.
5. Settings: recurring tasks, schedule editor, exercise editor, stats entry.
6. Calendar adapter.
7. Notifications.
8. Stats.
9. `.hprgm` export/import.
10. App lock and encryption.
11. Game bridge.
12. Game integration.

Current priorities:

- Finish moving planner UI behavior from the JSON snapshot into Room repositories.
- Add real notification permission and reminder scheduling UX.
- Add real calendar permission/source selection and event import UX.
- Add durable app-lock storage and full PIN/biometric UX.
- Add `.hprgm` file save/open flows.
- Split large Compose files, especially `HumanProgramApp.kt`, into smaller screens/components once behavior is stable.

## Testing Expectations

Add tests when implementing core rules, especially:

- daily page generation
- recurring task weekday matching
- backlog assignment and completion sync
- overdue backlog behavior
- past page snapshot behavior
- exercise exclusion from completion
- calendar inclusion in completion
- schedule conflict detection
- streak calculation
- export contents
- game unlock logic

When possible, run Gradle tests or explain clearly why they were not run.
For app/build changes, prefer:

```text
JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home ./gradlew test assembleDebug
```

If Gradle fails only because sandbox access to `~/.gradle` is blocked, rerun with the needed approval.

## Git Workflow

- Check status before editing when practical.
- Do not revert user changes unless explicitly asked.
- Keep commits focused.
- Use clear beginner-readable commit messages.
- Ask before pushing if the owner has not already requested it.

## Maintained Project Docs

Agents may update these files as needed while working:

- `ADD.md`
- `BUILD_STATUS.md`
- `AGENTS.md`
