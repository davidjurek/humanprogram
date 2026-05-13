# Human Program Android Agent Master Spec

This file is the authoritative build brief for creating **Human Program** as a native Android app. It is written primarily for AI coding agents, with enough plain-language explanation for a beginner human owner to understand the intended behavior.

The Android package name is:

```text
app.humanprogram.android
```

This master spec integrates the ADD design requirements, observed app behavior, project working rules, and Android port decisions into one standalone file. An agent should not need the old project files to understand what to build.

## 1. Agent Instructions

Build **Human Program**, not a clone tied to any previous platform implementation. Treat this document as the product, architecture, and workflow source of truth.

MUST:

- Build the app as a native Android application using Kotlin and Jetpack Compose.
- Use a calm, utilitarian, pleasant, human-friendly interface.
- Keep the app fully usable offline.
- Support a broad range of modern Android devices, with Pixels and GrapheneOS treated as first-class targets.
- Avoid Google Play Services, Firebase, Google sign-in, Google Analytics, advertising SDKs, crash-reporting trackers, and cloud dependencies by default.
- Build with optional future sync in mind, but do not ship sync in the first build.
- Use local persistence as the primary backend.
- Preserve privacy and user ownership of data.
- Use `Human Program` in user-facing language.
- Use `.hprgm` as the Human Program export file extension.
- Keep the architecture modular enough for a full game to live in the same app.
- Build the game as a separate container/module that is bridged to the planning container by app state, not mixed into task-list code.
- Gate game access behind completion of all tasks for the current day.
- Make the game save persist independently from the daily task gate.
- Prefer Godot or another open-source, embeddable game path for the future game container.
- Explain technical work in beginner-friendly language when communicating with the human owner.

SHOULD:

- Use Material 3 components where they fit, but customize typography, spacing, and color enough that the app feels personal rather than generic.
- Use a Georgia-like serif as the default reading font, with a font setting that lets the user switch fonts later.
- Use Room for structured local data.
- Use DataStore for small preferences.
- Use Android notification APIs directly for reminders.
- Use the Android Calendar Provider only as an optional adapter, never as a hard dependency.
- Keep every feature in a service/repository/viewmodel boundary so the game module can be added without destabilizing the planner.
- Write tests for date generation, completion logic, backlog assignment logic, export/import, and game unlock logic.

DO NOT:

- Require internet access for core behavior.
- Require a Google account.
- Require a cloud account.
- Add analytics or trackers.
- Add hidden network calls.
- Build the game directly into planner viewmodel logic.
- Make optional tasks. Human Program has required tasks only.
- Add an admin bypass for the game lock.
- Wipe game saves when the daily lock resets.
- Use glossy translucent styling or large decorative glass effects.
- Create a marketing landing screen as the main app experience.
- Use a one-hue palette. The app should be restrained but not monochrome.
- Use swipe-to-delete as the main editing pattern.
- Use red delete circles as the default editing pattern.
- Reorder or regenerate historical daily pages when templates change later.
- Make past day edits easy to trigger accidentally.
- Assume the user knows programming terms.

## 2. Product Purpose

Human Program is a personal operating app for daily execution. Its purpose is to turn scattered planning inputs into one daily operational page.

The app answers:

- What day is it?
- What needs to be done today?
- What is on the schedule?
- What calendar obligations exist today?
- What exercise routine applies today?
- Is the day complete?
- Is the game unlocked?

The core product idea is simple:

Each day gets one generated page. That page combines recurring tasks, date-assigned backlog tasks, calendar-derived items, exercise information, and a schedule snapshot. The page is the live command center for that day. Once created, it also becomes a historical record.

Human Program is not primarily a public productivity app. It is a personal tool optimized for reliability, clarity, privacy, and extensibility.

## 3. Recommended Android Build Choices

Use these defaults unless the human owner explicitly changes them.

```text
Language: Kotlin
UI: Jetpack Compose
Design system: Material 3 with custom theme
Package: app.humanprogram.android
Minimum Android version: Android 12 / API 31
Target SDK: latest stable available in the installed Android tooling
Persistence: Room + DataStore
Background work: WorkManager only where needed
Reminders: Android notification APIs plus exact-alarm policy only if truly necessary
Calendar adapter: Android Calendar Provider, optional and permission-gated
Export format: .hprgm
Game path: separate game module/container, Godot-compatible
```

API 31 is a modern baseline that supports a decent range of Android phones without chasing very old Android behavior. The app should be tested especially on Pixel and GrapheneOS, but it should not be artificially limited to those devices. If Android Studio generates a newer project template, keep the minimum at API 31 unless there is a clear reason to change it.

## 4. Big Architecture

Human Program has two major containers:

1. **Planning Container**
2. **Game Container**

The Planning Container owns:

- daily pages
- tasks
- backlog
- recurring task templates
- exercise templates
- schedule templates
- stats
- settings
- reminders
- calendar adapter
- export/import
- game unlock state

The Game Container owns:

- game engine integration
- game screens
- game save files
- game settings
- game assets
- game lifecycle

The two containers communicate through a small bridge:

```text
Planning Container -> GameAccessService -> Game Container
```

The game must ask the planning side whether access is allowed. The planning side answers based on today’s completion state. The game does not inspect task tables directly.

The game save is separate:

```text
Game access lock: controlled by daily tasks
Game progress: controlled by game save
```

The daily gate can lock again on a new day, but game progress must persist.

## 5. App Modules

Recommended module structure:

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

For the first build, this can be a single Gradle app module with package-level folders. However, the code should be arranged so it can be split into Gradle modules later.

Do not let screens directly write raw database records. Screens talk to viewmodels. Viewmodels talk to repositories. Repositories talk to Room/DataStore/services.

## 6. Core Navigation

Use five bottom tabs:

```text
Today
Backlog
Calendar
Routines
Settings
```

Stats currently lives as a Settings entry and may later become its own bottom tab if the owner wants it. The current intended first navigation keeps the five tabs above.

Archive is not a tab. Past daily pages are reached by changing the date inside Today.

## 7. Core Object Model

Use stable IDs for all entities. UUID strings are acceptable.

### 7.1 BacklogItem

Represents work that is not necessarily part of today yet.

Fields:

```text
id: String
title: String
notes: String
projectBucket: String
assignedDate: LocalDate?
status: BacklogStatus
createdAt: Instant
updatedAt: Instant
```

Statuses:

```text
BACKLOG
DONE
```

Rules:

- Title is required.
- Notes are optional.
- Project bucket is optional.
- Assigned date is optional.
- Active backlog screens show only `BACKLOG` items.
- `DONE` items are hidden from normal backlog lists.

### 7.2 ProjectBucket

A project bucket is a lightweight grouping label for backlog items.

Fields:

```text
name: String
createdAt: Instant
updatedAt: Instant
```

Rules:

- Empty project bucket means no project.
- UI displays empty project as `Unorganized`.
- `Unorganized` is virtual and cannot be deleted.
- Empty named projects are allowed and should remain visible.
- Project names should be unique case-insensitively.

### 7.3 RecurringTaskTemplate

