# Human Program Development Progress

Use this file to track what has been built, what is next, and what decisions have been made.

## Current Status

- Repository exists at `https://github.com/davidjurek/humanprogram.git`.
- Full app blueprint exists in `HUMAN_PROGRAM_ANDROID_AGENT_SPEC.md`.
- Persistent agent instructions exist in `AGENTS.md`.
- Android app code has not been created yet.

## Completed

- Created project repository.
- Added Human Program Android master spec.
- Added persistent agent instruction file.
- Added this development progress tracker.

## In Progress

- Project planning and setup.

## Next Steps

1. Create the native Android project foundation.
2. Use package name `app.humanprogram.android`.
3. Use Kotlin, Jetpack Compose, and Material 3.
4. Add the five main tabs: Today, Backlog, Calendar, Routines, Settings.
5. Make a simple first version that opens and runs before adding deeper features.

## Decisions

- Build as an offline-first Android app.
- Do not require Google login, Firebase, analytics, ads, or cloud services.
- Keep the future game separate from planner logic.
- Treat `HUMAN_PROGRAM_ANDROID_AGENT_SPEC.md` as the source of truth.
- Keep beginner-friendly communication as a standing rule.

## Notes For Future Agents

- Update this file after each meaningful development chunk.
- Keep entries short and plain.
- Do not use this file for detailed product requirements; use the master spec for that.
- Do not mark work completed until it has actually been implemented.
