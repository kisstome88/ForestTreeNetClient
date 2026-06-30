# Security Policy

## Supported versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

Only the latest minor release receives security updates. Please upgrade before reporting.

## Reporting a vulnerability

**Please do not open a public GitHub issue for security problems.**

Instead, report privately via one of the following channels:

- **Email**: `<your-security-email@example.com>` (preferred — please use a subject like `[BtTreeGauge][security] ...`).
- **GitHub private vulnerability report**: *Repository → Security → Advisories → "Report a vulnerability"*.

We aim to acknowledge new reports within **3 business days** and to provide a fix or mitigation plan within **30 days** for high-severity issues.

When filing, please include:

- A clear description of the issue and the impact (RCE, info leak, DoS, etc.).
- Steps to reproduce, ideally with a minimal Java snippet.
- The version affected (`lib/forest-tree-net-client-1.0.0.jar` for the prebuilt, or your build).
- Any known workarounds.

## Scope

In-scope issues include:

- Authentication bypass or token leakage in `ForestTreeNetClient.login`.
- Unsafe deserialization in `post(...)` or query methods (the library uses FastJSON — please review `JSON.parse`/`toJavaObject` usage patterns in any PRs you submit).
- TLS / certificate-handling mistakes in the bundled HTTP client defaults.
- Credential or URL leakage in logs / error messages.

Out of scope:

- Bugs in upstream dependencies (please report to the upstream project).
- Issues requiring a malicious server (we treat the Forest Tree Net server as trusted).

## Disclosure policy

We follow a **coordinated disclosure** model:

1. Reporter contacts us privately.
2. We confirm and develop a fix on a private branch.
3. We release a patched version and publish a GitHub Security Advisory with full credit to the reporter (unless they wish to remain anonymous).

We kindly ask reporters to give us a reasonable window (typically up to 90 days) before any public disclosure.