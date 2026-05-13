# Human Program Agent Instructions

This file is persistent working memory for AI coding agents working on Human Program.
The full product brief lives in `HUMAN_PROGRAM_ANDROID_AGENT_SPEC.md`; treat that file as the source of truth for product behavior.

## Human Owner

- The owner is a beginner.
- Use short, plain explanations.
- Avoid programming jargon unless it is necessary, and explain it simply.
- Tell the owner exactly what changed and exactly what to test.
- Ask only questions that are truly needed.
- Do not give long walls of text unless the owner asks for detail.

## Product

Human Program is a private, offline-first Android daily execution app.

Core idea:

- Each day has one generated daily page.
- The daily page combines recurring tasks, backlog items assigned to that date, calendar items, exercise information, and schedule blocks.
- Completing all required tasks unlocks a future game for that day.
- The planner and game must stay separate.

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

## Safety Rules

- Preserve user data.
- Ask before destructive changes.
- Do not wipe databases, generated files, or project history unless explicitly requested.
- Keep the app usable after each significant chunk of work.
- Do not make large unrelated changes.
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

## Git Workflow

- Check status before editing when practical.
- Do not revert user changes unless explicitly asked.
- Keep commits focused.
- Use clear beginner-readable commit messages.
- Ask before pushing if the owner has not already requested it.
