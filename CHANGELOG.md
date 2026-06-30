# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-06-30

### Added
- Initial public release of `forest-tree-net-client` (artifact: `BtTreeGauge`).
- `ForestTreeNetClient.login(username, password)` — username/password authentication; returns a Bearer token with 24h validity.
- `ForestTreeNetClient.login(serverUrl, username, password)` — overload that pins a per-call server URL.
- `ForestTreeNetClient.queryTreeLandRanges(token, body)` — paginated query of sample plots (`样地`).
- `ForestTreeNetClient.queryTreeInfos(token, body)` — paginated query of sample trees (`样木`) under a sample plot.
- `ForestTreeNetClient.queryTreeMeasureStations(token, body)` — paginated query of tree-diameter devices (`树径设备`).
- `ForestTreeNetClient.queryTreeMeasureData(token, body)` — paginated query of historical sensor readings.
- `ForestTreeNetClient.post(...)` — generic `POST` escape hatch for endpoints not covered by the typed helpers.
- `LoginException` for credential / token / network failures.
- Pluggable `HttpClient` via `ForestTreeNetClient.setHttpClient(...)` for custom TLS, proxy, or pooling.
- Server URL resolution priority: `-DforestTreeNet.serverUrl` JVM property → `FOREST_TREE_NET_SERVER_URL` env var → bundled `forest-tree-net-client.properties`.

### Notes
- The prebuilt fat-JAR ships shaded Apache HttpComponents Client, FastJSON, and SLF4J.
- Minimum runtime: **Java 1.8**.

[1.0.0]: https://github.com/<your-org>/BtTreeGauge/releases/tag/v1.0.0