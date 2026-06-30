# API Reference

> Tree-diameter (树径) monitoring client SDK — interface documentation.
>
> **Technical support**: 四川恩特网联科技有限公司 (Sichuan Enternet Technology Co., Ltd.)
>
> **Import**: `import com.landinfo.client.ForestTreeNetClient;`
>
> **Original Word doc**: [树径监测jar包接口文档.docx](./树径监测jar包接口文档.docx)

---

## Table of contents

1. [`login` — Obtain a token](#1-login--obtain-a-token)
2. [`queryTreeLandRanges` — List sample plots](#2-querytreelandranges--list-sample-plots)
3. [`queryTreeInfos` — List sample trees](#3-querytreeinfos--list-sample-trees)
4. [`queryTreeMeasureStations` — List tree-diameter devices](#4-querymeasurestations--list-tree-diameter-devices)
5. [`queryTreeMeasureData` — Query historical sensor data](#5-querymeasuredata--query-historical-sensor-data)

---

## Conventions used in this document

- All methods are `public static` on `com.landinfo.client.ForestTreeNetClient`.
- `JSONObject` refers to `com.alibaba.fastjson.JSONObject`.
- Every method may throw `com.landinfo.client.LoginException` on credential / network / parse failure.
- Times are ISO-8601 strings (e.g. `2026-06-30T10:15:00`).
- Pagination is zero-based: `page=0` returns the first page.

---

## 1. `login` — Obtain a token

```
login(String username, String password)             // uses default server URL
login(String serverUrl, String username, String password)
```

### Parameters

| Name       | Type     | Required | Description |
| ---------- | -------- | -------- | ----------- |
| serverUrl  | String   | Yes (overload) | Forest Tree Net base URL. Defaults to the value resolved from config — see [CONFIGURATION.md](./CONFIGURATION.md). |
| username   | String   | Yes     | Login name. |
| password   | String   | Yes     | Password. |

### Return value

| Field    | Type    | Description |
| -------- | ------- | ----------- |
| success  | Boolean | Whether authentication succeeded. |
| token    | String  | Bearer token on success. Valid for **24 hours**. |
| msg      | String  | Error message on failure. |

### Example

```java
JSONObject resp = ForestTreeNetClient.login("alice", "s3cret");
if (resp.getBooleanValue("success")) {
    String token = resp.getString("token");
    // ... use the token for subsequent calls
}
```

---

## 2. `queryTreeLandRanges` — List sample plots

```
queryTreeLandRanges(String token, JSONObject body)
```

Queries the list of monitoring sample plots (`样地`) visible to the authenticated user.

### Parameters

| Name           | Type    | Required | Description |
| -------------- | ------- | -------- | ----------- |
| token          | String  | Yes      | Bearer token from `login()`. |
| landNumber     | String  | No       | Filter by sample-plot number. |
| name           | String  | No       | Filter by sample-plot name. |
| belongType     | String  | No       | Monitoring mode: `single` (单木监测样地) or `standard` (标准样地). |
| shengCode      | String  | No       | Province code. |
| shiCode        | String  | No       | City code. |
| xianCode       | String  | No       | County code. |
| startTime      | String  | No       | Filter — created-time range start. |
| endTime        | String  | No       | Filter — created-time range end. |
| isDelete       | Boolean | **Yes**  | Whether deleted plots are included. Default: `false`. |
| returnStNum    | Boolean | No       | Whether to also return the device count per plot. |
| page           | Integer | No       | Page index, zero-based. |
| size           | Integer | No       | Page size. |
| sortList       | Array   | No       | Sort spec. Default: created-time descending. Example: `["create_date_time desc"]`. |

### Return value

| Field          | Type    | Description |
| -------------- | ------- | ----------- |
| success        | Boolean | Whether the query succeeded. |
| content        | Array   | Array of sample-plot objects (see fields below). |
| totalElements  | Integer | Total record count (for pagination). |

### Sample-plot object fields

| Field            | Type    | Description |
| ---------------- | ------- | ----------- |
| id               | String  | Primary key. |
| belongType       | String  | `single` or `standard`. |
| landNumber       | String  | Sample-plot number. |
| name             | String  | Sample-plot name. |
| address          | String  | Full street address. |
| shengCode        | String  | Province code. |
| shengName        | String  | Province name. |
| shiCode          | String  | City code. |
| shiName          | String  | City name. |
| xianCode         | String  | County code. |
| xianName         | String  | County name. |
| xiangCode        | String  | Township code. |
| xiangName        | String  | Township name. |
| cunCode          | String  | Village code. |
| cunName          | String  | Village name. |
| isDelete         | Boolean | Whether the plot is deleted. |
| stNum            | Integer | Number of devices in the plot (only if `returnStNum=true`). |
| projectId        | String  | Parent project ID. |
| createAuthor     | String  | Creator account. |
| createDateTime   | String  | Creation time. |
| remark           | String  | Notes. |

### Example

```java
JSONObject body = new JSONObject();
body.put("isDelete", false);
body.put("page", 0);
body.put("size", 20);
body.put("sortList", Arrays.asList("create_date_time desc"));

JSONObject resp = ForestTreeNetClient.queryTreeLandRanges(token, body);
JSONArray plots = resp.getJSONArray("content");
```

---

## 3. `queryTreeInfos` — List sample trees

```
queryTreeInfos(String token, JSONObject body)
```

Returns the sample trees (`样木`) that belong to a given sample plot.

### Parameters

| Name        | Type    | Required | Description |
| ----------- | ------- | -------- | ----------- |
| token       | String  | Yes      | Bearer token from `login()`. |
| landNumber  | String  | **Yes**  | Sample-plot number. |
| treeNumber  | String  | No       | Filter by tree number. |
| page        | Integer | No       | Page index, zero-based. |
| size        | Integer | No       | Page size. |
| sort        | String  | No       | Sort spec. Default: `tree_number` ascending. |

### Return value

| Field          | Type    | Description |
| -------------- | ------- | ----------- |
| success        | Boolean | Whether the query succeeded. |
| content        | Array   | Array of sample-tree objects. |
| totalElements  | Integer | Total record count. |

### Sample-tree object fields

| Field               | Type    | Description |
| ------------------- | ------- | ----------- |
| id                  | String  | Primary key. |
| landNumber          | String  | Sample-plot number. |
| species             | String  | Tree species. |
| treeNumber          | String  | Tree number. |
| dm                  | Double  | Diameter at breast height (DBH), in **mm**. |
| height              | Double  | Total tree height. |
| address             | String  | Address. |
| imgUrl              | String  | Tree image URL. |
| longitude           | Double  | Longitude. |
| latitude            | Double  | Latitude. |
| xj                  | Double  | Volume, in **m³**. |
| swl                 | Double  | Biomass, in **kg**. |
| tcl                 | Double  | Carbon stock, in **kg**. |
| createDateTime      | String  | Creation time. |
| measureStationSn    | String  | Sn of the bound tree-diameter device. |
| treeMeasureStations | Array   | Bound tree-diameter device(s). Normally a single entry — see section 4 for fields. |
| remark              | String  | Notes. |

### Example

```java
JSONObject body = new JSONObject();
body.put("landNumber", "PLOT-2025-001");
body.put("page", 0);
body.put("size", 50);

JSONObject resp = ForestTreeNetClient.queryTreeInfos(token, body);
```

---

## 4. `queryTreeMeasureStations` — List tree-diameter devices

```
queryTreeMeasureStations(String token, JSONObject body)
```

Returns the tree-diameter sensor / gateway devices (`树径设备`) belonging to a sample plot.

### Parameters

| Name        | Type    | Required | Description |
| ----------- | ------- | -------- | ----------- |
| token       | String  | Yes      | Bearer token from `login()`. |
| landNumber  | String  | **Yes**  | Sample-plot number. |
| sn          | String  | No       | Filter by device serial number. |
| treeNumber  | String  | No       | Filter by the bound tree number. |
| type        | String  | No       | Device type: `基站` (gateway) or `传感器` (sensor). |
| returnTree  | Boolean | No       | Whether to also return the bound tree info. Default: `false`. |
| returnData  | Boolean | No       | Whether to also return the latest reading. Default: `false`. |
| page        | Integer | No       | Page index, zero-based. |
| size        | Integer | No       | Page size. |
| sort        | String  | No       | Sort spec. Default: `sn` ascending. |

### Return value

| Field          | Type    | Description |
| -------------- | ------- | ----------- |
| success        | Boolean | Whether the query succeeded. |
| data           | Array   | Array of device objects. |
| totalElements  | Integer | Total record count. |

### Device object fields

| Field            | Type    | Description |
| ---------------- | ------- | ----------- |
| id               | String  | Primary key. |
| sn               | String  | Device serial number. |
| landNumber       | String  | Parent sample-plot number. |
| type             | String  | `基站` (gateway) or `传感器` (sensor). |
| dm               | Double  | DBH, in **mm**. |
| bat              | Double  | Battery voltage, in **mV**. |
| angle            | Double  | Angle, in **°**. |
| temp             | Double  | Temperature, in **℃**. |
| hum              | Double  | Humidity, in **%**. |
| treeNumber       | String  | Bound tree number. |
| status           | String  | Device status. |
| installed        | Boolean | Whether installed. |
| installTime      | String  | Installation time. |
| enable           | Boolean | Whether enabled. |
| longitude        | Double  | Longitude. |
| latitude         | Double  | Latitude. |
| createDateTime   | String  | Creation time. |
| updateDateTime   | String  | Update time. |
| remark           | String  | Notes. |
| treeInfo         | Object  | Bound tree info, only when `returnTree=true`. |
| latestData       | Object  | Latest reading, only when `returnData=true`. |

### Example

```java
JSONObject body = new JSONObject();
body.put("landNumber", "PLOT-2025-001");
body.put("returnTree", true);
body.put("returnData", true);
body.put("page", 0);
body.put("size", 100);

JSONObject resp = ForestTreeNetClient.queryTreeMeasureStations(token, body);
```

---

## 5. `queryTreeMeasureData` — Query historical sensor data

```
queryTreeMeasureData(String token, JSONObject body)
```

Returns paginated historical readings uploaded by a tree-diameter device.

### Parameters

| Name        | Type    | Required | Description |
| ----------- | ------- | -------- | ----------- |
| token       | String  | Yes      | Bearer token from `login()`. |
| sn          | String  | **Yes**  | Device serial number. |
| treeNumber  | String  | No       | Bound tree number. |
| page        | Integer | No       | Page index, zero-based. |
| size        | Integer | No       | Page size. |
| sort        | String  | No       | Sort spec. Default: `create_date_time desc`. |

### Return value

| Field          | Type    | Description |
| -------------- | ------- | ----------- |
| success        | Boolean | Whether the query succeeded. |
| data           | Array   | Array of reading objects. |
| totalElements  | Integer | Total record count. |

### Reading object fields

| Field            | Type    | Description |
| ---------------- | ------- | ----------- |
| id               | String  | Primary key. |
| sn               | String  | Tree-diameter device serial. |
| stationSn        | String  | Gateway serial. |
| landNumber       | String  | Parent sample-plot number. |
| landName         | String  | Parent sample-plot name. |
| treeNumber       | String  | Tree number. |
| dm               | Double  | DBH, in **mm**. |
| angle            | Double  | Angle, in **°**. |
| bat              | Double  | Tree-diameter device battery voltage, in **mV**. |
| relayBat         | Double  | Gateway battery voltage, in **mV**. |
| longitude        | Double  | Longitude. |
| latitude         | Double  | Latitude. |
| reportDateTime   | String  | Reading timestamp. |
| remark           | String  | Notes. |

### Example

```java
JSONObject body = new JSONObject();
body.put("sn", "TDS-2025-0001");
body.put("page", 0);
body.put("size", 100);

JSONObject resp = ForestTreeNetClient.queryTreeMeasureData(token, body);
```

---

## Error handling

Every method may throw `com.landinfo.client.LoginException`. Common causes:

| Cause                              | What happens                                                                 |
| ---------------------------------- | ---------------------------------------------------------------------------- |
| Username / password is blank      | `LoginException` is thrown **before** any HTTP call.                          |
| Server URL is blank                | `LoginException` is thrown **before** any HTTP call.                          |
| Server returns non-JSON            | `LoginException` wrapping the raw body.                                       |
| HTTP 401 / token expired           | The exception message contains the server's `msg` field.                      |
| Network failure                    | The underlying `IOException` is wrapped in `LoginException`.                   |
| Server returns `success=false`     | The method **returns** the response `JSONObject` — it does **not** throw.    |

Always check `success` on the returned `JSONObject` for business-level failures:

```java
JSONObject resp = ForestTreeNetClient.queryTreeInfos(token, body);
if (!resp.getBooleanValue("success")) {
    log.warn("query failed: {}", resp.getString("msg"));
}
```

---

## See also

- [QUICK_START.md](./QUICK_START.md) — 5-minute end-to-end setup.
- [CONFIGURATION.md](./CONFIGURATION.md) — Server URL resolution, custom HTTP clients, logging.
- [FAQ.md](./FAQ.md) — Common pitfalls and fixes.