Represents a task that appears automatically on matching daily pages.

Fields:

```text
id: String
title: String
notes: String
applicableWeekdays: Set<Int>
active: Boolean
createdAt: Instant
updatedAt: Instant
```

Weekday numbering should be explicit and consistent:

```text
1 = Sunday
2 = Monday
3 = Tuesday
4 = Wednesday
5 = Thursday
6 = Friday
7 = Saturday
```

Rules:

- Title is required.
- At least one weekday is required.
- Active templates generate tasks.
- Inactive templates remain stored but do not generate tasks.
- Changing templates updates today and future generated pages, but not past pages.

### 7.4 ExerciseTemplate

Represents one exercise routine per weekday.

Fields:

```text
id: String
weekday: Int
title: String
notes: String
items: List<ExerciseTemplateItem>
updatedAt: Instant
```

ExerciseTemplateItem:

```text
id: String
text: String
sortOrder: Int
```

Rules:

- There should always be seven weekday templates, one for each weekday.
- Exercise title may be blank.
- Exercise items may be empty.
- Blank exercise item lines are not saved.
- Exercise templates generate an exercise item/module on daily pages.
- Exercise displayed as a separate section does not count toward day completion unless the user also creates a required recurring task named something like `Exercise`.

### 7.5 ScheduleTemplate

Represents a named daily schedule.

Fields:

```text
id: String
name: String
isEnabled: Boolean
assignedWeekdays: Set<Int>
customDateStart: LocalDate?
customDateEnd: LocalDate?
blocks: List<ScheduleBlock>
createdAt: Instant
updatedAt: Instant
```

Rules:

- A template can use weekday assignment or custom date range.
- A custom date range overrides normal weekday schedules.
- A weekday can have no schedule.
- A weekday cannot have more than one enabled weekday schedule.
- Overlapping enabled custom date ranges are allowed only if the conflict policy is intentionally changed; default behavior should block overlap.
- If a template has no name, auto-name it `Untitled Schedule N`.
- Templates sort alphabetically by name.
- Disabled templates do not generate schedule blocks.

### 7.6 ScheduleBlock

Represents one time block inside a schedule.

Fields:

```text
id: String
startTime: LocalTime
endTime: LocalTime
title: String
sortOrder: Int
```

Rules:

- A schedule should always start with a `Sleep` block.
- The first block is locked as the sleep block.
- If missing, insert `Sleep`.
- If first block is not named `Sleep`, insert `Sleep` above it.
- When blocks are reordered or edited, normalize subsequent blocks so each non-sleep block starts at the previous block’s end time.
- End time may be on the next day. Example: `21:30` to `05:30`.
- Durations should be at least 5 minutes.

### 7.7 DailyPage

Represents the generated page for a specific date.

Fields:

```text
id: String
date: LocalDate
createdAutomatically: Boolean
tasks: List<DailyPageTask>
scheduleBlocks: List<DailyPageScheduleBlock>
dayComplete: Boolean
createdAt: Instant
updatedAt: Instant
```

Rules:

- One daily page per date.
- Daily pages are generated on demand.
- Existing daily pages are reused.
- Past pages are historical snapshots.
- Today and future pages stay synced with current templates.
- Past pages do not get rewritten when templates change.
- `dayComplete` is the single day-completion truth.

### 7.8 DailyPageTask

Represents one checklist item on a generated day.

Fields:

```text
id: String
sourceType: DailyTaskSourceType
sourceId: String?
title: String
notes: String
completed: Boolean
completedAt: Instant?
sortOrder: Int
```

Source types:

```text
RECURRING
BACKLOG
EXERCISE
MANUAL
CALENDAR
```

Rules:

- Manual tasks have no source ID.
- Recurring tasks point to a recurring template ID.
- Backlog tasks point to a backlog item ID.
- Exercise tasks point to an exercise template ID.
- Calendar tasks point to a calendar event ID.
- Exercise source tasks should normally be displayed in the Exercise section, not counted in Today’s Tasks completion.
- Calendar-derived entries count toward completion.

### 7.9 DailyPageScheduleBlock

Snapshot copy of schedule blocks for a generated day.

Fields:

```text
id: String
startTime: LocalTime
endTime: LocalTime
title: String
sortOrder: Int
```

Rules:

- These blocks are copied from the active schedule template when a page is generated.
- Today and future pages can be refreshed from templates.
- Past page blocks remain historical.

### 7.10 CalendarEventLocalState

Stores local app state about external calendar events.

Fields:

```text
date: LocalDate
eventId: String
completed: Boolean
hidden: Boolean
titleOverride: String?
notesOverride: String?
sortOrder: Int?
updatedAt: Instant
```

Rules:

- Completing a calendar-derived task inside Human Program does not change the external calendar event.
- Hiding a calendar event from Today does not delete it from the calendar provider.
- Title and notes overrides are local.
- Calendar local state must be exportable.

### 7.11 StreakStats

Fields:

```text
currentStreak: Int
longestStreak: Int
```

Rules:

- Current streak counts backward from today while each daily page is complete.
- Longest streak is the longest run of complete daily pages up to and including today.
- Future pages do not contribute to streak.

### 7.12 NotificationReminder

Fields:

```text
id: String
title: String
reminderAt: Instant
soundMode: NotificationSoundMode
imageFilename: String?
intervalAmount: Int?
intervalUnit: NotificationIntervalUnit?
isEnabled: Boolean
recurrenceMode: NotificationRecurrenceMode
createdAt: Instant
updatedAt: Instant
```

Modes:

```text
ONE_TIME
INTERVAL
```

Interval units:

```text
MINUTES
HOURS
DAYS
WEEKS
```

Sound modes:

```text
DEFAULT
SILENT
```

Rules:

- Multiple reminders are allowed.
- Reminder text is required.
- One-time reminders auto-disable after they are no longer pending.
- Interval reminders repeat.
- Reminder image attachments are optional.
- Images should be compressed and stored locally.
- If notification permission is denied, reminder definitions are still saved but scheduling is skipped.

### 7.13 GameAccessState

Fields:

```text
date: LocalDate
isUnlocked: Boolean
unlockedAt: Instant?
reason: String
```

Rules:

- Game access is recalculated from today’s task completion.
- A new day starts locked until that day’s tasks are complete.
- The lock is only an access gate.
- Game progress is never deleted by the gate.

### 7.14 GameSaveMetadata

Fields:

```text
id: String
engine: String
saveSlot: String
lastPlayedAt: Instant?
localPath: String
schemaVersion: Int
```

Rules:

- Store game saves in app-private storage.
- Keep them separate from planner tables.
- Include game save metadata in `.hprgm` export only if the owner enables full export including game state.

## 8. Daily Page Generation

Generate a daily page automatically whenever a date is requested and no page exists yet.

Generation inputs:

1. Active recurring tasks matching the weekday.
2. Active backlog items assigned to the date.
3. The exercise template for the weekday.
4. The active schedule for the date.
5. External calendar events for that date, if permission and provider data are available.

Generation output:

1. DailyPage record.
2. DailyPageTask records for recurring tasks.
3. DailyPageTask records for assigned backlog items.
4. Exercise daily item/module.
5. DailyPageScheduleBlock snapshot records.
6. Local calendar entries or UI-derived calendar task entries.

Snapshot rule:

- Once a daily page exists, it is a record for that date.
- Template changes should update today and future pages only.
- Template changes must not rewrite past pages.

On app startup:

1. Load preferences and database.
2. Ensure seven exercise templates exist.
3. Reconcile overdue backlog assignments.
4. Ensure today’s page exists.
5. Refresh today/future generated pages from active templates.
6. Recalculate streaks.
7. Schedule reminders if allowed.

## 9. Day Completion Logic

Human Program has required tasks only. There are no optional tasks.

A day is complete when every item in Today’s Tasks is completed.

Completion includes:

- recurring tasks
- backlog-derived tasks
- manual tasks
- calendar-derived tasks

Completion excludes:

- the separate Exercise section, unless the user has also made exercise a normal required task
- schedule blocks
- hidden calendar events

Rules:

- If Today’s Tasks is empty, the day is not complete.
- If all Today’s Tasks entries are completed, set `dayComplete = true`.
- If any entry is incomplete, set `dayComplete = false`.
- Adding a new unchecked manual task sets `dayComplete = false`.
- Unchecking any task sets `dayComplete = false`.
- When `dayComplete = true`, show: `Congratulations, you are done for the day!`
- When `dayComplete = false`, hide that message.
- Game access for the day is unlocked when `dayComplete = true`.
- Game access locks again on a new day until that new day is complete.
- Game progress never resets when access locks.

## 10. Backlog Logic

Backlog items can be undated or assigned to a date.

When a day is generated:

- Find active backlog items assigned to that date.
- Create backlog-derived daily tasks for those items.
- Keep the original backlog item visible in the backlog until completed through the matching daily task.

Completion sync rules:

- If a backlog-derived task is checked on the daily page matching the backlog item’s current assigned date, mark the source backlog item as `DONE`.
- If that same task is unchecked while the assigned date still matches and the day is today or future, mark the source backlog item back to `BACKLOG`.
- This live connection applies only while the source backlog item is assigned to that same date and the daily page is not a past historical page.

Overdue rules:

- If an assigned backlog item’s date passes and it is still `BACKLOG`, remove its assigned date.
- The old daily page keeps its unchecked historical task.
- The active backlog item remains in backlog without an assigned date.
- It does not automatically roll forward.
- The user must choose a new date later if they want it to appear on another day.

Implication:

- One backlog item can produce multiple historical daily tasks over time if the user repeatedly assigns it to dates but does not complete it.

Project behavior:

- Backlog has Item View and Project View.
- Item View shows all active items.
- Project View groups by project bucket, including virtual `Unorganized`.
- Deleting projects offers two choices:
  - Delete project only and move items to `Unorganized`.
  - Delete project and delete its items.
- Confirmation must show project count, item count, and per-project breakdown.
- Completed backlog items are hidden from active views.

Mass import:

- Text import: one item per nonblank line, title only.
- CSV import: support header `title,date,project_bucket,note`.
- CSV date format: `yyyy-MM-dd`.
- Rows without title are rejected.
- Imported rows go to a preview page before insertion.
- Preview supports selecting which rows to import.

## 11. Schedule Logic

Schedules are named templates made of sequential time blocks.

Schedule assignment:

- Weekday schedules are assigned by weekday.
- Custom date schedules are assigned by date range.
- Custom date schedules override weekday schedules.
- Disabled schedules do not apply.

Conflict policy:

- Weekday buttons are always selectable while drafting.
- Conflicts are enforced on save or enable.
- If enabling or saving would overlap an already-enabled schedule, block the action and show a conflict message.
- Conflict messages should name the conflicting schedule and the overlapping weekday or date range.

Block normalization:

- First block is always `Sleep`.
- Sleep block has editable start and wake times.
- Each later block has a title and duration.
- Later block start times are computed from the previous block’s end time.
- Reordering blocks preserves durations and recomputes start/end times.
- Sleep block cannot be selected for deletion or moved.

Today display:

- Show a compact vertical daily schedule near the top of Today.
- Show 24-hour time markers.
- Show schedule blocks as colored rectangles.
- Show calendar events as additional timeline segments when available.
- Show the current-time line only on today’s page.
- Do not show the current-time line on past or future dates.

## 12. Calendar Logic

Calendar is an optional adapter. The app must work without calendar permission and without a calendar provider.

Calendar behavior:

- Support Month, Week, Day, and Agenda views.
- Month view shows a grid and daily event summary.
- Week view shows a multi-day timeline.
- Day view shows a single-day timeline.
- Agenda view shows upcoming events for about 30 days.
- User can choose which device calendars are included.
- If no calendars are selected, calendar views and Today calendar-derived entries should be empty.
- Calendar events appear on Today as checkable local entries.
- Checking a calendar-derived task inside Human Program does not modify the actual calendar event.
- Hiding a calendar-derived Today entry does not delete the calendar event.
- Local completion, hiding, title override, notes override, and order state must be persisted locally.

Calendar source selection:

- Default state may be all calendars.
- User can switch to explicit selected calendars.
- Store selected calendar IDs in DataStore or Room.

Sync status:

- Provide a status screen or sheet that compares active Today pages with current calendar provider data.
- Detect events hidden from Today but still present in provider data.
- Detect title overrides that differ from provider title.
- Detect stale hidden events that no longer exist.
- Provide a resync action that restores hidden items and clears local title overrides for active dates.

Permissions:

- Ask for calendar permission only when needed.
- If denied, show a calm explanation and a button to open app settings.
- Do not block the rest of the app.

## 13. Exercise Logic

Exercise templates are managed in Settings.

Behavior:

- There is one exercise template per weekday.
- Each template has a label/title and ordered exercise items.
- User can add items inline.
- User can edit existing items inline.
- Empty item text deletes the item or is ignored for new entries.
- User can enter edit mode to select and delete items.
- User can reorder items while editing.
- Reordering should batch persistence so dragging remains smooth.
- On save/done, propagate exercise template changes to today and future pages.
- Do not rewrite past pages.

Today display:

- Show Exercise below Today’s Tasks.
- Display weekday’s exercise title and item list.
- If no routine exists, show an empty state.

## 14. Routines

Routines is a planned tab.

Initial behavior:

- Show a simple placeholder.
- Keep it as a real navigation destination so future routine logic has a home.

Future behavior:

- Routines may contain repeatable non-task workflows.
- Routines should be searchable.
- Routines may later feed Today generation, but only after a clear model is designed.

## 15. Stats

Stats is currently light but must be built with expansion in mind.

Initial stats:

- Current streak.
- Longest streak.
- Today status: Complete or In progress.
- Tasks finished: completed count / total count.

Future stats:

- Weekly completion summary.
- Monthly completion summary.
- Completion rate trends.
- Item-level adherence.
- Exercise consistency.
- Goal and reward tracking.
- Sleep metrics.

Streak calculation:

