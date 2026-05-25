# Human Program UI Development Guide

## Radio Choice List

Use a Radio Choice List for screens where the user chooses exactly one option from a short list.

Examples:

- Lock Settings
- Appearance
- Date Format
- Default calendar view
- Any simple one-choice preference screen

Required layout:

```text
[top capsule / normal app chrome]

    Screen Title

    ○  Option One
    ○  Option Two
    ●  Selected Option
    ○  Option Four
```

Rules:

- Use the plain page background. Do not place the list inside a card, rounded rectangle, gray panel, or frosted container.
- Show a visible radio button for every option, including unselected options.
- Use one selected radio button only.
- Align the screen title with the radio-button column, not with the far left page edge.
- Keep the gap above the title and below the title visually balanced.
- Use medium-density mobile settings rows: target about `48dp` to `56dp` row height.
- Keep vertical row spacing cohesive, not loose. The list should read as one setting group.
- Do not use divider lines between radio choices.
- Do not use decorative icons on radio choice rows.
- Do not add status text that repeats the selected value, such as `Locked` when the current selection already communicates state.
- Do not add extra action buttons unless the action is truly separate from choosing the option.

Current spacing reference from Lock Settings:

```kotlin
Column(modifier = Modifier.padding(top = 11.dp)) {
    Text(
        text = "Lock Settings",
        modifier = Modifier.padding(start = 20.dp)
    )
    Spacer(Modifier.height(11.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Radio rows
    }
}
```

Radio row reference:

```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick)
        .padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    RadioButton(selected = selected, onClick = onClick)
    Text(label, fontWeight = FontWeight.SemiBold)
}
```

Appearance should follow this same pattern:

```text
    Appearance

    ○  Match System
    ○  Light
    ●  Dark
```
