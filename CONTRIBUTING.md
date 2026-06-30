# Contributing to BtTreeGauge

Thanks for your interest in improving `forest-tree-net-client`! 🎉

This document covers everything you need to send a useful pull request or file a high-quality issue.

## Code of Conduct

By participating, you agree to abide by the [Code of Conduct](./CODE_OF_CONDUCT.md). Please read it before you start.

## Reporting issues

Before opening an issue:

1. **Search existing issues** — your bug or feature may already be tracked.
2. **Use a clear, descriptive title** — e.g. *"`queryTreeMeasureData` returns `totalElements=0` when sorting by `create_date_time desc"`* rather than *"doesn't work"*.
3. **Include a minimal reproduction**:
   - SDK version (`mvn dependency:tree | grep forest-tree-net-client`, or the JAR's filename).
   - JDK version (`java -version`).
   - Server URL / environment (you may mask credentials).
   - The exact request body and the response you got vs. the response you expected.
4. **Attach logs** — set the SLF4J logger for `com.landinfo.client` to `DEBUG` and paste the relevant excerpt.

## Suggesting features

Open an issue with the label **enhancement** and describe:

- The problem you're trying to solve.
- The proposed API or behavior change.
- Any backwards-compatibility implications.

For **breaking changes** to the public surface (`com.landinfo.client.ForestTreeNetClient`), please open a discussion issue first so we can agree on the shape before any code is written.

## Pull requests

### Workflow

1. Fork the repo and create a feature branch: `git checkout -b feat/my-change`
2. Make your changes — keep commits small and focused; write present-tense messages (`Add ...`, `Fix ...`).
3. Run the build and tests locally (see below).
4. Push your branch and open a PR against `main`.
5. Address review feedback by pushing new commits (do **not** force-push during review).

### Coding style

- **Language**: Java 1.8 source / target. No Java 9+ APIs (no `var`, no `List.of`, etc.) unless absolutely necessary.
- **Indentation**: 4 spaces, no tabs.
- **Naming**: standard Java conventions — `lowerCamelCase` for methods/fields, `UpperCamelCase` for types, `UPPER_SNAKE_CASE` for constants.
- **Logging**: use SLF4J (`Logger log = LoggerFactory.getLogger(getClass())`).
- **JSON**: use `com.alibaba.fastjson.JSONObject` to stay consistent with the existing surface.
- **Public API stability**: changes to `ForestTreeNetClient`'s public static methods require a `MAJOR` version bump per SemVer.

### Tests

If you change behavior, add a test that would have caught the bug. Place tests under `src/test/java/` mirroring the package layout.

### Commit messages

We loosely follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add overload for queryTreeMeasureData with date-range shortcut
fix: throw LoginException when server returns 401 instead of returning the body
docs: clarify token expiry in API.md
refactor: extract ServerUrlResolver logic from ForestTreeNetClient
```

### Sign your work

Add a `Signed-off-by:` line to your commit message (use `git commit -s`) to certify the [Developer Certificate of Origin](https://developercertificate.org/):

```
Signed-off-by: Your Name <you@example.com>
```

## Release process

Maintainers cut releases from `main`:

1. Update `CHANGELOG.md` (move *Unreleased* items under a new versioned section).
2. Bump the version in `lib/` and any build files.
3. Tag the release: `git tag -s vX.Y.Z -m "Release vX.Y.Z"`.
4. Push the tag and publish the GitHub release with the fat-JAR attached.

## Questions?

Open a discussion or drop a note in an issue. There are no stupid questions.

Thanks again for contributing! 🌲