- Sort daily pages up to today by date.
- Longest streak is the longest consecutive run of `dayComplete = true`.
- Current streak counts backward from today while pages are complete.
- Future pages are ignored.

## 16. Settings

Settings sections:

```text
Search
General
Planning
Stats
Import/Export
About
Danger Zone
```

Search:

- Top-level entry above General.
- Initially can be placeholder, but the planned behavior is universal search.

General:

- Date format.
- Appearance: light, dark, match system.
- Font setting: default Georgia-like serif, plus future options.

Today Display:

- Metadata behavior:
  - hidden unless tapped
  - visible by default
- Metadata fields:
  - show project bucket
  - show task source

Notifications:

- Manage daily-list reminders.
- Multiple reminders.
- Enable/disable toggles.
- Create, edit, delete.

Planning:

- Recurring Tasks.
- Edit Schedule.
- Edit Exercise.

Stats:

- Entry to Stats screen.

Import/Export:

- Import backlog from text.
- Import backlog from CSV.
- Export current backlog to CSV.
- Export historical daily task data to CSV by date range, including presets such as last 30, 60, or 90 days.
- Export `.hprgm`.
- Import `.hprgm` later if enabled.

About:

- App version.
- Build number.
- Developer info.
- Cat Corner-style personal media gallery.
- Hidden document easter egg using a readable document-style screen.
- Hidden Sudoku-style puzzle gate entry.
- Future game entry point.

Danger Zone:

- Factory reset.
- Requires two-step confirmation.
- User must type `reset`.
- Reset clears planning data, preferences, reminders, local calendar state, and generated pages.
- Reset must not silently delete exported files outside app-private storage.

## 17. Universal Search Planned Behavior

Search must be case-insensitive.

Support `*` wildcard matching:

```text
jo*   can match JOSEPH
s*H   can match JOSEPH
```

Search included sources:

- tasks in daily pages across past, present, and future dates
- backlog items
- project bucket names
- notes on tasks
- schedule settings content
- exercise routine items
- recurring task items
- routines content
- calendar content available through the local calendar adapter

Search excluded sources:

- stats summaries
- import/export pages
- notification settings content
- unrelated preferences

Recommended implementation:

- Start with simple Room queries and Kotlin wildcard matching.
- Later add FTS tables if performance requires it.

## 18. Reminder Behavior

Reminder list:

- Shows reminder primary text.
- Shows secondary text with reminder title, recurrence info, and image indicator.
- Has quick enable/disable toggles.
- Supports add and edit.
- Supports delete.

Editor fields:

- Reminder text.
- Type: One Time or Interval.
- Notify at time.
- Date for one-time reminders.
- Interval amount and unit for interval reminders.
- Sound mode.
- Optional image attachment.

Scheduling rules:

- Save definitions locally even when notification permission is unavailable.
- If permission is granted, schedule enabled reminders.
- If permission is denied, skip scheduling and keep the settings.
- One-time reminders in the past should not schedule.
- One-time reminders should auto-disable after firing or no longer being pending.
- Interval amount should be clamped to a reasonable range.
- Minimum repeat interval should comply with Android platform limits.

GrapheneOS note:

- Do not depend on proprietary push notification services.
- Use local notifications only.

## 19. Export Format

The Human Program export extension is:

```text
.hprgm
```

Recommended container:

```text
ZIP file with .hprgm extension
```

Recommended internal files:

```text
manifest.json
planning.json
preferences.json
calendar_local_state.json
notifications.json
notification_images/
game_manifest.json
game_saves/
```

Manifest fields:

```text
formatName: "Human Program Export"
formatVersion: Int
exportedAt: Instant
appVersionName: String
appVersionCode: Int
packageName: "app.humanprogram.android"
includesGameData: Boolean
```

Planning export includes:

- backlog items
- project buckets
- recurring task templates
- exercise templates
- schedule templates
- daily pages
- streak stats

Preferences export includes:

- date format
- appearance mode
- font setting
- metadata visibility
- project/source metadata toggles
- backlog display mode
- backlog sort mode
- calendar view mode
- selected calendar IDs
- welcome/completion flags where relevant

Calendar local state export includes:

- completed calendar event IDs by date
- hidden calendar event IDs by date
- calendar event title overrides
- calendar event notes overrides
- Today task entry order by date

Notification export includes:

- reminder definitions
- enabled flags
- sound mode
- recurrence mode
- image filenames

Security and privacy:

- Export is user-triggered only.
- Do not automatically upload exports.
- Future encrypted export is strongly recommended.
- If encrypted export is added, use a standard authenticated encryption scheme through AndroidX Security or a well-reviewed library.

Import:

- Since this Android app is a separate app, no migration from older formats is required.
- `.hprgm` import can be built later.
- If import is built, warn clearly that restoring a full backup overwrites local app state.
- If app lock is enabled, importing a `.hprgm` file requires the user to unlock first with PIN or approved biometric.
- A locked app must never import, preview, parse, overwrite, or restore a `.hprgm` file in the background without explicit unlock.
- `.hprgm` import must validate manifest version, file paths, file sizes, content types, and schema before writing anything to the live database.
- `.hprgm` import must defend against ZIP path traversal and decompression bombs.

## 19.1 CSV Exports

Human Program must support user-triggered CSV export for people who want readable spreadsheet-style data outside the full `.hprgm` backup format.

CSV export types:

1. Current backlog export.
2. Historical daily task export.

Current backlog CSV:

- Exports what is currently in active backlog.
- Includes only active backlog items by default.
- Completed backlog items may be included later behind an explicit option.
- Filename pattern: `human-program-backlog-YYYY-MM-DD.csv`.
- Recommended columns:
  - `id`
  - `title`
  - `notes`
  - `project_bucket`
  - `assigned_date`
  - `status`
  - `created_at`
  - `updated_at`

Historical daily task CSV:

- Exports daily task history for a chosen date range.
- Must include quick presets:
  - Last 7 days.
  - Last 30 days.
  - Last 60 days.
  - Last 90 days.
  - Custom range.
- Filename pattern: `human-program-history-STARTDATE-to-ENDDATE.csv`.
- Recommended columns:
  - `date`
  - `day_complete`
  - `task_id`
  - `task_title`
  - `task_notes`
  - `source_type`
  - `source_id`
  - `completed`
  - `completed_at`
  - `sort_order`
  - `project_bucket`
  - `calendar_event_id`
  - `calendar_title`
  - `calendar_completed`
  - `calendar_hidden`

CSV formatting rules:

- Use UTF-8.
- Include a header row.
- Escape commas, quotes, and newlines according to standard CSV rules.
- Defend against spreadsheet formula injection by safely prefixing exported cells that begin with `=`, `+`, `-`, `@`, tab, or carriage return.
- Store dates as ISO-style values:
  - `LocalDate`: `YYYY-MM-DD`
  - instants/timestamps: ISO-8601
- Blank optional fields should be empty cells, not the string `null`.
- Export should be shareable through Android's standard share sheet or saved through the system document picker.
- Do not upload CSV files automatically.
- CSV export should not mutate app state.

UI behavior:

- Settings -> Import/Export should offer CSV Export.
- CSV Export should ask what to export:
  - Current Backlog.
  - Historical Tasks.
- If Historical Tasks is chosen, show preset range buttons and custom date range controls.
- Show a short Included/Excluded summary before export.
- Generate the file only after the user taps an explicit export button.

## 20. Privacy And Security

Privacy posture:

- Offline-first.
- No account required.
- No analytics.
- No ads.
- No tracker SDKs.
- No Firebase by default.
- No Google Play Services dependency.
- No hidden cloud sync.
- No hidden telemetry.
- All core data stored locally.

Permissions:

- Request only when a feature needs them.
- Calendar permission only when using calendar features.
- Notification permission only when using reminders or at first appropriate setup moment.
- Photo/media permission only when attaching reminder images or adding media.

App lock:

- Support locking the app with a PIN.
- Support biometric unlock as an option.
- The user may enable PIN only, biometric only where platform policy allows, or PIN plus biometric.
- If biometric is enabled, keep PIN as a fallback unless Android platform rules or user settings make that impossible.
- Lock should trigger on app launch after enabled, after a configurable background timeout, and from a manual Lock Now action.
- Recommended timeout options:
  - Immediately.
  - After 30 seconds.
  - After 1 minute.
  - After 5 minutes.
  - After 15 minutes.
- Lock screen should be plain, calm, and fast.
- Do not show task contents, calendar contents, backlog titles, notification details, or game content while locked.
- Local notifications must still be delivered while the app is locked and encrypted.
- Notifications shown while locked must avoid exposing sensitive content. Use generic reminder text such as `Human Program reminder` or `Check your daily list`.
- Tapping a notification while the app is locked should open the lock screen first, then route to the intended screen after successful unlock.

Encryption:

- Data should be encrypted at rest.
- Use Android Keystore-backed keys.
- Prefer SQLCipher for Room database encryption or another well-reviewed Android database encryption path.
- Use EncryptedFile or equivalent authenticated encryption for exported temporary files, notification images where practical, and game save files when app lock/encryption is enabled.
- The encryption key should not be hardcoded.
- The PIN must not be stored in plaintext.
- Store only a strong salted hash or verifier for the PIN.
- Biometric unlock should release or authorize access to the app encryption key through Android Keystore/BiometricPrompt where practical.
- When the app is locked, sensitive decrypted state should be cleared from memory as much as practical.
- Full-database encryption should be treated as a required security goal, not a decorative lock screen only.

Recovery:

- If the user forgets the PIN and biometric is unavailable, the app should not silently bypass encryption.
- The acceptable recovery path is destructive reset after clear warnings, unless a future secure recovery feature is deliberately designed.
- Exported `.hprgm` backups may later support user-chosen encryption passwords, separate from the app PIN.

Additional GrapheneOS-aligned security:

- Do not request the `INTERNET` permission in the first offline-only build.
- When optional sync is eventually added, make network access an explicit feature with clear settings and no hidden background service.
- Do not use Play Integrity, SafetyNet, device attestation checks, root checks, or OS discrimination to block users.
- Do not require proprietary Google services for any core feature.
- Keep dependencies minimal and well-maintained.
- Do not use dynamic remote code loading, plugin downloading, remote JavaScript execution, or WebView-based app logic for core features.
- Avoid WebView entirely unless a specific future feature truly needs it.
- Do not request sensors, camera, microphone, contacts, location, Bluetooth, nearby devices, or broad storage permissions unless a feature explicitly needs them.
- Prefer Android's system document picker for import/export instead of broad file access.
- Support Android scoped storage cleanly.
- Support GrapheneOS Storage Scopes and Contact Scopes by working correctly with narrow user-granted access.
- Add a privacy setting to hide app content from the recent-apps preview and block screenshots/screen recordings for sensitive screens.
- Hide sensitive notification content when app lock is enabled.
- Avoid logging task titles, notes, calendar contents, file paths containing personal names, or decrypted data.
- Crash/error reporting must be local and user-initiated unless the owner later adds an explicit opt-in reporting flow.
- Add PIN attempt rate limiting with increasing delay after repeated failures.
- Consider an optional duress PIN later, but only with very clear warnings because it should destructively wipe local app data.
- Disable automatic OS cloud backup for encrypted app databases unless the backup is explicitly app-encrypted and user-controlled.
- Clear clipboard automatically only for secrets created by the app, not for normal user text.
- Keep game integration offline and local by default; the game must not add trackers, ad SDKs, analytics, or network permissions.

Future sync:

- Build repository boundaries so sync can be added later.
- Sync should be optional.
- Sync should be privacy-respecting.
- Sync should support a GrapheneOS-friendly path.
- Prefer user-owned sync, standard file export, WebDAV, local network sync, or explicitly chosen encrypted provider over proprietary default sync.

## 21. UI Direction

Overall feel:

- Calm.
- Utilitarian.
- Pleasant.
- Cozy.
- Human-friendly.
- Plain and straightforward.

Typography:

- Default font should be Georgia-like serif.
- Add a font setting early enough that typography is not hardcoded everywhere.
- Use serif for body-heavy areas where it feels cozy.
- Use clear sans-serif or medium-weight labels where dense utility screens need scanning.
- Do not scale font size with viewport width.
- Ensure long task titles wrap instead of truncating whenever possible.

Color:

- Use a restrained neutral base.
- Use multiple accents sparingly: blue for actions, green for completion, red only for destructive/locked states, muted warm/cool schedule colors.
- Avoid a single-hue theme.
- Avoid heavy purple gradients.
- Avoid decorative orbs/blobs.

Cards and surfaces:

- Use cards for repeated items, modules, and focused tools.
- Do not nest cards inside cards.
- Keep corner radii modest, around 8dp for utility cards unless a screen specifically benefits from softer containers.
- Daily schedule blocks should use sharp or modest rectangle corners, not bubbly decorative styling.

Controls:

- Use icons for common actions.
- Use text buttons only when the command needs clarity.
- Use segmented controls for modes.
- Use toggles for binary settings.
- Use sliders/steppers/pickers for numeric and date/time values.
- Use menus for option sets.
- Use tabs for top-level navigation.

Editing:

- Read mode and edit mode should remain visually stable.
- Entering edit mode should not cause rows to jump or change shape unnecessarily.
- Prefer selection checkmarks plus explicit toolbar actions.
- Do not rely on swipe-to-delete as the main deletion flow.
- Keyboard dismissal should work by tapping outside and by system back/done behavior.

## 22. Today Screen

Today is the most important screen.

Layout order:

1. Date header.
2. Past-day lock state if viewing past.
3. Daily Schedule.
4. Today’s Tasks.
5. Completion message when complete.
6. Exercise.

Header:

- Display full date.
- Toolbar supports previous day, next day, today, and date picker.
- Past dates show a lock control.
- Past dates are locked by default.
- Long-press or deliberate unlock action unlocks a past day temporarily.
- Past day re-locks when leaving the date or leaving Today.

Daily Schedule:

- Compact 24-hour vertical timeline.
- Time markers every 3 hours.
- Current-time indicator only on today.
- Schedule template blocks displayed in stable colors.
- Calendar events displayed as extra timeline segments.

