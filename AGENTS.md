# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/xyz/ludwicz/ludwigmod/` contains the client mod source.
- Feature code is grouped by package (`zoom/`, `freelook/`, `hud/`, `saturation/`, etc.).
- Mixins live in `src/main/java/xyz/ludwicz/ludwigmod/mixin/`; keep class names in the `Mixin*` pattern matching the target class.
- Assets and mod metadata are in `src/main/resources/` (`fabric.mod.json`, `ludwigmod.mixins.json`, versioned mixin/access widener files, language files, textures, shaders).
- Version-specific build settings live under `versions/<mc-version>/gradle.properties` (for example, `versions/1.20.1/` and `versions/1.21.11/`).
- `build/` is generated output; do not commit manual edits there.

## Build, Test, and Development Commands
- `./gradlew build`: main CI command; builds the currently active Stonecutter version and packages the mod jar.
- `./gradlew clean build`: full rebuild when mappings/dependencies or generated output look stale.
- `./gradlew runClient`: launches a local Fabric client for gameplay/manual validation.
- `./gradlew --refresh-dependencies build`: useful when dependency resolution is out of date.
- Switch target MC version by updating `stonecutter.active` in `stonecutter.gradle` before running build/client tasks.
- If Unix shells report `^M` in `gradlew`, normalize line endings first (`dos2unix gradlew`).

## Coding Style & Naming Conventions
- Keep code compatible with target Java levels from version properties (`1.20.1 -> Java 17`, `1.21.11 -> Java 21`).
- Use 4-space indentation and existing brace style (same-line opening braces).
- Naming: packages `lowercase`, classes `PascalCase`, methods/fields `camelCase`, constants `UPPER_SNAKE_CASE`.
- Keep feature-specific logic inside its package; move reusable helpers to `util/` or `gui/`.

## Testing Guidelines
- There is currently no dedicated `src/test` suite.
- Minimum verification for each change: run `./gradlew build`, run `./gradlew runClient`, then manually test affected UI/gameplay behavior in-game.
- If a change affects shared code paths, repeat verification after switching `stonecutter.active` for each supported MC version.
- If you add automated tests later, place them in `src/test/java` and use `*Test` class naming.

## Commit & Pull Request Guidelines
- Existing history is short and direct (for example: `Initial commit`, release/date bumps). Keep commits concise and specific, e.g. `hud: fix anchor snapping`.
- PRs should include a brief change summary, manual validation steps/results, a linked issue when applicable, and screenshots or short clips for HUD/visual changes.
- Ensure GitHub Actions `build` passes (currently Java 21) before requesting review.
