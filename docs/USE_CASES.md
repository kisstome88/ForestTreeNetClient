# Use Cases / 应用场景

The Forest Tree Net platform — and the sensor that feeds it — is used in six broad scenarios. Each one benefits from **continuous, long-term, automated DBH data** rather than the discrete, manual measurements that tape-and-notebook surveys produce.

---

## 1. 国有林场资源动态监测 / State forest resource monitoring

**问题 / Problem.** State forest farms need to track DBH growth across thousands of trees on a continuous basis. Manual re-measurement is expensive and operationally hard to sustain.

**解法 / Solution.** Install sensors on representative trees in each compartment; pull the data via [`queryTreeLandRanges`](./API.md#2-querytreelandranges--list-sample-plots) → [`queryTreeInfos`](./API.md#3-querytreeinfos--list-sample-trees) → [`queryTreeMeasureData`](./API.md#5-querymeasuredata--query-historical-sensor-data) on a daily cron.

**关键指标 / Key metrics.** Volume increment, mean annual increment, compartment-level growth rates.

```java
// Pseudocode: collect yesterday's measurements for every plot
List<JSONObject> plots = listAllPlots(token);
for (JSONObject plot : plots) {
    String landNumber = plot.getString("landNumber");
    JSONObject trees = ForestTreeNetClient.queryTreeInfos(token, body("landNumber", landNumber));
    for (JSONObject tree : trees.getJSONArray("content")) {
        JSONObject data = ForestTreeNetClient.queryTreeMeasureData(token, body(
            "sn", tree.getString("measureStationSn"),
            "startTime", yesterday(),
            "endTime", today()
        ));
        store(tree, data);
    }
}
```

---

## 2. 国家储备林建设 / National reserve forest management

**问题 / Problem.** 国家储备林 (national reserve forest) projects require multi-year DBH increment records to validate growth models and report to the central forestry authority.

**解法 / Solution.** Strap-mounted sensors provide uninterrupted time-series data — exactly what inventory models and compliance reports need.

**关键指标 / Key metrics.** Stand-level growth curves, species-specific increment tables, periodic compliance exports.

See [PRODUCT.md → 智能数据管理 / Smart data management](./PRODUCT.md#核心优势--key-advantages) for the export format.

---

## 3. 森林碳汇监测 / Forest carbon sink monitoring

**问题 / Problem.** Carbon accounting requires **continuous** DBH increment data to estimate above-ground biomass and carbon stock. Manual quarterly measurements introduce interpolation error and miss seasonal dynamics.

**解法 / Solution.** With sensors reporting DBH every few hours, biomass (`swl`) and carbon (`tcl`) fields can be updated continuously, supporting high-frequency carbon-flux analysis.

**关键指标 / Key metrics.** Above-ground biomass (kg), carbon stock (kg), carbon flux over time.

```java
JSONObject data = ForestTreeNetClient.queryTreeMeasureData(token, body(
    "sn", deviceSn,
    "startTime", "2026-01-01T00:00:00",
    "endTime",   "2026-06-30T23:59:59"
));

double initialDm = firstReading(data).getDoubleValue("dm");    // mm
double finalDm   = lastReading(data).getDoubleValue("dm");     // mm
double growth    = finalDm - initialDm;                        // mm over 6 months

// feed growth into your allometric biomass model
double biomassKg = allometricBiomass(initialDm, species);
double carbonKg  = biomassKg * 0.5;                            // ≈ 50% of biomass
```

---

## 4. 古树名木保护 / Ancient & heritage tree protection

**问题 / Problem.** Ancient trees (often >500 years old) are protected as cultural relics. Each tree's health trend matters — even a small growth-rate change can signal root disease, drought stress, or risk of collapse.

**解法 / Solution.** One sensor per protected tree, with [`queryTreeMeasureStations`](./API.md#4-querymeasurestations--list-tree-diameter-devices) returning both the bound tree (`treeInfo`) and the latest reading (`latestData`) in a single call — useful for daily dashboards.

**关键指标 / Key metrics.** Year-over-year growth rate, sudden growth deceleration (early warning), battery / angle drift.

```java
JSONObject body = new JSONObject();
body.put("landNumber", "ANCIENT-TREE-PLOT");
body.put("returnTree", true);
body.put("returnData", true);
JSONObject resp = ForestTreeNetClient.queryTreeMeasureStations(token, body);

for (JSONObject device : resp.getJSONArray("data")) {
    JSONObject tree   = device.getJSONObject("treeInfo");
    JSONObject latest = device.getJSONObject("latestData");
    log.info("{}: dbh={}mm, last seen {}",
        tree.getString("treeNumber"),
        latest.getDoubleValue("dm"),
        latest.getString("reportDateTime"));
}
```

---

## 5. 林业样地长期监测 / Long-term sample-plot monitoring

**问题 / Problem.** Universities and research institutes run permanent sample plots that are re-measured on a 3–5 year cycle. Discontinuous data makes it hard to study climate-growth relationships, inter-annual variability, or disturbance recovery.

**解法 / Solution.** A sensor per measured tree turns each plot into a continuous observatory. Researchers can query the same plot year after year without re-entering the field.

**关键指标 / Key metrics.** Daily / weekly / monthly DBH series, growth-response curves to climate variables, treatment-vs-control comparisons in fertilization or thinning experiments.

See [examples/BasicUsage.java](../examples/BasicUsage.java) for the canonical end-to-end workflow.

---

## 6. 林木倾倒风险预警 / Tree-fall risk early warning *(sensor with IMU)*

**问题 / Problem.** Trees in urban parks, along roads, and on steep slopes pose a falling risk after storms, root rot, or soil saturation. Routine visual inspection is slow and subjective.

**解法 / Solution.** Sensors with integrated IMU stream inclination (`angle`) at high frequency. A sudden tilt change, or a sustained tilt above threshold, can be detected from the `angle` field returned by [`queryTreeMeasureData`](./API.md#5-querymeasuredata--query-historical-sensor-data).

**关键指标 / Key metrics.** `angle` (degrees) drift rate, sustained tilt alerts, post-storm step-change detection.

```java
JSONObject data = ForestTreeNetClient.queryTreeMeasureData(token, body(
    "sn", deviceSn,
    "startTime", lastStormStart(),
    "endTime",   lastStormEnd(),
    "size", 1000
));

double maxAngle = data.getJSONArray("data").stream()
    .map(o -> ((JSONObject) o).getDoubleValue("angle"))
    .max(Double::compare)
    .orElse(0.0);

if (maxAngle > 5.0) {                   // configurable threshold
    alertOps("tree {} tilted {}° during last storm", deviceSn, maxAngle);
}
```

---

## 其他场景 / Other scenarios

- **城市绿化与生态工程 / Urban greening** — same SDK, different dashboards.
- **校园 / 公园 / 名胜古树 / Heritage trees in parks and campuses** — see scenario 4.
- **林业碳汇 CCER / VCS 项目 / Carbon-credit projects** — see scenario 3.

---

## 选型与采购 / Selection & procurement

For detailed specifications, custom ranges, OEM / ODM options, and procurement, see [PRODUCT.md → 采购与合作](./PRODUCT.md#采购与合作--get-in-touch).