Today’s Tasks:

- Combined checklist.
- Includes recurring, backlog, manual, and calendar-derived entries.
- Does not include exercise section items for completion unless they are also normal recurring tasks.
- Items keep stable order after completion.
- Checking an item does not move it.
- Shows add button when editable.
- Add button creates inline manual task entry.
- Edit mode supports selecting entries, deleting selected entries, and reordering.
- Calendar-derived deleted entries are hidden locally rather than deleted externally.
- Detail button opens task/calendar details.
- Metadata can show source and project bucket.
- Metadata hidden by default unless setting says otherwise.

Completion message:

```text
Congratulations, you are done for the day!
```

Show it directly below the task list when all required entries are complete.

Exercise:

- Shows weekday routine.
- Title plus item lines.
- Empty state when no routine exists.

## 23. Backlog Screen

Backlog main toolbar:

- Sort menu.
- View mode menu.
- Add menu.
- Select button.

Sort modes:

- Creation order.
- Date ascending.
- Date descending.
- Alphabetical A-Z.
- Alphabetical Z-A.

View modes:

- Item View.
- Project View.

Add menu:

- New Backlog Item.
- New Project.

Item View:

- Shows active backlog items.
- Row shows title.
- Row shows assigned date if present.
- Row shows project bucket if present.
- Info button opens item detail.
- Select mode supports multi-select, delete, and assign to project.

Project View:

- Shows folder rows.
- Each row shows project name and item count.
- Includes `Unorganized`.
- `Unorganized` cannot be selected for deletion.
- Selecting project folders supports project-level delete flows.
- Tapping a project opens that project’s item list.

Backlog detail:

- Read-first detail page.
- Shows title, assigned date, project bucket, notes, status.
- Edit button opens edit form.

Backlog form:

- Title required.
- Notes optional.
- Assign date toggle.
- Date picker when date is enabled.
- Project menu.
- Unsaved changes warning.

## 24. Calendar Screen

Calendar toolbar:

- Today button.
- Sync status button.
- View mode menu.
- Add event button when provider and permissions allow.

Views:

- Month.
- Week.
- Day.
- Agenda.

Month:

- Month grid.
- Selected day summary.
- Event markers.

Week:

- Seven-day timeline.
- Events positioned by time.
- Current-time line when viewing current week/day.

Day:

- Single-day timeline.
- All-day and timed events.

Agenda:

- Next 30 days with events.

Event rows:

- Show title.
- Show time or all-day.
- Show location if present.
- Show source calendar name.
- Show color marker.

Event details:

- Show title, time, location, notes, calendar name.
- Editing/deleting only when provider supports it.

## 25. Settings Detail Screens

General Settings:

- Date format options:
  - MM/DD/YYYY
  - DD/MM/YYYY
  - YYYY-MM-DD
  - Day Month Year
  - Month Day Year
  - Year Month Day
- Appearance:
  - Match System
  - Light
  - Dark
- Font:
  - Georgia-like default
  - system default
  - future custom options

Today Display:

- Metadata:
  - Hidden Unless Tapped
  - Visible by Default
- Show project bucket toggle.
- Show task source toggle.

Recurring Tasks:

- List recurring items.
- Add button.
- Edit button.
- Select/delete in edit mode.
- Row shows title, weekday summary, notes preview, and off badge.
- Editor supports title, active toggle, weekdays, every day shortcut, and notes.

Schedule:

- Weekday assignment summary row.
- List of schedule templates.
- Each row shows name, assigned weekdays or custom date range, and enabled toggle.
- Add button.
- Tapping row opens full editor.

Exercise:

- List all weekdays.
- Header shows weekday and custom routine label.
- Add item per day.
- Edit mode supports selection, delete, and reorder.
- Tapping an item outside edit mode edits inline.

About:

- Version.
- Build.
- Developer name.
- Cat Corner-style personal media gallery.
- Hidden document easter egg.
- Hidden Sudoku-style puzzle gate.
- Future game entry.

## 26. Puzzle Gate And Game Unlock

The app currently has a hidden 3x3 Sudoku-style number puzzle concept. In Human Program, treat this as the gate ritual or entry point to the future game.

Access flow:

1. User completes all Today’s Tasks.
2. `dayComplete` becomes true.
3. Game access for today becomes unlocked.
4. User goes to Settings -> About.
5. User opens the hidden puzzle gate or visible game entry, depending on the final UX.
6. If today is complete, the gate allows entry to the Game Container.
7. If today is incomplete, show a plain locked message and do not enter the game.

Puzzle behavior:

- 3x3 grid.
- One cell can be prefilled.
- Remaining cells accept digits 1-9.
- Puzzle is solved when all blanks are filled with the expected set of missing numbers and a hidden rule is satisfied.
- The puzzle is not the full game.
- The puzzle is a gate/ritual before entering the full game.

Future game:

- Prefer Godot integration.
- Keep game assets and save files separate from planner data.
- Load game only after access check passes.
- Do not initialize heavy game engine state on normal app launch if avoidable.
- The game should be able to pause/resume independently.
- Game state persists across days.
- Daily lock affects only whether user can open the game today.

If using an open-source game such as a puzzle/RPG/grid game, isolate it:

```text
game/
  engine/
  assets/
  saves/
  GameEntryActivity or GameComposableHost
gamebridge/
  GameAccessService
  GameUnlockRepository
```

GameAccessService API:

```text
canEnterGame(today: LocalDate): Boolean
getLockedReason(today: LocalDate): String
markGameOpened(today: LocalDate): Unit
```

Game container must not know how tasks are completed. It only gets `canEnterGame`.

## 27. About, Personal Media, And Easter Eggs

The About area is not only technical metadata. It also holds small personal, nonfunctional features that make the app feel owned and human.

About should include:

- Version name.
- Build number.
- Developer name.
- Cat Corner-style personal gallery.
- Hidden document easter egg.
- Hidden Sudoku-style puzzle gate.
- Future Game entry.

Personal gallery:

- Opens full screen.
- Uses a black viewing background.
- Supports swiping between bundled images.
- Supports a visible Done action.
- Can dismiss with a deliberate vertical swipe if implemented cleanly.
- Should not require network.
- Should not fetch images remotely.

Hidden document screen:

- Opens from a deliberate hidden gesture, such as double-tapping the version row.
- Displays a top image and a long readable document.
- Uses comfortable serif typography.
- Supports text selection if practical.
- Closes only with a clear Done action.

Hidden puzzle gate:

- Opens from a deliberate hidden gesture or from a later explicit game entry.
- Uses a dark, focused puzzle surface.
- Should not expose the full future game until the daily gate allows it.

Future design note:

- It is acceptable for the About page to keep hidden personal extras, but the actual full game should eventually become easier to find after the user earns access. The owner described the game as living in a second container inside the same app, bridged to the daily checklist. The About/puzzle entry can remain the doorway.

## 28. Local Backend Services

Recommended service boundaries:

DailyPageRepository:

