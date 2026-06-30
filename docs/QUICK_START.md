# Quick Start

This guide walks you from a fresh checkout to a working Java program that talks to the Forest Tree Net platform — in about five minutes.

## 1. Prerequisites

- **JDK 8 or newer** (`java -version` should report 1.8+).
- **Maven 3.x** *or* **Gradle 5.x** (only if you want to install the fat-JAR into your local repo).
- A **Forest Tree Net account** with `username` / `password` and a reachable server URL.
- **Outbound HTTPS** to the Forest Tree Net server.

## 2. Get the artifact

The prebuilt fat-JAR lives at [`lib/forest-tree-net-client-1.0.0.jar`](../lib/forest-tree-net-client-1.0.0.jar).

### Option A — Drop it into your classpath directly

```bash
javac -cp "lib/forest-tree-net-client-1.0.0.jar" MyApp.java
java  -cp ".:lib/forest-tree-net-client-1.0.0.jar" MyApp
```

> The fat-JAR already shades Apache HttpClient, FastJSON, and SLF4J, so no extra `-cp` entries are needed.

### Option B — Install into your local Maven repo

```bash
mvn install:install-file \
    -Dfile=lib/forest-tree-net-client-1.0.0.jar \
    -DgroupId=com.landinfo \
    -DartifactId=forest-tree-net-client \
    -Dversion=1.0.0 \
    -Dpackaging=jar
```

Then in `pom.xml`:

```xml
<dependency>
    <groupId>com.landinfo</groupId>
    <artifactId>forest-tree-net-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option C — Gradle

Drop the JAR into `libs/`, then in `build.gradle`:

```groovy
dependencies {
    implementation files('libs/forest-tree-net-client-1.0.0.jar')
}
```

## 3. Configure the server URL

Pick **one** of the following — they're tried in order:

| Priority | Source                                  | Example |
| -------- | --------------------------------------- | ------- |
| 1        | JVM system property                      | `-DforestTreeNet.serverUrl=https://example.com/forest-tree-net/` |
| 2        | Environment variable                    | `FOREST_TREE_NET_SERVER_URL=https://example.com/forest-tree-net/` |
| 3        | `forest-tree-net-client.properties` on the classpath | see below |

Example `src/main/resources/forest-tree-net-client.properties`:

```properties
forestTreeNet.serverUrl=https://example.com/forest-tree-net/
```

The URL **must end with `/`** — the SDK will append one if missing.

## 4. First call: login → list plots

Create `MyApp.java`:

```java
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.landinfo.client.ForestTreeNetClient;
import com.landinfo.client.LoginException;

public class MyApp {
    public static void main(String[] args) throws LoginException {
        // 1) Authenticate
        JSONObject loginResp = ForestTreeNetClient.login("alice", "s3cret");
        if (!loginResp.getBooleanValue("success")) {
            System.err.println("login failed: " + loginResp.getString("msg"));
            return;
        }
        String token = loginResp.getString("token");
        System.out.println("token = " + token + " (valid for 24h)");

        // 2) Query the first 20 sample plots
        JSONObject body = new JSONObject();
        body.put("page", 0);
        body.put("size", 20);
        body.put("isDelete", false);

        JSONObject plotsResp = ForestTreeNetClient.queryTreeLandRanges(token, body);
        if (!plotsResp.getBooleanValue("success")) {
            System.err.println("query failed: " + plotsResp.getString("msg"));
            return;
        }

        JSONArray plots = plotsResp.getJSONArray("content");
        System.out.println("got " + plots.size() + " / "
                + plotsResp.getIntValue("totalElements") + " plots");
        for (int i = 0; i < plots.size(); i++) {
            JSONObject p = plots.getJSONObject(i);
            System.out.printf("- %s | %s | %s%n",
                    p.getString("landNumber"),
                    p.getString("name"),
                    p.getString("address"));
        }
    }
}
```

Compile and run:

```bash
javac -cp "lib/forest-tree-net-client-1.0.0.jar" MyApp.java
java  -cp ".:lib/forest-tree-net-client-1.0.0.jar" MyApp
```

Expected output (credentials and data will, of course, differ):

```
token = eyJhbGciOiJIUzI1NiJ9... (valid for 24h)
got 20 / 137 plots
- PLOT-2025-001 | 西岭样地 | 四川省成都市大邑县...
- PLOT-2025-002 | 卧龙样地 | 四川省成都市汶川县...
```

## 5. Walking the rest of the API

The five methods you will use 95% of the time:

```
login(username, password)                              → JSONObject (token)
queryTreeLandRanges(token, body)                      → JSONObject (plots)
queryTreeInfos(token, body)                           → JSONObject (trees)
queryTreeMeasureStations(token, body)                 → JSONObject (devices)
queryTreeMeasureData(token, body)                     → JSONObject (readings)
```

Each method's full request/response contract is documented in [API.md](./API.md). A worked example that walks **all five** end-to-end (login → plots → trees → devices → readings) is at [examples/BasicUsage.java](../examples/BasicUsage.java).

## 6. Token caching (24h validity)

Tokens are valid for 24 hours. Don't call `login()` for every request — cache it:

```java
public class TokenCache {
    private static final long EXPIRY_MARGIN_MS = 5 * 60 * 1000L; // refresh 5 min early
    private String token;
    private long expiresAt;

    public synchronized String get() throws LoginException {
        if (token == null || System.currentTimeMillis() >= expiresAt - EXPIRY_MARGIN_MS) {
            JSONObject resp = ForestTreeNetClient.login("alice", "s3cret");
            if (!resp.getBooleanValue("success")) {
                throw new LoginException("login failed: " + resp.getString("msg"));
            }
            token = resp.getString("token");
            expiresAt = System.currentTimeMillis() + 24L * 60 * 60 * 1000;
        }
        return token;
    }
}
```

## 7. Troubleshooting

- **`LoginException: 服务器地址不能为空`** — your server URL is empty. Set it via system property, env var, or classpath properties file (see [CONFIGURATION.md](./CONFIGURATION.md)).
- **`LoginException: 用户名和密码不能为空`** — `username` or `password` is `null` / blank.
- **Token expires mid-run** — your 24h window elapsed. Re-run `login()` (the cache above does this automatically).
- **Wrong server URL silently used** — system property beats env var beats properties file. Double-check with `System.getProperty("forestTreeNet.serverUrl")` and `System.getenv("FOREST_TREE_NET_SERVER_URL")`.

For more, see [FAQ.md](./FAQ.md).