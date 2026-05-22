# Human Program UI Development Guide

This file is the detailed UI/UX implementation guide for Human Program. It is written for AI coding agents and future engineers, not for casual product reading. It translates owner feedback, current-app audit findings, external UI research, and the supplied like/dislike reference images into precise implementation rules.

Use this guide when redesigning or coding any Human Program interface. `ADD.md` remains the authoritative product spec. `BUILD_STATUS.md` remains the progress tracker. This file explains how the UI should be built so the app feels like a polished published product instead of a default Android prototype.

## 1. Product Posture

Human Program is a daily command center.

It is not:

- a brainstorming app
- a notebook
- a generic task demo
- a Material component sample
- a settings portal
- a Trello clone
- a calendar clone only
- a bottom-tab productivity toy

The owner workflow is:

1. On the weekend, plan the week.
2. Enter the week/program into the app.
3. During the week, open the app to execute the program.
4. Make minor changes when reality changes.
5. Use Today as home base.
6. Use Backlog/projects as the task/project source.
7. Use Calendar both as a serious calendar view and as a source that feeds Today.
8. Use Routines, Reminders, Stats, Import/Export, App Lock, and Appearance as first-class app areas.
9. Keep the hidden game out of normal UI planning until its container exists.

The first-run/factory state must work well without customization. Customization mostly affects appearance, display density, tracking/dashboards, routines, and app preferences. Today section order is fixed; the user should not rearrange Today widgets.

## 2. Owner Taste Inputs

### Liked Reference Set

The owner liked reference images with the following qualities:

- spacious mobile dashboards
- top-left menu/back affordances and top-right compact icon controls
- no bottom-tab dependence
- strong first-screen focal point
- soft surfaces and subtle depth
- readable section grouping
- meaningful cards rather than every control living in a card
- dedicated add/edit screens instead of inline permanent forms
- simple, large, touchable controls where actions matter
- calm text hierarchy
- polished empty space
- low visual noise
- custom visual language instead of stock Android defaults
- black/full-screen special surfaces when the content calls for ceremony
- date selection as an intentional screen/surface, not only previous/next buttons

What to borrow:

- hierarchy
- spacing
- soft but disciplined surfaces
- iconic chrome
- focused add/edit flows
- elegant command surfaces
- visual grouping
- strong Today/home identity

What not to borrow blindly:

- low contrast pastel text
- overlarge decorative cards that reduce execution density
- fake concept-app navigation that would slow daily use
- novelty controls without a practical mental model
- excessive gradients or glass effects
- purely aesthetic hero panels that hide the work

### Disliked Reference Set

The owner disliked references with these traits:

- default Android teal app bars
- generic checkbox/radio/list demo screens
- heavy stock component look
- bottom tabs as the primary product shape
- settings/action menu dumps
- too many controls visible at once
- boxy enterprise portal layouts
- bureaucratic card grids
- visible forms everywhere
- harsh default buttons
- poor hierarchy
- actions organized as junk drawers
- stock web-form styling
- UI that feels like a component library test page

Human Program must not inherit these traits.

## 3. External Research Synthesis

These are the outside design findings this guide applies. Links are included for future agents.

### Android App Quality

Android's quality guidance says high-quality apps are intuitive, delightful, differentiated, and visually crafted. It explicitly calls out a strong identity, visual appeal, coherent product surfaces, whitespace, color, and hierarchy as part of quality, not decoration.

Source: https://developer.android.com/quality/user-experience

Implementation consequence:

- Do not rely on default Material styling as the product identity.
- Use platform primitives where they help, but wrap them in Human Program's own design language.
- Treat visual polish as functional. The interface should make the program feel trustworthy, orderly, and calm.

### Android Layout Basics

Android's layout guidance emphasizes safe areas, different screen sizes, containment, alignment, consistent spacing, and not overwhelming a view with too many actions.

Source: https://developer.android.com/design/ui/mobile/guides/layout-and-content/layout-basics

Implementation consequence:

- Every screen must honor status bars, navigation bars, display cutouts, edge displays, and IME/keyboard insets.
- The UI must be tested on Pixel Pro-style centered camera cutouts.
- Containment is for related content and actions, not for wrapping every label in a card.
- Similar elements must share consistent alignment and spacing.
- Essential interactions must be visible or quickly reachable; secondary interactions must not crowd the main view.

### Android Settings Guidance

Android settings guidance says settings should contain infrequently accessed preferences, not frequently used feature actions. It recommends clear language, groups/screens when needed, polite defaults, saving preferences, and contextual placement of frequently used controls near the feature they affect.

Source: https://developer.android.com/design/ui/mobile/guides/patterns/settings

Implementation consequence:

- Settings root must not be one long expanded page.
- Settings is a menu of focused detail pages.
- Frequently used actions belong in the feature screen or its contextual command menu, not buried in Settings.
- Settings rows should show status summaries, not full editors.

### Compose Custom Design Systems

Jetpack Compose permits custom design systems. Material is recommended but not mandatory, and Material components can be used within a custom theme. Official Compose docs describe extending or replacing Material systems and using `CompositionLocal` theme tokens.

Sources:

- https://developer.android.com/develop/ui/compose/designsystems/custom
- https://developer.android.com/develop/ui/compose/designsystems/anatomy
- https://developer.android.com/develop/ui/compose/designsystems/material3

Implementation consequence:

- Build a Human Program design system layer instead of dropping raw `Button`, `OutlinedTextField`, `Card`, `NavigationBar`, and `TopAppBar` everywhere.
- Material 3 can remain a low-level foundation, but product UI must use Human Program components and tokens.
- Define tokens for colors, typography, spacing, radii, elevation, motion, and density.
- Prefer custom `Hp*` components that encode product rules.

### Adaptive Layouts

Android recommends window size classes and centralized adaptive decisions. Do not scatter display-size logic throughout every composable.

Source: https://developer.android.com/develop/ui/compose/layouts/adaptive/support-different-display-sizes

Implementation consequence:

- Centralize screen class decisions in the app shell.
- Phone compact width: Today-first stack with menu sheet.
- Medium/expanded width: optional navigation rail or side panel, but only after phone shell is polished.
- Keep components reusable by avoiding hidden dependencies on global screen size.

### Insets And Cutouts

Compose provides `WindowInsets`, including system bars, safe drawing areas, and display cutouts.

Source: https://developer.android.com/develop/ui/compose/system/insets

Implementation consequence:

- Use `WindowInsets.safeDrawing`, `statusBarsPadding`, `navigationBarsPadding`, `imePadding`, or equivalent safe wrappers.
- Do not place primary headings or buttons under a cutout.
- Do not use fixed top padding guesses.

### NN/g Aesthetic-Usability Effect

Users perceive attractive interfaces as more usable and are more tolerant of minor issues when the visual design is appealing. But attractive UI cannot compensate for severe usability issues.

Source: https://www.nngroup.com/articles/aesthetic-usability-effect/

Implementation consequence:

- Polished visuals matter because they change trust, patience, and perceived quality.
- Visual design must support actual execution. Do not sacrifice task clarity for aesthetics.
- A pretty but inefficient command center fails the product.

### NN/g Progressive Disclosure

Progressive disclosure shows the most important options first and makes specialized options available on request. This helps satisfy both power and simplicity.

