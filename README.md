<div align="center">

# BtTreeGauge / Forest Tree Net Client

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Java Version](https://img.shields.io/badge/Java-1.8%2B-blue.svg)](https://www.java.com)
[![Version](https://img.shields.io/badge/Version-1.0.0-green.svg)](./CHANGELOG.md)
[![JAR Size](https://img.shields.io/badge/JAR-2.1%20MB-lightgrey.svg)](./lib/)

A lightweight Java SDK for talking to the **Forest Tree Net** (森林树径监测) monitoring platform — query sample plots, sample trees, tree-diameter devices, and historical sensor data with a few lines of code.

[English](#english) · [中文](#中文) · [产品介绍 / Product](#产品介绍--product)

</div>

---

## 产品介绍 / Product

> **树木胸径生长监测仪 —— 一次安装，守护十年**

This SDK is the **software side** of an end-to-end forest-monitoring solution. The hardware counterpart is the **Tree DBH Growth Monitor** (树木胸径生长监测仪), a strap-mounted, industrial-grade displacement sensor built by **Sichuan Enternet Technology** (四川恩特网联科技有限公司). The sensor samples DBH 7×24, uploads over 4G / NB-IoT / LoRa, and feeds data into the Forest Tree Net platform — which is exactly what this SDK queries.

<table>
<tr>
<td width="55%" valign="top">

**核心特性 / Key features**

- 🎯 **±1 mm 精度** / millimeter accuracy
- ⏱️ **7×24 实时监测** / continuous 7×24 monitoring
- 🔋 **≥10 年续航** / 10-year battery life
- 📡 **多通信方式** / 4G · NB-IoT · LoRa
- 🌲 **30 秒无损安装** / 30-second strap install
- 📊 **生长曲线自动生成** / automatic growth curves

</td>
<td width="45%" align="center">

![device mounted on tree](./assets/product/device-on-tree.png)

</td>
</tr>
</table>

**典型应用场景 / Typical applications**

| 场景 / Scenario | 一句话 / In one line |
| --- | --- |
| 国有林场 / State forest | 全林分长期生长监测 / Continuous growth monitoring across stands |
| 国储林 / Reserve forest | 多年度生长模型与上报 / Multi-year growth models and reporting |
| 碳汇监测 / Carbon sink | 连续 DBH 增量 → 生物量 / 碳储量 / Continuous DBH → biomass / carbon |
| 古树名木 / Heritage trees | 单株长期健康趋势 / Per-tree long-term health trend |
| 样地监测 / Sample plots | 科研级连续时间序列 / Research-grade continuous time series |
| 林木倾倒预警 / Tree-fall alert | 倾角实时监测 / Real-time tilt monitoring |

👉 完整产品介绍、技术参数、应用案例：[docs/PRODUCT.md](./docs/PRODUCT.md) · [docs/USE_CASES.md](./docs/USE_CASES.md)

---

## English

### What is this?

`BtTreeGauge` (a.k.a. `forest-tree-net-client`) is a Java client library that wraps the HTTP API of the **Forest Tree Net** platform — a forest-resource monitoring system built around BLE/LoRa tree-diameter sensors and gateways. The SDK is published by **Sichuan Enternet Technology Co., Ltd.** (四川恩特网联科技有限公司) and exposes a thin, type-safe façade over the platform's JSON endpoints.

It is intended for system integrators, researchers, and forestry-application developers who need to pull monitoring data (sample plots, sample trees, sensor readings, etc.) into their own Java applications, ETL pipelines, or dashboards.

### Features

- 🚀 **Zero-boilerplate** — A handful of static methods; no DI, no Spring, no configuration files required.
- 🔐 **Token-based auth** — Built-in `login()` handles authentication; the returned token is reused across calls.
- 📋 **Five core endpoints covered** — Sample plots, sample trees, tree-diameter devices, sensor history, and a generic `post()` escape hatch.
- 🧰 **Sensible defaults** — Server URL can be set via classpath properties, JVM system property, or environment variable.
- ⚙️ **Pluggable HTTP client** — Bring your own `org.apache.http.client.HttpClient` if you need custom TLS / proxy / pooling behavior.
- 📦 **Tiny surface** — Ships as a single ~2 MB fat-JAR with shaded Apache HttpClient, FastJSON, and SLF4J.

### Quick start

```java
import com.alibaba.fastjson.JSONObject;
import com.landinfo.client.ForestTreeNetClient;
import com.landinfo.client.LoginException;

public class Demo {
    public static void main(String[] args) throws LoginException {
        // 1) Authenticate (token is valid for 24 hours)
        JSONObject login = ForestTreeNetClient.login("your-username", "your-password");
        String token = login.getString("token");

        // 2) Query sample plots
        JSONObject body = new JSONObject();
        body.put("page", 0);
        body.put("size", 20);
        body.put("isDelete", false);
        JSONObject plots = ForestTreeNetClient.queryTreeLandRanges(token, body);

        System.out.println(plots);
    }
}
```

For the full walkthrough — including device queries, history retrieval, and Maven/Gradle setup — see **[docs/QUICK_START.md](docs/QUICK_START.md)**.

### Installation

The artifact is published as a single fat-JAR under [`lib/forest-tree-net-client-1.0.0.jar`](./lib/).

**Maven** (local install):

```bash
mvn install:install-file \
    -Dfile=lib/forest-tree-net-client-1.0.0.jar \
    -DgroupId=com.landinfo \
    -DartifactId=forest-tree-net-client \
    -Dversion=1.0.0 \
    -Dpackaging=jar
```

Then add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.landinfo</groupId>
    <artifactId>forest-tree-net-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

> The fat-JAR already shades Apache HttpClient, FastJSON, and SLF4J, so no extra dependencies are required for the common case.

### Documentation

| Document | Purpose |
| --- | --- |
| [docs/PRODUCT.md](docs/PRODUCT.md) | Hardware product overview (传感器 + 平台) |
| [docs/USE_CASES.md](docs/USE_CASES.md) | Six typical application scenarios |
| [docs/QUICK_START.md](docs/QUICK_START.md) | 5-minute getting-started guide |
| [docs/API.md](docs/API.md) | Full API reference (parameters, return fields, examples) |
| [docs/CONFIGURATION.md](docs/CONFIGURATION.md) | Server URL, HTTP client override, logging |
| [docs/FAQ.md](docs/FAQ.md) | Frequently asked questions |
| [docs/树径监测jar包接口文档.docx](docs/树径监测jar包接口文档.docx) | Original Word doc from the vendor |
| [examples/](examples/) | Runnable Java examples |

### Project layout

```
BtTreeGauge/
├── lib/                          # Prebuilt fat-JAR (shaded dependencies)
│   └── forest-tree-net-client-1.0.0.jar
├── docs/                         # All documentation
│   ├── API.md
│   ├── QUICK_START.md
│   ├── CONFIGURATION.md
│   ├── PRODUCT.md                # Hardware product overview
│   ├── USE_CASES.md              # Six typical application scenarios
│   ├── FAQ.md
│   └── 树径监测jar包接口文档.docx  # Original Word doc from the vendor
├── assets/                       # Images & media
│   └── product/                  # Product photos used in PRODUCT.md
├── examples/                     # Runnable Java samples
│   └── BasicUsage.java
├── CHANGELOG.md
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── SECURITY.md
├── LICENSE                       # MIT
└── README.md
```

### Contributing

Issues and pull requests are welcome. Please read [CONTRIBUTING.md](./CONTRIBUTING.md) and follow the [Code of Conduct](./CODE_OF_CONDUCT.md).

### Security

To report a vulnerability privately, see [SECURITY.md](./SECURITY.md). **Do not open public issues for security problems.**

### License

This project is released under the [MIT License](./LICENSE).

---

## 中文

### 这是什么？

`BtTreeGauge`（又名 `forest-tree-net-client`）是一个 Java 客户端 SDK，用于对接**森林树径监测平台**的 HTTP 接口。该平台围绕 BLE/LoRa 树径传感器和网关构建，服务于林业资源监测场景。本 SDK 由**四川恩特网联科技有限公司**发布，在平台 JSON 接口之上提供了一层简洁、类型安全的封装。

适合需要在自有 Java 应用、ETL 流水线或可视化大屏中拉取样地、样木、设备、历史监测数据等的系统集成商、研究人员和林业应用开发者使用。

### 特性

- 🚀 **零样板代码** —— 几个静态方法即可上手，无需依赖注入、无需 Spring、无需配置文件。
- 🔐 **基于 Token 的鉴权** —— 内置 `login()` 流程，返回的 token 可在后续所有调用中复用。
- 📋 **覆盖 5 个核心接口** —— 样地查询、样木查询、树径设备查询、历史监测数据，以及通用的 `post()` 入口。
- 🧰 **开箱即用的默认配置** —— 服务地址可通过 classpath 配置文件、JVM 系统属性或环境变量配置。
- ⚙️ **可插拔的 HTTP 客户端** —— 如需自定义 TLS / 代理 / 连接池，可注入自己的 `HttpClient`。
- 📦 **极小的依赖面** —— 单个 ~2 MB 的 fat-JAR，已 shade Apache HttpClient、FastJSON、SLF4J。

### 快速开始

```java
import com.alibaba.fastjson.JSONObject;
import com.landinfo.client.ForestTreeNetClient;
import com.landinfo.client.LoginException;

public class Demo {
    public static void main(String[] args) throws LoginException {
        // 1) 登录（token 有效期 24 小时）
        JSONObject login = ForestTreeNetClient.login("用户名", "密码");
        String token = login.getString("token");

        // 2) 查询样地列表
        JSONObject body = new JSONObject();
        body.put("page", 0);
        body.put("size", 20);
        body.put("isDelete", false);
        JSONObject plots = ForestTreeNetClient.queryTreeLandRanges(token, body);

        System.out.println(plots);
    }
}
```

完整示例（含设备查询、历史数据拉取、Maven/Gradle 配置）请见 **[docs/QUICK_START.md](docs/QUICK_START.md)**。

### 安装

构件以单个 fat-JAR 形式发布，位于 [`lib/forest-tree-net-client-1.0.0.jar`](./lib/)。

**Maven** 本地安装：

```bash
mvn install:install-file \
    -Dfile=lib/forest-tree-net-client-1.0.0.jar \
    -DgroupId=com.landinfo \
    -DartifactId=forest-tree-net-client \
    -Dversion=1.0.0 \
    -Dpackaging=jar
```

然后在 `pom.xml` 中加入：

```xml
<dependency>
    <groupId>com.landinfo</groupId>
    <artifactId>forest-tree-net-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

> fat-JAR 已包含 Apache HttpClient、FastJSON、SLF4J，常规场景无需额外依赖。

### 文档

| 文档 | 说明 |
| --- | --- |
| [docs/PRODUCT.md](docs/PRODUCT.md) | 硬件产品介绍（传感器 + 平台） |
| [docs/USE_CASES.md](docs/USE_CASES.md) | 六个典型应用场景 |
| [docs/QUICK_START.md](docs/QUICK_START.md) | 5 分钟上手指南 |
| [docs/API.md](docs/API.md) | 完整 API 参考（参数、返回字段、示例） |
| [docs/CONFIGURATION.md](docs/CONFIGURATION.md) | 服务地址配置、HTTP 客户端覆盖、日志 |
| [docs/FAQ.md](docs/FAQ.md) | 常见问题 |
| [docs/树径监测jar包接口文档.docx](docs/树径监测jar包接口文档.docx) | 厂商原始 Word 文档 |
| [examples/](examples/) | 可运行的 Java 示例 |

### 项目结构

```
BtTreeGauge/
├── lib/                          # 预编译 fat-JAR（已 shade 依赖）
│   └── forest-tree-net-client-1.0.0.jar
├── docs/                         # 全部文档
│   ├── API.md
│   ├── QUICK_START.md
│   ├── CONFIGURATION.md
│   ├── PRODUCT.md                # 硬件产品介绍
│   ├── USE_CASES.md              # 六个典型应用场景
│   ├── FAQ.md
│   └── 树径监测jar包接口文档.docx
├── assets/                       # 图片与媒体资源
│   └── product/                  # PRODUCT.md 中使用的产品图
├── examples/                     # 可运行的 Java 示例
│   └── BasicUsage.java
├── CHANGELOG.md
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── SECURITY.md
├── LICENSE                       # MIT
└── README.md
```

### 贡献

欢迎提交 Issue 和 Pull Request。请先阅读 [CONTRIBUTING.md](./CONTRIBUTING.md) 并遵守 [Code of Conduct](./CODE_OF_CONDUCT.md)。

### 安全

如需私下上报漏洞，请阅读 [SECURITY.md](./SECURITY.md)。**请勿在公开 Issue 中讨论安全问题。**

### 协议

本项目基于 [MIT 协议](./LICENSE) 发布。

---
