# CYREX – Cyber Resilience Evaluation & Stress Testing Platform

**Phase 1** – Modular Java OOP + JavaFX simulation (no real networking, no ML).

## What Phase 1 Includes

- **Network model**: `NetworkComponent`, `Server`, `DatabaseServer`
- **Attacks**: `DDoSAttack`, `RansomwareAttack`, `SQLInjectionAttack`
- **Defenses**: `Firewall`, `IDS`, `BackupSystem`
- **Strategy pattern**: `LayeredDefenseStrategy`, `ZeroTrustStrategy`
- **Simulation engine**: detection, mitigation, damage, resilience score (0–100)
- **JavaFX UI**: Network builder, attack panel, live results, strategy comparison

## Requirements

- **JDK 17 or 21** (e.g. from [Adoptium](https://adoptium.net/) or [Microsoft OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download)). The project **does not** work with Java 8 or 11.
- Maven is **not** required: the project includes a Maven Wrapper.

## Build & Run (no Maven installed)

**First time only** – download the Maven Wrapper JAR (needs internet).  
From **Command Prompt (cmd)** or any terminal, run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\setup-maven-wrapper.ps1
```

If you're already in **PowerShell**, you can use:

```powershell
.\setup-maven-wrapper.ps1
```

Then:

```cmd
# Compile
.\mvnw.cmd compile

# Run JavaFX app
.\mvnw.cmd javafx:run
```

**If `java` is not in your PATH**, use the helper scripts (they look for Java in common install folders):

```cmd
.\compile.cmd
.\run.cmd
```

If both Java and Maven are on your PATH you can use `mvn compile` and `mvn javafx:run` instead.

From IDE: run the main class `com.cyrex.ui.CyrexApp`.

## Demo Flow

1. **Network Architecture Builder** – Add Server, Database, Firewall, IDS; view topology list.
2. **Launch Attack Panel** – Choose attack type (DDoS / Ransomware / SQL Injection), severity (Low/Medium/High), stealth (Low/High), strategy; click **Run Simulation**.
3. **Live Simulation Result** – See: Attack detected? Mitigated? Compromised nodes, System health %, Defense efficiency %, Resilience score.
4. **Strategy Comparison** – Click **Run Same Attack with Both Strategies & Compare** to see Layered vs Zero Trust side by side.

## Resilience Formula

```
Resilience = (DetectionRate × 0.4) + (MitigationRate × 0.4) − (DamageImpact × 0.2)
```
Normalized to 0–100.

## Package Layout

- `com.cyrex.model` – Network components, attacks, defenses
- `com.cyrex.strategy` – Defense strategies
- `com.cyrex.engine` – Simulation engine, controller (singleton), attack factory, metrics
- `com.cyrex.ui` – JavaFX views
- `com.cyrex.util` – Helpers (optional)

Phase 1 does **not** include: multi-stage APT chains, cascading propagation, real networking, recommendation engine, or ML.

---

## Troubleshooting

**`invalid target release: 17`**  
Your current `java` is older than 17 (e.g. Java 8 or 11). This project requires **JDK 17 or 21**.

1. Install [Eclipse Temurin JDK 17](https://adoptium.net/temurin/releases/?version=17) (or JDK 21).
2. Add the JDK **bin** folder to your system **PATH** (e.g. `C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot\bin`).
3. In a **new** terminal, run `java -version` and confirm it shows version 17 or 21.
4. Then run `.\mvnw.cmd compile` and `.\mvnw.cmd javafx:run` again.

If you have several Java versions, ensure the one on PATH is 17 or 21 when you build and run.