Source: https://www.nngroup.com/articles/progressive-disclosure/

Implementation consequence:

- Read mode is the default.
- Edit mode, add flows, bulk actions, delete, reorder, import, and advanced settings are available but not permanently exposed.
- Hiding clutter is not disabling features; it is professional disclosure.
- The split between primary and secondary controls must match the owner's workflow.

### NN/g Usability Heuristics

Relevant heuristics include user control/freedom, consistency/standards, error prevention, recognition rather than recall, flexibility/efficiency, and aesthetic/minimalist design.

Source: https://www.nngroup.com/articles/ten-usability-heuristics/

Implementation consequence:

- Support undo/redo for reversible edits.
- Use platform and industry conventions enough that the app feels learnable.
- Prevent high-cost errors with constraints and staged flows.
- Make current context and available actions recognizable.
- Allow power-user efficiency through quick add, search, menus, and gestures after the basic path is clear.
- Remove irrelevant/rarely needed information from primary views.

### NN/g Visual Hierarchy

Visual hierarchy is created with color/contrast, scale, grouping, proximity, and common regions. Screens with too many equal-weight elements feel cluttered and hard to parse.

Source: https://www.nngroup.com/articles/visual-hierarchy-ux-definition/

Implementation consequence:

- Every screen must have one primary focal point.
- Use contrast and scale sparingly to show importance.
- Use whitespace/proximity before borders/cards.
- Use the squint test: if a blurred screen does not show a clear structure, the UI is not done.

### Apple HIG Influence

Apple's current HIG emphasizes hierarchy, harmony, consistency, layout, color, typography, and platform-native interaction. Even though Human Program is Android-native, these principles are platform-independent enough to inform product polish.

Source: https://developer.apple.com/design/human-interface-guidelines

Implementation consequence:

- Chrome should not dominate content.
- Controls should feel harmonious with device shape and screen edges.
- Typography, spacing, icons, and surfaces need one coherent language.
- The app should feel native, but not generic.

## 4. Current UI Audit

Current primary UI file:

```text
app/src/main/java/app/humanprogram/android/ui/HumanProgramApp.kt
```

Current audit facts:

- The file is about 1,836 lines, too large for polished product iteration.
- App shell uses `Scaffold` with a persistent `TopAppBar` title of `Human Program`.
- Main navigation uses a bottom `NavigationBar` with Today, Backlog, Calendar, Routines, Settings.
- Top-level pages repeat obvious page headers.
- Today uses three equal-width text buttons for Previous/Today/Next instead of a polished date control.
- Today shows an always-visible undo/redo bar.
- Today shows a standalone Calendar card instead of integrating events into Schedule and Tasks.
- Today exposes a `New task` text field and `Add` button inline.
- Task rows use always-visible `OutlinedTextField` values and delete icons.
- Backlog exposes a large segmented control for Items/Projects.
- Backlog exposes `New backlog item`, `Project`, and `Add` controls inline.
- Backlog item cards use editable text fields in read mode.
- Project actions are always-visible text buttons.
- Calendar uses a large segmented Month/Week/Day/Agenda control.
- Calendar screen is mostly permission/status copy and editor rows, not a real calendar surface.
- Routines exposes an inline text field/add button and placeholder cards.
- Settings is one giant expanded page with Stats, Recurring Tasks, Schedule, Exercise, Import/Export, Notifications, Calendar Permission, Privacy, Local Reset, App Lock, and About all rendered inline.
- Settings includes many editors directly on root.
- Hidden Sudoku gate is inline inside About/Settings.
- Theme is a simple Material 3 light/dark color scheme with serif headings but no custom design system tokens, spacing system, component language, or screen shell.
- Many rows use stock `OutlinedTextField`, `OutlinedButton`, `Button`, `Checkbox`, `Card`, and `SegmentedButton` directly.

Main problem:

The app currently exposes implementation state, not product intent. It looks like a working internal prototype because every available feature was made visible immediately.

## 5. North-Star UX Architecture

### Primary Mental Model

Human Program has one home:

```text
Today
```

Everything else supports Today:

```text
Backlog/Projects -> feeds Today
Calendar -> feeds Today and provides standalone calendar review
Routines -> repeatable workflows/templates
Reminders -> execution support
Stats/Dashboards -> feedback and tracking
Settings -> app behavior/preferences
Import/Export -> ownership/backups
App Lock -> privacy/security
```

### Navigation Rule

Remove bottom tabs from the phone UI.

Use a Today-first app shell:

- default route: Today
- top-left: Program menu button
- top-center/left content: current date/program status, not app title
- top-right: compact contextual actions
- menu sheet/drawer: top-level destinations
- detail screens: pushed onto a stack with back affordance
- add/edit flows: modal bottom sheets or dedicated edit screens

### Proposed Phone Navigation

Root shell:

```text
┌─────────────────────────────┐
│ [menu]  Thu, May 14   [+][⋯]│
│                             │
│ Today command center        │
│                             │
└─────────────────────────────┘
```

Program menu sheet:

```text
Program
Today
Backlog
Projects
Calendar
Routines
Reminders
Stats
Import / Export
Settings
```

Menu behavior:

- Opens as a side drawer on wide screens, modal sheet on phones.
- Includes concise status summaries where useful.
- Does not become a Trello-style junk drawer.
- Does not include hidden game.

### Route Model

Recommended route taxonomy:

```kotlin
sealed interface HpRoute {
    data object Today : HpRoute
    data object Backlog : HpRoute
    data class ProjectDetail(val projectName: String) : HpRoute
    data object Calendar : HpRoute
    data object Routines : HpRoute
    data object Reminders : HpRoute
    data object Stats : HpRoute
    data object ImportExport : HpRoute
    data object SettingsRoot : HpRoute
    data class SettingsDetail(val section: SettingsSection) : HpRoute
    data object AppLock : HpRoute
    data object Onboarding : HpRoute
    data object HiddenSudokuGate : HpRoute
}
```

Use Jetpack Navigation Compose or a small explicit route state, but keep routing separate from screen rendering.

## 6. App Shell Rules

### Do

- Use a custom Human Program shell component.
- Respect insets.
- Place Today status/date in chrome, not the app name.
- Use compact icons for menu, date picker, search, add, overflow.
- Keep contextual actions near the screen they affect.
- Use command sheets/menus for secondary actions.
- Keep top chrome visually light.
- Let content own the screen.

### Do Not

- Do not use bottom tabs on phone.
- Do not show persistent `Human Program` title.
- Do not repeat page titles that duplicate route names.
- Do not dump every available action in the top bar.
- Do not use default Material top app bar styling without product customization.
- Do not hide safe-area/cutout issues with arbitrary padding.

### Shell Skeleton

```kotlin
@Composable
fun HpAppShell(
    route: HpRoute,
    chrome: HpChromeState,
    onMenu: () -> Unit,
    onPrimaryAction: () -> Unit,
    onOverflow: () -> Unit,
    content: @Composable PaddingValues.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HpTheme.colors.canvas)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
    ) {
        Column(Modifier.fillMaxSize()) {
            HpCommandBar(
                state = chrome,
                onMenu = onMenu,
                onPrimaryAction = onPrimaryAction,
                onOverflow = onOverflow
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                content(PaddingValues())
            }
        }
    }
}
```

