# Configuration

This document covers every knob you can turn on `forest-tree-net-client` — server URL resolution, HTTP-client override, logging, and timeouts.

## Server URL

The SDK must know the base URL of your Forest Tree Net deployment. Three sources are checked, in priority order:

| Priority | Source                                                | Set by                                                                              |
| -------- | ----------------------------------------------------- | ----------------------------------------------------------------------------------- |
| 1        | JVM system property `forestTreeNet.serverUrl`         | `-DforestTreeNet.serverUrl=https://...`                                             |
| 2        | Environment variable `FOREST_TREE_NET_SERVER_URL`     | `export FOREST_TREE_NET_SERVER_URL=https://...`                                      |
| 3        | `forest-tree-net-client.properties` on the classpath  | `forestTreeNet.serverUrl=https://...`                                                |

The SDK ships its own default in `lib/forest-tree-net-client-1.0.0.jar` → `forest-tree-net-client.properties`:

```properties
forestTreeNet.serverUrl=http://kisstome99.qicp.vip:38084/forest-tree-net/
```

You can override the default at runtime by:

```bash
java -DforestTreeNet.serverUrl=https://my-deployment.example.com/forest-tree-net/ -cp ... MyApp
```

Or permanently, by putting your own `forest-tree-net-client.properties` **earlier** on the classpath than the fat-JAR (classpath order wins).

The URL **must end with `/`**. The SDK appends one if missing.

### Per-call override

If you need to call a different server for one request, use the 3-arg `login` overload or `post(serverUrl, token, path, body)`:

```java
String token = ForestTreeNetClient
        .login("https://other-host/", "alice", "s3cret")
        .getString("token");

JSONObject resp = ForestTreeNetClient.post(
        "https://other-host/",
        token,
        "api/tree/landRange/query",
        body);
```

## HTTP client override

For custom TLS, proxy, connection pooling, or auth schemes, plug in your own `HttpClient`:

```java
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

HttpClient mine = HttpClients.custom()
        .setMaxConnTotal(200)
        .setMaxConnPerRoute(20)
        .disableRedirectHandling()
        .build();

// package-private setter, exposed via reflection or a future public API
// ForestTreeNetClient.setHttpClient(mine);
```

> ⚠️ `setHttpClient` is currently package-private. If you need it, either:
> 1. Open an issue and we'll promote it to public, or
> 2. Subclass / fork the SDK, or
> 3. Drive the HTTP layer yourself via `ForestTreeNetClient.post(serverUrl, token, path, body)`.

## Logging

The SDK logs via SLF4J. The fat-JAR ships `slf4j-api` but **no binding** — you add your own.

Add **one** of:

```xml
<!-- Maven: logback -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.12</version>
</dependency>
```

```xml
<!-- Maven: slf4j-simple -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.36</version>
</dependency>
```

To see request/response detail, set the logger to `DEBUG`:

```properties
# logback.xml
<logger name="com.landinfo.client" level="DEBUG"/>
```

Output will include the full URL, headers, request body, and response body for each call.

## Timeouts

The bundled `HttpClientFactory` uses these defaults:

| Setting         | Default |
| --------------- | ------- |
| Connect timeout | 10 s    |
| Socket timeout  | 30 s    |

To change them, override the `HttpClient` (see above) and set your own `RequestConfig`:

```java
RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(5_000)
        .setSocketTimeout(60_000)
        .build();

CloseableHttpClient mine = HttpClients.custom()
        .setDefaultRequestConfig(config)
        .build();
```

## JSON serialization

The SDK uses **FastJSON** for both request bodies (via `JSON.toJSONString`) and response parsing (via `JSONObject.parseObject`). All return values are `JSONObject` / `JSONArray`, so you can introspect fields directly:

```java
JSONObject tree = trees.getJSONObject(0);
double dbh = tree.getDoubleValue("dm");     // mm
double lon = tree.getDoubleValue("longitude");
```

If you want strongly-typed POJOs, parse them yourself:

```java
TreeInfo info = tree.toJavaObject(TreeInfo.class);
```

> Be cautious with `toJavaObject` on FastJSON — auto-type resolution has historically been a vector for deserialization attacks. Stick to `JSONObject` unless you control the server's schema fully.

## Putting it all together

Typical `application.yml` (for Spring Boot, for example) might look like:

```yaml
forest-tree-net:
  server-url: https://my-deployment.example.com/forest-tree-net/
  connect-timeout-ms: 5000
  socket-timeout-ms: 60000
```

…and you wire them through:

```java
@Value("${forest-tree-net.server-url}")
private String serverUrl;

@PostConstruct
void init() {
    System.setProperty("forestTreeNet.serverUrl", serverUrl);
}
```