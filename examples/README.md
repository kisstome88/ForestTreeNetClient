# Examples

Runnable Java samples that show how to use `forest-tree-net-client`.

## Prerequisites

- JDK 8 or newer (`java -version`).
- The fat-JAR at [`../lib/forest-tree-net-client-1.0.0.jar`](../lib/).
- A Forest Tree Net account and a reachable server.

## `BasicUsage.java`

End-to-end walk through all five public methods on `ForestTreeNetClient`:

1. `login()` — get a Bearer token.
2. `queryTreeLandRanges()` — list sample plots.
3. `queryTreeInfos()` — list sample trees.
4. `queryTreeMeasureStations()` — list tree-diameter devices.
5. `queryTreeMeasureData()` — pull historical readings.

### Compile and run

```bash
# from the repo root
javac -cp "lib/forest-tree-net-client-1.0.0.jar" -d examples/out examples/BasicUsage.java

java -cp "lib/forest-tree-net-client-1.0.0.jar:examples/out" \
     -DforestTreeNet.serverUrl=https://your-host/forest-tree-net/ \
     -Dftn.username=alice \
     -Dftn.password=s3cret \
     BasicUsage
```

You can also hard-code `USERNAME` / `PASSWORD` in `BasicUsage.java` for quick local testing, but the `-D` route is recommended for anything that touches a real server.

### Expected output

```
[1] logged in, token = eyJhbGci…bXl0
[2] 5 / 137 plots
    → first plot: PLOT-2025-001 (西岭样地)
[3] 12 / 12 trees in plot PLOT-2025-001
[4] 12 / 12 devices in plot PLOT-2025-001
    → first device: TDS-2025-0001 (DBH=148.32 mm)
[5] 5 / 1024 readings for device TDS-2025-0001
    - 2026-06-30T08:00:00 | dm=148.32mm | angle=12.45° | bat=3450mV
    - 2026-06-29T08:00:00 | dm=148.21mm | angle=12.43° | bat=3460mV
    ...
```

## Adding your own example

1. Drop a `<Name>.java` file in this directory.
2. Update this README with a short description.
3. Open a PR — see [../CONTRIBUTING.md](../CONTRIBUTING.md).