## 7. Human Program Design System

The UI should use custom components and tokens. Raw Material components may exist inside these wrappers, but screens should not be composed directly from raw Material primitives.

### Package Structure

Split UI into at least:

```text
app/src/main/java/app/humanprogram/android/ui/
  HumanProgramApp.kt
  navigation/
    HpRoute.kt
    HpNavigator.kt
  shell/
    HpAppShell.kt
    HpCommandBar.kt
    HpProgramMenu.kt
    HpOverflowMenu.kt
  design/
    HpTheme.kt
    HpColors.kt
    HpTypography.kt
    HpSpacing.kt
    HpShapes.kt
    HpElevation.kt
    HpMotion.kt
    HpComponents.kt
  screens/
    today/
    backlog/
    calendar/
    routines/
    reminders/
    stats/
    importexport/
    settings/
    security/
    onboarding/
  components/
    HpListRow.kt
    HpSection.kt
    HpTaskRow.kt
    HpProjectRow.kt
    HpCalendarTimeline.kt
    HpEmptyState.kt
    HpFormSheet.kt
    HpDatePickerSheet.kt
```

### Theme Tokens

Recommended token model:

```kotlin
@Immutable
data class HpColors(
    val canvas: Color,
    val canvasRaised: Color,
    val surface: Color,
    val surfaceSoft: Color,
    val surfaceStrong: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentOn: Color,
    val success: Color,
    val successSoft: Color,
    val warning: Color,
    val danger: Color,
    val divider: Color,
    val scrim: Color
)

@Immutable
data class HpSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp
)

@Immutable
data class HpRadii(
    val chip: Dp = 999.dp,
    val control: Dp = 14.dp,
    val card: Dp = 22.dp,
    val sheet: Dp = 28.dp,
    val small: Dp = 10.dp
)

@Immutable
data class HpElevation(
    val none: Dp = 0.dp,
    val low: Dp = 1.dp,
    val medium: Dp = 6.dp,
    val high: Dp = 16.dp
)
```

### Theme Access

```kotlin
private val LocalHpColors = staticCompositionLocalOf { lightHpColors() }
private val LocalHpSpacing = staticCompositionLocalOf { HpSpacing() }
private val LocalHpRadii = staticCompositionLocalOf { HpRadii() }
private val LocalHpElevation = staticCompositionLocalOf { HpElevation() }

object HpTheme {
    val colors: HpColors
        @Composable get() = LocalHpColors.current
    val spacing: HpSpacing
        @Composable get() = LocalHpSpacing.current
    val radii: HpRadii
        @Composable get() = LocalHpRadii.current
    val elevation: HpElevation
        @Composable get() = LocalHpElevation.current
}
```

### Material Bridge

Material can provide typography baseline and component internals, but Human Program colors should be explicit.

```kotlin
@Composable
fun HumanProgramTheme(
    appearance: AppearanceMode,
    content: @Composable () -> Unit
) {
    val dark = when (appearance) {
        AppearanceMode.SYSTEM -> isSystemInDarkTheme()
        AppearanceMode.LIGHT -> false
        AppearanceMode.DARK -> true
    }

    val hpColors = if (dark) darkHpColors() else lightHpColors()
    val materialColors = if (dark) hpDarkMaterialScheme(hpColors) else hpLightMaterialScheme(hpColors)

    CompositionLocalProvider(
        LocalHpColors provides hpColors,
        LocalHpSpacing provides HpSpacing(),
        LocalHpRadii provides HpRadii(),
        LocalHpElevation provides HpElevation()
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = hpTypography(),
            shapes = hpMaterialShapes(),
            content = content
        )
    }
}
```

### Color Direction

The current beige/green/brown palette risks feeling one-note. Keep warmth, but introduce a more modern, authored palette:

Light mode:

- canvas: warm white, not yellow
- surface: near-white with slight cool/warm tint
- text: near-black neutral
- secondary text: muted graphite
- accent: saturated but calm blue/violet or blue-green
- success: green only for completion
- warning/danger: reserved
- schedule colors: multiple muted categories, not one hue

Dark mode:

- canvas: deep neutral charcoal or near-black, not pure default Material dark gray
- surface: slightly raised charcoal
- text: warm off-white
- accent: vivid enough to read
- completed states must remain visible
- avoid low-contrast gray-on-gray

Do not:

- default to Material teal/purple
- use a one-hue palette
- use large gradients as the core UI language
- make every card the same beige/brown/tan
- use red unless destructive/error

### Typography

Human Program can retain a Georgia-like serif influence, but do not overuse serif headings everywhere. A polished productivity app needs readable task text.

Recommended:

- Primary body/task text: clean sans-serif or system font.
- Display/date headings: optional serif or humanist display style.
- Section labels: small sans-serif, medium weight.
- Metadata: smaller, muted, high legibility.
- Numeric stats/time: tabular figures if available.

Avoid:

- giant page titles inside every screen
- all caps for ordinary UI
- negative letter spacing
- body text that looks like a marketing hero
- low-contrast placeholder-style text for real content

### Spacing

Use consistent spacing:

- screen horizontal padding: 20-24 dp on phones
- section gap: 20-28 dp
- row vertical padding: 12-16 dp
- row inner gap: 8-12 dp
- card padding: 16-20 dp
- command bar height: content-based, usually 56-64 dp plus safe inset
- minimum touch target: 48 dp for icon/action controls

### Shapes

Use rounded surfaces, but keep them purposeful:

- icon buttons: circular or soft capsule
- text fields/sheets: 16-24 dp
- major dashboard cards: 20-28 dp
- list rows: 14-18 dp when contained
- do not nest rounded cards inside rounded cards
- avoid all surfaces having identical radius

### Elevation And Shadow

Use subtle depth:

- prefer tonal separation and whitespace before shadows
- use low shadow for floating command buttons and sheets
- avoid heavy web-card shadows
- avoid everything looking flat and table-like

## 8. Component Rules

### `HpCommandBar`

Purpose:

- primary navigation/menu
- date/current context
- primary contextual action
- overflow

Must:

- be inset-safe
- use icon buttons, not text-button rows
- not show app title except rare onboarding/about contexts
- not crowd more than 3 right-side actions

### `HpProgramMenu`

Purpose:

- access top-level app areas without bottom tabs

Rows:

- Today
- Backlog
- Projects
- Calendar
- Routines
- Reminders
- Stats
- Import/Export
- Settings

Optional row metadata:

- Today: completion count
- Backlog: active count
- Calendar: selected date/events
- Reminders: enabled count

Do not:

- include hidden game
- show every settings detail as a top-level menu row
- create nested junk drawers

### `HpSection`

A section is not always a card.

Use section styles:

- `PlainSection`: header + rows, no card
- `SoftPanelSection`: soft background for a genuinely grouped block
- `HeroStatusPanel`: one per screen maximum
- `TimelineSection`: schedule/calendar

Do not wrap every list item in a card. Use rows.

### `HpListRow`

Polished list row behavior:

- leading icon/check/status optional
- title primary
- subtitle/metadata secondary
- trailing value/action optional
- row menu optional
- consistent height
- no exposed text field in read mode

Skeleton:

