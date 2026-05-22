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
- The owner is frustrated by overpromising and partial completion claims. Do not say the app is done, complete, production-ready, or "100%" unless every core feature, flow, persistence path, and QA item has actually been verified.
- The owner does not want decorative-only work. Every page, function, and tool must have a clear working purpose.

## Source Of Truth

- `ADD.md` is the product/build source of truth.
- `UI_DEVELOPMENT_GUIDE.md` is the UI/UX source of truth.
- `BUILD_STATUS.md` is the progress tracker and next-step source.
- Read the relevant docs before changing related code.
- Do not duplicate detailed product requirements in this file.

## Coding Architecture

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

## Work Habits

- Before coding, check `git status --short` when practical.
- Search with `rg` or `rg --files`.
- For UI work, read `UI_DEVELOPMENT_GUIDE.md` first.
- For dailyOS parity work, use the folded lessons in `ADD.md` and `UI_DEVELOPMENT_GUIDE.md`.
- For feature scope and product rules, read `ADD.md` first.
- For current next steps, read `BUILD_STATUS.md` first.
- Treat remaining work as a functional product audit, not a surface redesign.
- Remove or replace placeholder-only tools. A route is not production-ready just because it opens.
- Keep implementation chunks small enough to test.

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
- `UI_DEVELOPMENT_GUIDE.md`

Do not keep separate handoff documents as long-term source-of-truth files. Fold durable lessons into the maintained docs above.
