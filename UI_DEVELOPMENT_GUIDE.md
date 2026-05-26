# Human Program UI Development Guide

This guide defines how Human Program screens should be built.

## Core Rule

Build UI from shared controls and shared layouts.

Do not copy the same row, button, sheet, editor, or section code into each page by hand. If multiple screens need the same behavior or shape, make one reusable control and use it everywhere.

## Clean UI Code

- Remove sloppy one-off code when a shared control can do the job.
- Keep screen files focused on screen structure, not repeated low-level UI details.
- Prefer small reusable components with clear names.
- Do not leave placeholder-only controls, fake routes, or decorative-only UI.
- Do not create separate versions of the same UI unless the behavior is truly different.

## Screen Structure

Each important screen should have defined positions for its main controls.

Use the same spacing, alignment, row heights, padding, and section structure across related screens. A user should not feel like each page was built by a different system.

When a layout pattern is settled, reuse it instead of re-deciding positions on each screen.

## Read Mode And Edit Mode

Read mode and edit mode must use identical UI positioning.

This is non-negotiable.

- The same content must stay in the same place.
- Switching modes must not move rows, labels, checkboxes, fields, buttons, or section spacing.
- Hiding edit-only controls must not cause reflow.
- Reserve the same space in both modes.
- In read mode, edit-only controls may be invisible, disabled, or visually plain, but their layout space must remain.

Do not build one layout for read mode and a different layout for edit mode unless the owner explicitly asks for that.

## Reuse Standard Controls

Use one standard control for each repeated UI job:

- task rows
- section headers
- add/edit sheets
- read/edit fields
- date and time controls
- checkboxes and completion controls
- action rows
- settings rows

If a page needs a special case, add a clear option to the shared control when practical. Do not fork the whole UI for a tiny difference.

## Settings Screens

Settings should be clean, efficient, and consistent.

- Use the shared settings row/control pattern.
- Keep labels, values, toggles, and navigation actions aligned.
- Avoid scattered custom rows.
- Avoid repeated manual code for each setting.
- Group settings by purpose, not by implementation detail.

## Compose Implementation Rules

Screens should describe page structure. Shared components should handle repeated UI details.

On screens with text fields, tapping blank space outside the active field should clear focus and close the keyboard or numpad. Put this behavior in the shared screen wrapper when related screens use the same layout.

Good pattern:

```kotlin
SettingsRow(
    title = "Notifications",
    value = "Enabled",
    onClick = onNotificationsClick
)
```

Avoid this pattern:

```kotlin
Row {
    Text("Notifications")
    Spacer(...)
    Text("Enabled")
}
```

Do not rebuild the same row manually on every screen.

For read/edit mode, prefer one shared layout with different state:

```kotlin
TaskRow(
    task = task,
    mode = screenMode,
    onTitleChange = onTitleChange
)
```

Avoid separate read and edit layouts that place controls differently:

```kotlin
if (isEditing) {
    EditableTaskRow(...)
} else {
    ReadOnlyTaskRow(...)
}
```

That pattern is only acceptable if both branches use the same underlying layout and reserve the same space.

Shared controls may take simple options when screens need small differences. Do not copy the whole component for one label, icon, color, or action change.

## Before Finishing UI Work

Check that:

- read mode and edit mode have identical positioning
- repeated UI is handled by shared controls
- no placeholder-only UI remains
- the screen still has a clear working purpose
- spacing and alignment match related screens
- the implementation is easier to maintain than before