```kotlin
@Composable
fun HpListRow(
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(HpTheme.radii.control))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = HpTheme.spacing.lg, vertical = HpTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HpTheme.spacing.md)
    ) {
        leading?.invoke()
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = HpTheme.colors.textPrimary)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = HpTheme.colors.textSecondary)
            }
        }
        trailing?.invoke()
    }
}
```

### `HpFormSheet`

All creation/edit forms should use focused surfaces:

- Add task
- Edit task
- Add backlog item
- Edit project
- Add reminder
- Edit schedule block
- Add routine
- Import preview
- Reset confirmation

Rules:

- A form sheet has a clear title, primary action, cancel/done path.
- Fields are grouped.
- Default values are sensible.
- Keyboard actions are configured.
- The sheet should not look like a web form.
- Avoid outlined boxes for every field if filled/tonal fields better match product style.

### Text Fields

Text fields are for edit mode and forms only.

Do:

- use tonal filled fields with soft background
- keep labels concise
- use placeholders sparingly
- validate inline when needed
- separate read rows from edit fields

Do not:

- display task title as `OutlinedTextField` in read mode
- expose empty add fields on main screens
- use raw border-heavy fields everywhere

### Buttons

Use button hierarchy:

- Primary filled button: one per form/surface.
- Secondary tonal/outlined button: alternate actions.
- Text button: low-emphasis commands.
- Icon button: chrome and row actions.
- Destructive action: hidden behind menu/edit mode unless in explicit destructive flow.

Do not:

- create rows of text buttons for navigation
- use large blue default buttons everywhere
- expose delete as a red always-visible row control

## 9. Read Mode And Edit Mode

Read mode is default.

Edit mode is explicit and per screen/context.

### Read Mode

Shows:

- clean task rows
- completion checkboxes
- schedule timeline
- project rows
- status summaries
- selected date
- necessary primary action

Hides:

- text fields
- delete buttons
- reorder controls
- bulk-select controls
- import text areas
- CSV preview blobs
- reset fields
- hidden gate

### Edit Mode

Shows:

- editable fields
- add buttons
- delete/menu actions
- reorder handles
- selection controls
- undo/redo access

### State Model

```kotlin
enum class HpInteractionMode {
    READ,
    EDIT,
    SELECT
}

data class ScreenChromeState(
    val routeTitle: String?,
    val dateLabel: String?,
    val mode: HpInteractionMode,
    val primaryAction: HpAction?,
    val overflowActions: List<HpAction>
)
```

### Layout Stability

Read mode and edit mode must not move content. Switching modes may change icons, backgrounds, borders, and whether fields accept input, but the outer size, spacing, and text positions of the page must stay fixed. Use the same layout container for read and edit states, especially on task/detail pages, so titles, metadata rows, section labels, and note content do not jump when editing is toggled.

### Undo/Redo

Undo/redo must remain available but not as a visible row in normal content.

Recommended:

- overflow menu items
- transient snackbar after destructive/reversible action: `Task deleted` + `Undo`
- keyboard shortcut later if hardware keyboards matter

## 10. Screen Redesign Specifications

## 10.1 Today

Today is the app home and daily command center.

### Goals

- Answer "what do I execute now?"
- Show current day status.
- Integrate tasks, schedule, calendar, exercise.
- Support quick add and quick edit without clutter.
- Allow date jump.
- Keep future/past behavior clear.

### Layout

Top command bar:

- left: menu
- center/left: date chip, e.g. `Thu, May 14`
- optional sublabel below/chip: `3 of 8 complete`
- right: calendar/date picker icon, add icon, overflow icon

Content order:

1. Day status / progress panel
2. Schedule timeline with calendar events integrated
3. Today's required tasks
4. Exercise
5. Small completion/history/status footer if needed

### Day Status Panel

Should be compact and functional:

- date
- completion count
- progress
- current time/next block
- day state: Today / Future preview / Past locked / Past unlocked

Avoid:

- giant greeting unless it improves daily execution
- repeated `Today` title
- marketing copy

### Date Navigation

Replace Previous/Today/Next text-button row.

Use:

- date chip opens date picker
- left/right chevrons optional near chip
- "Today" jump in date picker or overflow
- current date visually distinct

### Schedule Timeline

Merge:

- schedule blocks
- device calendar events from selected calendars
- current-time indicator for today

Use:

- vertical timeline or compact agenda
- time rail
- colored segments
- tap calendar event for local controls/details

No standalone Today Calendar card.

### Task List

Read mode row:

```text
[checkbox] Task title                         [metadata/due/project/menu]
           optional subtle source/project
```

Rules:

- Checking does not reorder.
- Calendar-derived task completion is local.
- Calendar-derived task hide is local.
- Task title is text, not field, in read mode.
- Row click opens detail or toggles based on chosen interaction convention; avoid ambiguity.
- Long press or row menu can reveal edit actions.

Edit mode:

- task text fields or edit sheet
- reorder handle
- delete via row menu/selection
- add task button visible

Quick add:

- top-right `+`
- opens add-task sheet
- supports title, optional project/source, date, schedule time if needed

### Past/Future

Past:

- locked by default
- compact lock indicator
- deliberate unlock action in overflow or status panel
- no giant warning card unless necessary

Future:

- preview mode
- generated/preview source clear
- editing rules follow spec

## 10.2 Backlog And Projects

Backlog is a task inbox and project/folder system.

### Mental Model

- Backlog Items: individual tasks not necessarily assigned to Today.
- Projects: folders/labels containing backlog tasks.
- Unorganized: virtual folder for tasks without project.

### Top Command Bar

- Use the shared six-slot capsule.
- Root Backlog slots are Program/menu, add, view, sort, search, and undo/redo overflow.
- Project detail slots are back, add task, blank, blank, rename, and undo/redo overflow.
- Capsule dropdowns must use the shared capsule anchoring helper, not screen-specific magic numbers.
- Dropdown box left edges align to the separator immediately before the triggering slot. This follows the same mental model as a macOS menu opening from its menu title.
- Dropdowns size to content. Do not pad the right edge just to make menus uniform.
- Dropdown item labels use 20sp semibold text for comfortable tapping.

No giant segmented control inside content.

### Root Backlog Layout

Recommended:

1. Compact Task View or Project View content below the capsule.
2. Search field appears only when activated from the capsule.
3. Task View lists active tasks with separator lines, not white card blocks.
4. Project View lists project rows with separator lines, not white card blocks.
5. Avoid section titles such as `Active Tasks` and `Projects` in this view.

### Project Rows

Rows should look like folders:

```text
[folder icon]   004   Buy a car
```

Tapping opens Project Detail.
Long project names remain one line and end with ellipsis. There is no right chevron in Project View.
In select mode, replace the folder icon with the empty/checked selection circle without shifting the count or title.
The gray count sits to the left of the project title and reserves enough width for three digits.

### Project Detail

Shows:

- project name
- active tasks
- done/completed section collapsed
- add task for this project
- project actions in overflow

Actions:

- rename project
- transfer selected tasks to another project
- delete selected tasks

Project detail displays the project name left-aligned under the capsule. The add action creates tasks only; project creation is not allowed inside project detail.

### Backlog Task Rows

Read mode:

- checkbox or status marker
- title
- project label
- assign-to-date affordance hidden behind row menu or detail
- no text fields