- Get or create page for date.
- Update daily task.
- Add manual task.
- Delete tasks.
- Reorder task entries.
- Lock/unlock past day state.

DailyPageGenerator:

- Generate daily page from templates and assignments.
- Build snapshot records.
- Refresh today/future pages from templates.

BacklogRepository:

- CRUD backlog items.
- Assign dates.
- Assign projects.
- Delete projects.
- Reconcile overdue assignments.

RecurringTaskRepository:

- CRUD recurring templates.
- Active/inactive handling.
- Weekday logic.

ExerciseRepository:

- Ensure seven templates.
- CRUD exercise items.
- Reorder exercise items.
- Propagate to open pages.

ScheduleRepository:

- CRUD schedule templates.
- Conflict detection.
- Block normalization.
- Date-range overrides.

CalendarRepository:

- Provider adapter.
- Selected calendars.
- Event snapshots.
- Local completion/hidden/title/notes/order state.

StatsRepository:

- Recalculate streaks.
- Today counts.
- Future item-level stats.

ReminderRepository:

- CRUD reminders.
- Schedule local notifications.
- Store notification images.

ExportRepository:

- Create `.hprgm`.
- Export active backlog CSV.
- Export historical daily task CSV by date range.
- Later restore `.hprgm`.

SecurityRepository:

- Manage app lock settings.
- Manage PIN setup and verification.
- Manage biometric unlock state.
- Manage encryption key access.
- Report whether sensitive content may be shown.

GameAccessService:

- Determine daily game access.
- Provide lock reason.
- Keep game gate separate from task internals.

## 29. Persistence Recommendation

Use Room tables for:

- backlog_items
- project_buckets
- recurring_task_templates
- exercise_templates
- exercise_template_items
- schedule_templates
- schedule_blocks
- daily_pages
- daily_page_tasks
- daily_page_schedule_blocks
- calendar_local_state
- notification_reminders
- game_access_events
- game_save_metadata
- app_lock_settings

Use DataStore for:

- date format
- appearance
- font choice
- metadata visibility
- show project bucket
- show task source
- backlog display mode
- backlog sort mode
- calendar view mode
- selected calendar IDs
- welcome flags
- app lock enabled state
- biometric unlock enabled state
- lock timeout setting

Use app-private file storage for:

- notification images
- `.hprgm` temporary export build files
- game saves
- game assets that are generated or downloaded by explicit user action

## 30. Observed Personal Planning Content

The existing source material includes these planning examples. Do not hardcode them as default data unless the owner asks. They are useful examples for testing long text, projects, recurring tasks, schedules, and exercise routines.

Project buckets:

- Long term
- Long term misc

Recurring tasks:

- Exercise, every day.
- Study calendar, every day.
- Call parents, Thursday.
- Brainstorming/personal dev/social calendar/Dynalist, Saturday, with multi-line notes.
- Review anki, every day.

Exercise routines:

- Sunday: Cardio; Rucking/hiking; Running.
- Monday: Upper - Push; Smith machine OHP; Cable chest press; Cable lateral raise; Pec deck fly; Tricep pushdown.
- Tuesday: Lower - A; Leg press; Leg curl; Leg extension; Hip abductor machine; Forearm plank; Cable crunch.
- Wednesday: Rest day.
- Thursday: Upper - Pull; Lat pulldown; Seated cable row; Rear delt fly; Face pulls; Bicep curls.
- Friday: Lower - B; Smith machine squat; Romanian deadlift; Calf raise; Cable lateral raise; Deadbug/plank.
- Saturday: Recovery; Foam roll; Stretches; PT exercises.

Schedules:

- Weekday - GRE prep: assigned Monday through Friday; Sleep 21:30-05:30; Rise, gym, and ready for work 05:30-07:30; Work 07:30-15:30; Return home, rest 15:30-16:30; Study 16:30-20:30; Get ready for sleep 20:30-21:30.
- Weekday - normal: no active weekday assignment in the observed sample; Sleep 21:30-05:30; Rise, gym, and ready for work 05:30-07:30; Work 07:30-15:30; Return home, rest 15:30-16:30; Free time 16:30-20:30; Get ready for sleep 20:30-21:30.
- Weekend - GRE prep: assigned Sunday and Saturday; Sleep 21:30-05:30; Exercise and return 05:30-07:30; Study 07:30-17:30; Free time 17:30-20:30; Get ready for sleep 20:30-21:30.

Backlog examples:

- Test.
- Once I pass the GRE, I will begin my hair, skin, clothes, fitness, etc. transformation to attract the hottest of women.
- Work on body language: practice making proper prolonged eye contact with eyes wide open, mouth closed, competent body language.
- Gather all evidence and info for arm for the doctor.
- Come up with system of review of quant questions so I never forget. Anki does not seem to work for this purpose. I need control and visibility. I think I need to use spreadsheet software.
- I need to do something about sleep. Do I need to use sleep stories? Sleep meditation? Tire myself out? How can I make myself fall asleep well?
- Become an expert on Anki: features, use styles, add-ons, FSRS, themes, settings, visualizations, voice use, and AI use.
- Become an expert on Obsidian: YAML, plugins, and advanced features.
- Order Beauty of Joseon when running low on SPF.
- Talk with Claude on products that can reduce PIE, PIH, and fading of scars.
- Try to expand involvement in ENGin.
- Resolve old school records after transfer.
- Brainstorm how to become physically attractive.
- Restart social spreadsheet and update it during Saturday brainstorm sessions.
- Exercise is non-optional.
- Enforce the daily schedule.
- Journal and change my life; use Dynalist heavily; prioritize exercise and getting stronger.
- Speedrun WGU as much as possible, set fashion style, and think of leadership/initiative demonstrations.
- Start German using Anki and Babbel.
- Check out DASH diet.
- Set up doctor appointment for neurology.

Observed dataset shape:

- 21 backlog items.
- 2 project buckets.
- 5 recurring tasks.
- 7 exercise templates.
- 3 schedule templates.
- 82 generated daily pages.
- Longest streak: 3.
- Current streak: 0.
- Generated daily pages included recurring, backlog, manual, and exercise source types.

## 31. Beginner Collaboration Rules

When an AI agent works with the human owner:

- Assume the owner is a beginner.
- Use plain language.
- Avoid jargon.
- Explain necessary terms simply.
- Keep explanations short unless the owner asks for depth.
- Tell the owner exactly what changed.
- Tell the owner exactly what to test.
- Do not expect the owner to understand file structure.
- Do not jump into large unrelated changes.
- If the owner says to work on one tab or area only, stay in that area.
- Treat unreviewed work as provisional until the owner has tested it.
- Update this spec or a future project notes file when standing instructions change.
- Ask only follow-up questions that are truly needed.

Engineering habits:

- Preserve user data.
- Ask before destructive changes.
- Keep app usable after every significant chunk.
- Commit significant changes only after behavior is actually working.
- Prefer simple durable architecture.
- Document assumptions.
- Keep template-generated past pages stable.
- Keep local-first behavior central.
- Keep game architecture open and isolated.

## 32. Build Instructions In Beginner Terms

