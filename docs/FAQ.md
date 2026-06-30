# FAQ

## General

### What is this library for?

`forest-tree-net-client` (a.k.a. `BtTreeGauge`) is a Java SDK that wraps the HTTP API of the **Forest Tree Net** monitoring platform — a system built around BLE/LoRa tree-diameter sensors and gateways. See [README.md](../README.md) for the elevator pitch and [API.md](./API.md) for the full method reference.

### What's the difference between this and the original `forest-tree-net-client`?

There is none — they are the same library. `BtTreeGauge` is just the GitHub repository name; the Maven artifact, Java package (`com.landinfo.client`), and class names all remain unchanged for backwards compatibility.

### Does the SDK work with Spring / Spring Boot?

Yes — it's a plain `static-method` API, so just call it from any Spring bean. For configuration, see [CONFIGURATION.md](./CONFIGURATION.md#spring-boot).

## Installation

### I get `ClassNotFoundException: org/apache/http/...` at runtime.

You are using the **non-shaded** version of the library (i.e. you installed the JAR without its shaded dependencies). Either:

- Use the fat-JAR under [`lib/`](../lib/) and put it on the classpath, **or**
- Add explicit dependencies on `org.apache.httpcomponents:httpclient`, `com.alibaba:fastjson`, and `org.slf4j:slf4j-api`.

### Can I depend on this from Gradle / sbt / Ivy?

Yes — install the fat-JAR into your local Maven repo first:

```bash
mvn install:install-file \
    -Dfile=lib/forest-tree-net-client-1.0.0.jar \
    -DgroupId=com.landinfo \
    -DartifactId=forest-tree-net-client \
    -Dversion=1.0.0 \
    -Dpackaging=jar
```

Then point Gradle / sbt / Ivy at your local Maven repo.

## Authentication

### My token expires mid-run — what do I do?

Tokens are valid for **24 hours**. The recommended pattern is to cache the token and refresh it a few minutes before expiry. See the [`TokenCache`](./QUICK_START.md#6-token-caching-24h-validity) example in the Quick Start.

### `login()` returns `success=false` with `msg=...` — what now?

Inspect `msg`:

| `msg` contains            | Likely cause                                       |
| ------------------------- | -------------------------------------------------- |
| `用户名或密码错误`        | Wrong credentials.                                  |
| `账号被锁定`              | Account locked — contact your platform admin.       |
| `captcha` / `验证码`      | The server requires a captcha for this account.     |
| `IP not allowed`          | Your IP isn't in the server's allow-list.           |

### Can I use OAuth / API keys?

Not currently. The Forest Tree Net platform exposes username / password authentication only.

## Server / network

### How do I point the SDK at a different server?

See [CONFIGURATION.md → Server URL](./CONFIGURATION.md#server-url). In short: system property > env var > classpath properties.

### I get `ConnectException: Connection timed out`.

- Verify the server URL is correct (`curl -I $YOUR_URL`).
- Check firewall / proxy / VPN.
- The bundled default server URL (`http://kisstome99.qicp.vip:38084/...`) is a sample / dev URL — replace it with your production URL.

### SSL / TLS handshake fails.

The SDK uses Apache HttpClient's default `SSLSocketFactory`. If you need a custom truststore, override the `HttpClient` (see [CONFIGURATION.md → HTTP client override](./CONFIGURATION.md#http-client-override)).

## API behavior

### `queryTreeInfos` returns `totalElements=0` even though `queryTreeLandRanges` shows plots with trees.

Check that you passed the right `landNumber`. The two methods are independent — you must pass the exact `landNumber` string.

### Pagination — is `page` zero-based or one-based?

**Zero-based.** `page=0` is the first page.

### Sort syntax — what is `sortList`?

A list of `"<field> <direction>"` strings, e.g. `["create_date_time desc", "name asc"]`. Omit to get the server default.

### What's the difference between `dm`, `xj`, `swl`, and `tcl`?

| Field | Meaning                                                | Unit |
| ----- | ------------------------------------------------------ | ---- |
| `dm`  | Diameter at breast height (胸径)                         | mm   |
| `xj`  | Volume (蓄积)                                            | m³   |
| `swl` | Biomass (生物量)                                          | kg   |
| `tcl` | Carbon stock (碳储量)                                    | kg   |

## Error handling

### When does the SDK throw `LoginException`?

- Username / password / server URL is blank.
- Network failure.
- Server returns non-JSON or malformed JSON.
- HTTP 401 (token expired or invalid).

It does **not** throw for `success=false` business errors — those come back in the returned `JSONObject`. Always check `success`:

```java
JSONObject resp = ForestTreeNetClient.queryTreeInfos(token, body);
if (!resp.getBooleanValue("success")) {
    log.warn("query failed: {}", resp.getString("msg"));
}
```

## Contributing / source

### Where's the source code?

This repository publishes the **prebuilt fat-JAR** plus reference docs and examples. The upstream Java source for `ForestTreeNetClient`, `ServerUrlResolver`, and `HttpClientFactory` is maintained internally at Sichuan Enternet Technology.

### I found a bug — how do I report it?

Please open a GitHub issue with:

- The exact JAR version (`lib/forest-tree-net-client-1.0.0.jar`).
- JDK version (`java -version`).
- A minimal reproduction.
- Server logs if available.

For **security** issues, see [SECURITY.md](../SECURITY.md) — **do not open public issues**.