Edit/select:

- selection checkboxes
- move to project
- assign date
- complete
- delete

Task View select mode capsule:

- slot 1: X exits select mode
- slots 2-4: blank
- slot 5: trash
- slot 6: three-dot menu with Assign to project and Assign date

Project View select mode capsule:

- slot 1: X exits select mode
- slots 2-4: blank
- slot 5: transfer/reassign selected projects' tasks
- slot 6: trash

Project detail select mode uses the same project-task selection pattern: X, blank, blank, blank, transfer/reassign tasks, trash. Actions do not automatically leave select mode.

### Add Backlog Item

Use a dedicated page, not a sheet:

- title
- project dropdown with existing projects and No project
- assigned date, defaulting to no date assigned
- note

Do not offer project creation from the task page. The Backlog capsule `+` opens a small anchored popup with Task and Project choices. Task opens this page. Project opens a small `Enter project title` dialog.

The task creation page capsule uses back in slot 1, blank slots 2-5, and a checkmark in slot 6. The checkmark is disabled until Title is filled. Leaving with unsaved input shows only Save and Discard.

Do not show permanent `New backlog item` and `Project` fields on root.

### Backlog View And Sort Menus

View menu options:

- Task View
- Project View

Sort menu options:

- Task View: A-Z, Z-A, Creation Date, Assigned Date
- Project View: A-Z, Z-A

Selected view and sort choices show a right-side checkmark and persist across app restarts.

### Project Reassignment Menus

Project reassignment menus:

- include `Unorganized`
- pin `Unorganized` to the first position
- list real projects alphabetically
- include the current project
- show a right-side checkmark on the current project
- move the checkmark to the newly selected project after selection

## 10.3 Calendar

Calendar is both:

- serious standalone calendar view
- Today input source

### Calendar Root

Top:

- month/date control
- view mode menu: Month, Week, Day, Agenda
- source/filter icon
- add/local override actions if needed

No large segmented control.

### Month View

Must be more than placeholder text:

- month grid
- selected day highlight
- event dots/counts
- today marker
- tapping day updates selected date/details

### Week/Day View

Use:

- time rail
- blocks for schedule/calendar events
- selected calendars only

### Agenda

Use:

- chronological rows
- grouped by date
- concise metadata

### Calendar Sources

Move source selection to a detail sheet/page:

- row per calendar
- switch/checkbox
- color indicator
- clear explanation that selected sources feed Today

### Permission UX

Calendar permission prompt should be contextual:

- show only when needed
- short copy
- button
- no repeated permission card across multiple screens unless state requires it

## 10.4 Routines

Routines are first-class but not the same as Today tasks.

### Root

Show routine templates/workflows:

- title
- cadence/status
- next relevant use
- row menu

Add routine via `+` sheet.

No permanent text field on root.

### Detail

Routine detail can contain:

- steps
- schedule/cadence
- linked reminders
- history/tracking later

## 10.5 Reminders

Move reminders out of giant Settings root.

### Root

Rows:

- title
- time
- recurrence
- enabled switch
- row menu

### Add/Edit Reminder Sheet

Fields:

- title
- time picker, not raw text when practical
- recurrence: once/daily/weekdays/custom weekdays
- attach to task optional later

## 10.6 Stats And Dashboards

Stats is first-class but should not bloat Today.

### Root Stats

Show:

- completion rate
- current/longest streak
- tracked days
- seven-day strip
- charts/detail sections

Design:

- use visual summaries
- avoid raw text stacks
- use actual chart/list components

### Future Custom Dashboards

Custom dashboards may exist later. They should be separate from fixed Today layout.

## 10.7 Import / Export

Import/export is important but too complex for Settings root.

### Root

Show cards/rows:

- `.hprgm` backup
- `.hprgm` restore
- Backlog CSV
- Daily task history CSV
- Encryption/password state

### Export Flow

Use focused flow:

1. Select package type.
2. Choose include game save only when game container exists.
3. Password/encryption.
4. Save.
5. Result message.

### Import Flow

Use staged flow:

1. Choose file.
2. Enter password if encrypted.
3. Preview.
4. Explicit apply.

Never dump CSV text areas on root.

## 10.8 Settings

Settings root is a menu of detail pages.

### Root Rows

Recommended order:

1. Appearance
2. Today Display
3. Backlog
4. Recurring Tasks
5. Schedule
6. Exercise
7. Notifications
8. Calendar
9. Import / Export
10. Security / App Lock
11. Stats
12. Reset
13. About

Each row:

```text
[icon] Appearance
       Match system, Light, Dark      [chevron]
```

Rules:

- no full editor on root
- no raw CSV on root
- no PIN fields on root
- no reset form on root
- no hidden Sudoku inline

### Appearance Detail

Must support:

- Match System
- Light
- Dark

Optional later:

- accent color
- font style
- density

### Today Display Detail

May support:

- show/hide metadata
- time format
- completion message style
- calendar/task metadata display

Does not support:

- rearranging Today sections
- removing fixed core sections

### Security/App Lock Detail

Contains:

- PIN setup/change
- biometric toggle
- timeout controls
- lock now
- recovery phrase generation

Use staged subflows. Do not show raw PIN fields always.

### Reset Detail

Staged reset flow only.

Use:

- backup reminder
- acknowledgement
- typed reset
- destructive action
- cancel

No reset controls on root.

### About Detail

Contains:

- version
- developer label
- privacy/offline summary
- legal/open source later

Hidden game gesture:

- developer label double tap
- if locked, fail quietly or tiny haptic
- if allowed, navigate to full-screen black hidden route
- no visible game-related explanation

## 10.9 Onboarding

Onboarding should be short and polished.

Goals:

- communicate privacy/offline
- optionally set PIN/recovery
- optionally enable calendar
- enter Today

Rules:

- no giant `Human Program` title repeated unnecessarily
- no long feature tour
- use strong visual hierarchy
- use one primary action per screen
- allow skip where appropriate

## 10.10 App Lock Screen

Should feel secure and calm.

Rules:

- no visible planner content
- no app title hero unless needed
- PIN field centered/focused
- biometric as icon/secondary action
- recovery phrase tucked behind "Use recovery phrase"
- errors plain and calm

## 10.11 Hidden Sudoku Gate

Must be separate route.

Rules:

- full-screen black background
- no normal app chrome
- no bottom nav
- no Settings/About wrapper
- no explanatory copy exposing game
- if access locked, route is not entered

## 11. Missing Professional-App Capabilities

The following are not necessarily all v1 feature requirements, but they are common professional-app capabilities that Human Program should account for in architecture:

- Global quick add from Today command bar.
- Global search for tasks/projects/calendar/routines.
- Contextual row menus.
- Dedicated add/edit sheets.
- Date picker.
- Time picker for reminders/schedule.
- Project picker/create inline in add task flow.
- Filter/sort sheet for Backlog.
- Calendar source selection page.
- Appearance mode persistence.
- Polished empty states.
- Polished loading/permission states.
- Undo snackbar.
- Import preview surface.
- Visual QA previews for light/dark.
- Screenshot tests or screenshot checklist.
- Component catalog/previews for design system.

## 11A. Functional Completion Rule

The owner has specifically warned against decorative-only progress. UI polish is valuable only when the underlying function works.