These are the setup choices the Android project should use. They are included so an AI agent in Android Studio can create the project without asking the beginner owner to understand every technical detail first.

Use:

- Kotlin as the programming language.
- Jetpack Compose for screens.
- Material 3 for the base component library.
- `app.humanprogram.android` as the package name.
- API 31 as the minimum supported Android version.
- Latest stable target SDK available in the local Android Studio install.
- Room for the local database.
- DataStore for preferences.
- App-private file storage for images, exports, and game saves.
- Local notifications for reminders.
- Optional Android calendar-provider permissions for calendar features.

Do not add:

- A server backend.
- Required sign-in.
- Google account dependencies.
- Firebase.
- Analytics.
- Ad SDKs.
- Remote logging.
- Cloud sync in the first build.

The first Android build should be boring in the best way: local, durable, testable, and understandable.

## 33. Implementation Phases

### Phase 1: Project Foundation

- Create native Android project.
- Package `app.humanprogram.android`.
- Kotlin + Jetpack Compose.
- Material 3 theme.
- Georgia-like default font setup.
- Bottom navigation.
- Room database.
- DataStore preferences.
- Repository/viewmodel structure.

### Phase 2: Core Planning Data

- Implement entities and DAOs.
- Implement DailyPageGenerator.
- Implement recurring tasks.
- Implement backlog items.
- Implement exercise templates.
- Implement schedule templates.
- Implement app startup initialization.

### Phase 3: Today

- Date navigation.
- Daily schedule.
- Today’s Tasks list.
- Manual task creation.
- Completion toggles.
- Completion message.
- Past-day lock/unlock.
- Exercise section.
- Metadata visibility.

### Phase 4: Backlog

- Item View.
- Project View.
- Sorting.
- Create/edit/detail.
- Multi-select.
- Assign to project.
- Project deletion choices.
- Text import.
- CSV import with preview.
- CSV export for current backlog.
- CSV export for historical daily task data by date range.

### Phase 5: Settings

- General settings.
- Today Display settings.
- Recurring Tasks.
- Schedule editor.
- Exercise editor.
- Factory reset.
- About page.

### Phase 6: Calendar Adapter

- Permission flow.
- Calendar source selection.
- Month/week/day/agenda views.
- Today calendar-derived entries.
- Local completion/hide/title/notes/order state.
- Sync status.

### Phase 7: Notifications

- Reminder list.
- Reminder editor.
- One-time reminders.
- Interval reminders.
- Local scheduling.
- Image attachments.
- Permission-safe behavior.

### Phase 8: Stats

- Current streak.
- Longest streak.
- Today status.
- Task completion count.
- Prepare for future item-level stats.

### Phase 9: Export

- `.hprgm` ZIP export.
- `.hprgm` import requires PIN/biometric unlock when app lock is enabled.
- Manifest.
- Planning JSON.
- Preferences JSON.
- Calendar local state JSON.
- Notifications JSON.
- Optional media folders.
- Current backlog CSV export.
- Historical daily task CSV export with date-range presets.

### Phase 10: App Lock And Encryption

- PIN setup.
- PIN unlock.
- Biometric unlock option.
- Lock timeout.
- Manual Lock Now.
- Sensitive-content hiding while locked.
- Safe local notifications while locked.
- Database encryption.
- Encrypted file storage for sensitive files where practical.
- Destructive reset path for forgotten PIN.

### Phase 11: Game Bridge

- GameAccessService.
- Daily completion gate.
- Settings/About entry.
- Puzzle gate.
- Game placeholder container.
- Persistent game save metadata.

### Phase 12: Game Integration

- Choose open-source game codebase.
- Isolate game module.
- Add engine host.
- Connect gate to entry.
- Preserve game saves.
- Keep planner stable.

## 34. Required Tests

Unit tests:

- Daily page generated once per date.
- Recurring tasks apply to correct weekdays.
- Inactive recurring tasks do not generate.
- Backlog assigned to date appears on that daily page.
- Completing matching backlog-derived daily task marks backlog item done.
- Unchecking matching current/future backlog-derived task restores backlog item.
- Overdue incomplete backlog item loses assigned date.
- Past daily task edits do not mutate active backlog item.
- Exercise excluded from day completion.
- Calendar-derived entries included in day completion.
- Empty Today’s Tasks does not complete day.
- Adding unchecked manual task clears completion.
- Streaks calculate correctly.
- Schedule custom date override wins.
- Schedule conflict detection blocks overlaps.
- Schedule block normalization preserves order and duration.
- `.hprgm` export contains expected files.
- Current backlog CSV contains expected active backlog rows and headers.
- Historical task CSV respects last 90 days and custom date ranges.
- Game unlock returns false before day completion.
- Game unlock returns true after all tasks complete.
- Game progress is not deleted when next day locks.
- PIN lock blocks sensitive screens.
- Biometric unlock works when enabled and available.
- App data remains encrypted at rest.
- Forgotten PIN recovery does not bypass encryption.

UI tests:

- Today date navigation.
- Past-day lock prevents edits.
- Long-press/unlock or deliberate unlock enables past-day edit.
- Backlog Item View and Project View.
- Project deletion confirmation.
- Recurring task editor unsaved warning.
- Schedule editor unsaved warning.
- Exercise editor add/edit/delete/reorder.
- Reminder create/edit/delete.
- Calendar permission denied state.
- Export flow.
- CSV export flow for backlog and historical tasks.
- App lock setup, lock timeout, PIN unlock, biometric unlock, and Lock Now.
- Game entry locked/unlocked states.

Manual QA:

- Run on a Pixel emulator.
- Run on at least one non-Pixel Android emulator profile when practical.
- Run on a real Pixel if available.
- Run on a non-Pixel Android phone if available.
- Run on GrapheneOS if available.
- Run without network.
- Run without Google account.
- Run with calendar permission denied.
- Run with notifications denied.
- Rotate if supported.
- Test dark mode.
- Test long task titles.
- Test large backlog.

## 35. Glossary

Package name:

The unique technical ID Android uses for the app. Human Program uses `app.humanprogram.android`.

Room:

Android’s standard local database library. Use it for structured app data.

DataStore:

Android’s modern preference storage. Use it for small settings.

Repository:

A class that owns data access for one area, such as backlog or schedules.

ViewModel:

A class that prepares screen state and receives user actions.

Compose:

Android’s modern UI toolkit.

Offline-first:

The app works fully without internet. Sync may be added later, but is not required.

GrapheneOS-compatible:

The app avoids proprietary Google dependencies and works with standard Android APIs.

Snapshot:

A copied record of a generated day. Later template changes do not rewrite past snapshots.

Game gate:

The rule that blocks game access until all of today’s required tasks are complete.

## 36. Final Definition

Human Program is a private, offline-first daily execution app with a future game container. Its core is a generated daily page that turns recurring tasks, backlog assignments, calendar obligations, exercise routines, and schedule blocks into one required checklist and daily view. Completing every required task unlocks the game for that day. The planner and the game remain separate containers connected by a small access bridge, so the app can grow into both a serious daily operating tool and a full game without one architecture damaging the other.