Before calling any route, page, tool, or flow complete, verify:

- The page has a clear daily-use purpose.
- The page has real data behind it, not just placeholder copy.
- The primary action works.
- Add/edit/delete or enable/disable flows persist correctly.
- Read mode and edit mode are correct for the page.
- Undo/redo appears only for real reversible actions.
- Empty, permission-denied, loading, error, and locked states are handled.
- The route survives app restart where persistence is expected.
- The route has tests for core rules when behavior is nontrivial.
- The route has light/dark visual QA screenshots or a documented manual QA pass.

Recommended audit table for future implementation passes:

```text
Route:
Purpose:
Primary data:
Primary action:
Current placeholder/broken behavior:
Persistence path:
Undo/reset safety:
Tests needed:
Visual QA needed:
```

## 12. Minimalism Rules

Minimalism in Human Program does not mean fewer features.

It means:

- fewer visible controls per moment
- clearer information hierarchy
- features available at the right depth
- fewer equal-weight sections
- stronger defaults
- less explanatory copy
- less permanent editing UI
- less raw data exposed

Professional minimalism:

- Today shows what matters now.
- Backlog shows projects/tasks cleanly.
- Settings shows destinations, not editors.
- Forms appear when invoked.
- Advanced options are one clear gesture away.
- Every visible control earns its place.

Unprofessional minimalism:

- hiding features so users cannot find them
- unlabeled mystery icons for rare actions
- low contrast aesthetic text
- removing necessary status information
- making the user remember where things are

## 13. Avoiding Stock Android Look

Use Android/Compose responsibly, but do not look like stock UI.

### Replace Raw Material With Product Components

Screens should not directly call:

- `TopAppBar`
- `NavigationBar`
- `OutlinedTextField`
- `Button`
- `OutlinedButton`
- `Card`
- `SegmentedButton`
- `Checkbox`

unless wrapped in Human Program components or used in a narrow low-level component file.

Preferred wrappers:

- `HpCommandBar`
- `HpIconButton`
- `HpPrimaryButton`
- `HpSecondaryButton`
- `HpTextField`
- `HpListRow`
- `HpTaskRow`
- `HpProjectRow`
- `HpSection`
- `HpSoftPanel`
- `HpMenuSheet`
- `HpFormSheet`
- `HpDateChip`
- `HpProgressRing` or `HpProgressBar`

### Build Custom Chrome

Do:

- custom command bar
- menu sheet
- date chip
- compact icon controls

Do not:

- persistent default top app bar with app title
- bottom nav
- tab bar as primary phone nav

### Use Design Tokens Everywhere

No random `16.dp`/`20.dp` proliferation after the design-system pass. Use tokens except for one-off alignment that has a clear reason.

### Use Icons Carefully

Use Material icons or another consistent icon family, but place them in custom surfaces.

Icon controls need:

- 48 dp touch target
- clear content descriptions
- tooltips where appropriate on larger screens
- consistent container shape

### Use Motion Sparingly

Motion should:

- smooth route/sheet transitions
- indicate state changes
- not be decorative noise

Recommended:

- subtle sheet slide/fade
- row expand/collapse
- progress transition
- checked task transition

Avoid:

- bouncy novelty motion
- delays that slow execution
- decorative particles

## 14. Layout Patterns

### Phone Root

```text
Safe inset
Command bar
Scrollable content
Navigation inset padding
```

### Detail Screen

```text
Back + concise title + actions
Content
Optional sticky primary action if form
```

### Modal Sheet

```text
Grabber optional
Title
Fields/options
Primary action
Secondary/cancel
IME safe padding
```

### Menu Sheet

```text
Header/status
Destination rows
Utility rows
```

### Wide Screen Future

For medium/expanded width:

- optional persistent side rail/menu
- Today remains main panel
- detail pane possible for selected project/task
- do not simply stretch phone cards to huge width

## 15. Content And Microcopy

General rules:

- Use short, plain labels.
- Avoid explaining the app to the owner during normal daily use.
- Avoid "prototype copy" like "optional feature will never block planner" in main views.
- Save explanatory copy for onboarding, empty states, permission sheets, and detail info.
- Use nouns for destinations: Today, Backlog, Calendar, Routines, Stats.
- Use verbs for actions: Add Task, Move to Today, Save Backup.

Bad:

```text
Calendar permission is allowed. Loaded events are shown below and added to Today.
```

Better in UI:

```text
3 events from selected calendars
```

Bad:

```text
Repeatable workflows live here without automatically becoming required tasks.
```

Better:

```text
3 routines
```

## 16. Empty States

Empty states must be compact and actionable.

Examples:

Today tasks empty:

```text
No required tasks for this day.
[Add Task]
```

Backlog empty:

```text
No backlog tasks.
[Add Backlog Item]
```

Calendar permission missing:

```text
Connect device calendars to show events here and include selected events in Today.
[Allow Calendar]
```

Do not use giant explanatory cards in normal daily flow.

## 17. Permission States

Permissions should be contextual and polite.

Notification permission:

- ask from Reminders when enabling reminders
- explain what reminders do
- if denied, show status and allow retry

Calendar permission:

- ask from Calendar or onboarding
- explain selected calendars feed Today
- if denied, Calendar still works as local/empty shell

Do not:

- show repeated permission cards everywhere
- make permissions feel required for offline core app

## 18. Dark Mode

Dark mode is required.

Appearance modes:

```kotlin
enum class AppearanceMode {
    SYSTEM,
    LIGHT,
    DARK
}
```

Rules:

- Store in DataStore.
- Apply at theme root.
- Design dark colors manually.
- Test every screen in dark mode.
- Avoid pure white text on pure black unless intentionally used for hidden gate.
- Hidden Sudoku gate uses true black by design.
- Cards/surfaces need enough contrast from canvas.
- Accent and completion states must pass visual inspection.

## 19. Visual QA Checklist

Every meaningful UI pass must be checked against:

- Does the screen have one clear focal point?
- Does the screen still make sense when squinting/blurred?
- Are primary actions obvious?
- Are secondary actions reachable without clutter?
- Are forms hidden until requested?
- Are controls aligned consistently?
- Are similar rows the same height/shape?
- Does content avoid status bars/cutouts/navigation bars?
- Does keyboard avoid covering fields/actions?
- Does dark mode look intentionally designed?
- Is there any default Material sample-app smell?
- Are text fields absent from read mode?
- Are delete controls absent from read mode?
- Are Settings detail pages focused?
- Is Today still home base?
- Is the hidden game absent from normal UI?

## 20. Implementation Phases

## 20A. Owner Correction Pass From dailyOS Review

The iPhone `dailyOS` project at `/Users/algae/dailyOS` is now a functional reference for Android. Do not copy its visual style directly; copy the page structure, data flow, and interaction logic where it is ahead.

The former standalone dailyOS learning log has been folded into this section and `ADD.md`. Before changing Today, Backlog, Schedule, Settings, About, Calendar, or the hidden gate, use this section and `ADD.md` as the source of truth.

Reference capture facts:

- Source app: `/Users/algae/dailyOS`.
- Important files inspected: `dailyOS/ContentView.swift`, `dailyOS/DailyOSModel.swift`, `APP_BUILD_STATUS.md`, `CODEX_NOTES.md`, and `ADD.md`.
- Reference screenshot: `build/reference-dailyos/01-launch-today.png`.
- The iPhone app showed Today as launch/home, full date with year, previous/next/Today/calendar controls, a calendar-like Daily Schedule, and a task-section plus button.
- Android should not copy the iPhone app visually. Android should copy the useful flow, metadata depth, page relationships, and data behavior.

Data model lessons to preserve or expose:

- Backlog items need title, details/notes, status, assigned date, project bucket, and created date.
- Recurring task templates need weekdays and active state.
- Exercise templates need weekday exercise items.
- Schedule templates need name, enabled state, assigned weekdays, optional custom date range, and ordered blocks.
- Schedule blocks need start time, end time, title, and duration-aware editing.
- Daily pages need date, task snapshots, schedule block snapshots, exercise text/items, and completion state.
- Calendar local state needs completion, visibility/hide, title override, notes override, and ordering.
- Notification reminders need enabled state, recurrence, custom weekdays, and time.
- Generated daily pages are snapshots. Settings/template changes may affect today/future pages but must not rewrite past historical pages.

### Today Acceptance Rules

- Today must not show `Command Center`.
- Today must not show `Execution mode`.
- The selected date must include the year.
- Date controls must be grouped as previous, next, Today, calendar.
- Today's schedule must be a vertical calendar-style grid with a time rail, hour rules, boxes for schedule/calendar events, and a red current-time line only on today.
- The manual-task plus belongs in the required-tasks section header.
- Calendar events must feed both the schedule and required task list when visible.
- Past daily pages remain protected snapshots unless deliberately unlocked.

### Program Acceptance Rules

- The hamburger opens a separate Program page, not a bottom sheet.
- Program destinations should read as folder/app icons, similar to an app-view/watch-view mental model.
- Program currently shows exactly six large destinations: Today, Backlog, Calendar, Routines, Stats, and Settings.
- Reminders and Import/Export live under Settings unless the owner later promotes them back to top-level tiles.
- The Program page uses the Van Gogh background asset full-screen, including behind status and navigation areas.
- Program app tiles use the same opaque glass material as the search bar, direct icons, and serif labels; do not reintroduce nested icon boxes or visible tile borders.
- Search is hidden by default. A downward swipe reveals it, an upward swipe hides it, and tapping the search bar focuses text input, shows the keyboard, and hides the six tiles.
- The search bar must sit below centered camera cutouts/status icons when visible.

### Backlog Acceptance Rules

- Remove the current blue hero/header treatment.
- Add action offers two choices, Task or Project, in a small popup under the capsule `+`; tapping outside dismisses it.
- Search belongs in the top-right page chrome.
- Project View is folder-like, with Unorganized as the virtual default bucket.
- Creating a project should switch/reveal Project View.

### Schedule Acceptance Rules

- Schedule settings root defaults to read mode.
- Existing schedule rows are readable until explicit edit.
- Add Schedule Block must not use broken cramped controls.
- Add Schedule Block must include duration selection.
- Block start/end should be computed from previous block end plus selected duration.
- Audit the entire app for accidental permanent edit mode.

### About And Hidden Gate Acceptance Rules

- Remove the small extra `Human Program` label under About.
- Developer label hidden gesture must not leave an obvious highlighted/selected effect.
- Undo/redo must not appear on About.
- Hidden Sudoku gate must be full-screen black, repair black-on-black/missing cells, remove Enter, auto-detect completion, and fade toward game entry after completion.

### Material Direction

The owner now wants the app to feel glassy/glossy with opaque translucent surfaces. Use this as a controlled material system:

- preserve text contrast
- preserve touch target clarity
- avoid overusing blur as decoration
- avoid one-color palettes
- keep the app feeling like an execution tool, not a visual demo
- verify light and dark mode

Renumbering note: this section was inserted after dailyOS inspection on 2026-05-15; future agents may renumber sections later if they do a documentation cleanup.

### Phase 1: Design System Foundation

- Create `ui/design` tokens.
- Add custom theme wrapper.
- Add appearance mode enum and DataStore bridge if not already complete.
- Create component wrappers.
- Do not redesign all screens at once before component language exists.

### Phase 2: App Shell And Navigation

- Remove bottom navigation from phone UI.
- Create Today-first shell.
- Add Program menu sheet/drawer.
- Add route model.
- Remove persistent app title.
- Make command bar inset-safe.

### Phase 3: Today Rebuild

- Build status panel.
- Replace date row with date chip/picker.
- Integrate calendar into schedule/tasks.
- Replace task fields with read rows.
- Add add-task sheet.
- Move undo/redo into overflow/snackbar.

### Phase 4: Backlog/Projects Rebuild

- Replace segmented control.
- Add filter/sort/view sheet.
- Build project folder rows.
- Build project detail route.
- Add backlog item sheet.
- Hide edit controls in read mode.

### Phase 5: Calendar Rebuild

- Build real Month/Week/Day/Agenda surfaces.
- Move source selection to sheet/detail.
- Integrate local event rename/hide detail.
- Replace permission copy with contextual state.

### Phase 6: Settings Rebuild

- Create Settings root menu.
- Move sections to detail screens.
- Move Import/Export, Reminders, App Lock, Reset into focused flows.
- Move hidden gate route out of About.

### Phase 7: Routines/Reminders/Stats Polish

- Build dedicated roots/details.
- Add form sheets.
- Replace raw fields/segmented controls.

### Phase 8: Visual QA And Splitting

- Split `HumanProgramApp.kt`.
- Run tests/build.
- Run manual emulator UI testing.
- Capture screenshots light/dark.
- Fix layout issues from real device sizes.

### Phase 9: Functional Ship Audit

- Audit every route with the Functional Completion Rule.
- Remove or replace placeholder-only tools.
- Verify every Program tile reaches a useful page.
- Verify Today, Backlog, Calendar, Schedule, Settings, Routines, Reminders, Stats, Import/Export, App Lock, Reset, About, and hidden gate flows with real interactions.
- Verify screenshots beyond Today and Program; do not assume unvisited screens are visually correct.
- Run `lintDebug test assembleDebug` after meaningful chunks.

## 20B. Complete UI Function Sweep

Future UI work must include a complete sweep of everything that can be opened or touched. No button, dropdown, setting, menu item, sheet, or visual control should exist as decoration or dead UI.

### Sweep Rule

- Open every screen.
- Open every Program tile.
- Open every Settings detail.
- Open every dropdown.
- Open every overflow menu.
- Open every modal sheet.
- Open every date picker.
- Open every form.
- Open every permission state.
- Open every empty state.
- Open every locked/unlocked state.
- Tap every button and icon button.
- Toggle every switch.
- Select every chip/dropdown option.
- Try every destructive action.
- Try every undo/redo action.
- Try every import/export action.
- Try the hidden/easter-egg path.
- If a UI element has a purpose in `ADD.md` or this guide, make the function real.
- If a UI element has no purpose in `ADD.md` or this guide, remove it.
- If the UI suggests a feature that does not work yet, either implement it or hide it until it has a real function.

### Screenshot QA Requirement

Capture or manually inspect screenshots for:

- every route
- every Settings detail
- every modal sheet
- every dropdown/overflow menu
- every permission state
- every empty state
- every locked/unlocked state
- every keyboard-open form
- light mode
- dark mode
- match-system mode
- Pixel Pro-style centered camera cutout
- compact/small phone size

Do not assume a screen is visually correct because a nearby screen was checked.

### Opaque Glass Direction

The desired Human Program surface style is opaquely glassy, like frosted glass over paper: the layer is translucent and softened, but the content underneath is only vaguely visible. It should feel calm and tactile, closer to early iOS frosted surfaces than flashy liquid glass.

Apply this direction to:

- command surfaces
- shared six-slot top capsule
- Today status and schedule panels
- Program folder/app tiles
- focused forms
- modal sheets
- Settings rows and detail panels
- permission and empty states
- dark-mode surfaces

Rules:

- Text must remain readable.
- Touch targets must remain obvious.
- The effect must not reduce scan speed.
- Avoid random opacity on isolated cards.
- Avoid large decorative blur blobs.
- Avoid over-bright highlights.
- Avoid one-color palettes.
- Test in light and dark mode.

### Route And Control Audit Targets

Audit all of these current UI elements and either complete their function or remove/rework them:

- Program: Today tile, Backlog tile, Calendar tile, Routines tile, Stats tile, Settings tile, hidden-by-default search bar, active search state, keyboard-open search state, downward-swipe reveal, upward-swipe hide.
- Shell: shared six-slot capsule, menu, back, blank slots, page-specific slots, overflow/close toggle, disabled undo, disabled redo, enabled undo, enabled redo, undo/redo feedback popup.
- Today: previous day, next day, Today, calendar picker, past unlock, manual task add, task create sheet, task checkbox, task title edit, task delete, schedule boxes, calendar boxes, exercise section.
- Backlog: view dropdown, Project View option, Task View option, sort dropdown, A-Z, Z-A, Creation Date, Assigned Date, search toggle, search field, anchored Task/Project add popup, full-page task creation, project title dialog, task tap-to-open, task select mode, project select mode, transfer/reassign popup, assigned-date popup, delete, project detail, project-detail select mode, project rename, Delete items, Move items.
- Calendar: date picker, mode dropdown, Month, Week, Day, Agenda, Refresh, Sources, source toggles, event rows, event detail/local override behavior.
- Routines: root, add sheet, routine rows, empty state.
- Reminders: root, add sheet, enable switch, delete, permission button, recurrence/time controls.
- Stats: all summary panels, streak views, completion views, chart/strip areas.
- Import/Export: Save Backup, Choose Backup, Apply Import, Advanced Data, Import Rows, Preview Tasks, Preview Daily History.
- Settings root: Appearance, Today Display, Backlog, Recurring Tasks, Schedule, Exercise, Notifications, Calendar, Import/Export, Security/App Lock, Stats, Reset, About.
- Appearance: Match System, Light, Dark.
- Recurring Tasks: add, active toggles, edit/delete behavior.
- Schedule: read/edit toggle, add block, duration shortcuts, time range shortcuts, block delete.
- Exercise: item add/edit, move up, move down, delete.
- Notifications: permission, reminder settings links/status.
- Calendar Settings: permission, source toggles, source status.
- Security/App Lock: Set PIN, Change PIN, Lock Now, biometric toggle, timeout choices, Generate Recovery Phrase, Recovery Phrase.
- Reset: Prepare Reset, Save Backup, Continue, typed confirmation, Reset Local Data, Cancel Reset.
- About: developer hidden gesture, version/build info.
- App Lock: Unlock, Use Biometric Unlock, Use Recovery Phrase, Unlock With Recovery Phrase.
- Welcome: Set PIN, Recovery Phrase, Allow Calendar, Enter Today.
- Hidden gate: back button, fixed clue, editable cells, completion auto-detect, fade/game entry.

### Screen-Specific Completion Notes

- Today needs real task detail/edit behavior, true schedule/event positioning, overlap handling, local calendar controls, and visual QA in dense states.
- Backlog needs continued undo/redo hardening across project/task lifecycle operations, plus full visual QA of capsule dropdown anchoring, select-mode behavior, project delete choices, notes/details, assigned date editing, and project picker behavior.
- Calendar needs real Month/Week/Day/Agenda depth, event detail/local override UI, and calendar sync/status tools.
- Schedule needs real schedule templates, enabled state, weekday/custom-date assignment, conflict handling, Sleep locking, duration picking, and unsaved-change protection.
- Exercise needs seven weekday templates, title editing, item editing, deletion, reordering, blank-line handling, and today/future propagation.
- Routines needs a real v1 purpose or should be hidden until it does.
- Reminders need time picker, recurrence editing, permission-aware scheduling states, alarm cancellation coverage, and sensitive notification handling when locked.
- Stats needs visual summaries, weekly/monthly views, tracked days, saved pages, active/completed task counts, and no hidden game state.
- Import/Export needs clear staged flows, real file results, encrypted backup support, CSV range selection, and malformed-file states.
- Settings must stay a focused menu of detail pages, never a root editor dump.
- App Lock needs calm staged flows, timeout QA, recovery QA, biometric fallback QA, and sensitive-content hiding.
- Hidden gate needs full-screen black mystery, quiet locked failure, no explicit Enter, auto-detect completion, and fade toward game entry.

## 21. Code Review Checklist For Future UI Changes

Reject UI changes that:

- add bottom tabs back on phone
- add persistent app title chrome
- add inline permanent forms to primary screens
- add raw Settings editors to Settings root
- add hidden game UI to normal screens
- use raw Material components in screen files instead of wrappers
- ignore insets/cutouts
- introduce one-off colors or spacing
- add large segmented controls inside content for primary navigation
- use cards for every row
- create visible delete buttons in read mode
- introduce low-contrast text
- make dark mode an afterthought

Accept UI changes that:

- improve Today-first execution
- reduce clutter without hiding features beyond reach
- move editing into sheets/detail pages
- improve hierarchy
- preserve offline/privacy rules
- use design tokens/components
- add focused detail pages
- improve manual QA outcomes

## 22. Source Links

- Android app quality: https://developer.android.com/quality/user-experience
- Android layout basics: https://developer.android.com/design/ui/mobile/guides/layout-and-content/layout-basics
- Android settings: https://developer.android.com/design/ui/mobile/guides/patterns/settings
- Android Compose custom design systems: https://developer.android.com/develop/ui/compose/designsystems/custom
- Android Compose theme anatomy: https://developer.android.com/develop/ui/compose/designsystems/anatomy
- Android Compose Material 3: https://developer.android.com/develop/ui/compose/designsystems/material3
- Android Compose insets: https://developer.android.com/develop/ui/compose/system/insets
- Android adaptive display sizes: https://developer.android.com/develop/ui/compose/layouts/adaptive/support-different-display-sizes
- Apple Human Interface Guidelines: https://developer.apple.com/design/human-interface-guidelines
- NN/g aesthetic-usability effect: https://www.nngroup.com/articles/aesthetic-usability-effect/
- NN/g progressive disclosure: https://www.nngroup.com/articles/progressive-disclosure/
- NN/g usability heuristics: https://www.nngroup.com/articles/ten-usability-heuristics/
- NN/g visual hierarchy: https://www.nngroup.com/articles/visual-hierarchy-ux-definition/
