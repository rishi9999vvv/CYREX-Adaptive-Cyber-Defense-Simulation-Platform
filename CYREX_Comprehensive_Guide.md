# CYREX Phase 1.5 - The Ultimate Study Guide & Reference Manual

This document contains everything you need to know about your Java project, from the theoretical cybersecurity concepts to the underlying Object-Oriented Programming (OOP) logic, down to the exact presentation flow.

---

## 📅 Part 1: Presentation Flow & Speaker Notes

This section provides the exact content for your slides along with *what you should actually say* (your speaker notes) while presenting.

### Slide 1 – Title Slide
*   **Content:** Project Title (CYREX Phase 1.5), Team Details, Guide Name, Date.
*   **What to say:** "Good morning/afternoon everyone. Today, my team and I will be presenting CYREX, which stands for Cyber Resilience Evaluation and Stress Testing Platform."

### Slide 2 – Introduction
*   **Content:** Background on Defense-in-Depth, Domain relevance, Motivation.
*   **What to say:** "In modern cybersecurity, a single firewall is never enough. Companies rely on something called 'Defense-in-Depth'—layering multiple security measures. However, many academic tools oversimplify this into basic math. Our motivation was to build a tool that actually simulates path-based attacks against a layered network to see how architectural design choices impact survival."

### Slide 3 – Problem Definition
*   **Content:** Evaluating structural integrity against adaptive threats is hard without live malware. Identify gaps in current educational models.
*   **What to say:** "The core problem is this: how do you safely test if your network architecture is fundamentally sound against a threat that actually learns and adapts? You can't just deploy live ransomware. Current educational tools often rely on flat 'hit or miss' probabilities. We decided to build a simulator that factors in exactly *where* a defense is placed and tests it with an AI that actively tries to find the weak spots."

### Slide 4 – Proposed Solution
*   **Content:** Overview of CYREX Architecture Builder, Zone Mechanics (`EDGE`, `INTERNAL`, `DATA`), Adaptive AI.
*   **What to say:** "Our solution is CYREX Phase 1.5. It’s an Architecture Builder and Stress Testing Engine. We divided the network into strict zones: Edge, Internal, and Data. You design the defenses, and our Adaptive AI Agent launches 20 waves of attacks. If the AI gets blocked at the Edge, it adapts. It might immediately switch vectors to try and bypass the Edge and directly strike the Data layer with a SQL Injection instead."

### Slide 5 & 6 – OOP Design Overview & Concepts
*   **Content:** Encapsulation, Inheritance, Polymorphism, Abstraction, Interfaces.
*   **What to say (The most important technical slide):** "To build this, we relied entirely on Java OOP principles. 
    1. We used **Inheritance** by creating a base `NetworkComponent` class, which our specific `Server` and `Database` classes extend to inherit core health mechanics.
    2. We used strict **Encapsulation**—health values are private and clamped using setter methods so a server's health can never drop below zero.
    3. We heavily relied on **Polymorphism**. Our simulation engine just calls `attack.execute()`. It doesn't need to know the attack type—the Java runtime dynamically determines if it should run the DDoS logic or the Ransomware logic.
    4. Finally, we used an **Interface** for our `DefenseStrategy`, meaning we can plug in entirely new mathematical models later without rewriting our engine."

### Slide 7 – Methodology / Development Plan
*   **Content:** Agile approach, Java 17, JavaFX, IDE, GitHub.
*   **What to say:** "We developed this iteratively using Java 17 and JavaFX for a fluid, dark-mode cyber aesthetic. We explicitly chose not to use drag-and-drop tools like Scene Builder, instead writing the entire UI programmatically in Java to maintain strict control over our dynamic layout. The entire project is version-controlled via GitHub."

### Slide 8 – Module Breakdown
*   **Content:** Model Layer, Engine Layer, AI Layer, UI Layer.
*   **What to say:** "The architecture is split into four clean layers. The Model Layer holds our data objects. The Engine Layer calculates the probabilities. The AI layer governs the attacker's adaptative choices, and the UI layer handles the live JavaFX renderings."

### Slide 9 & 10 – Timeline & Conclusion
*   **Content:** Completed Model, UI, and Zone Routing. Final conclusions.
*   **What to say:** "To conclude, CYREX successfully demonstrates that flat defenses are useless against intelligent threats. By simulating strictly zoned architecture, we created a highly realistic, visually impressive educational tool that accurately mirrors real-world cybersecurity problems."

---

## 🧠 Part 2: The Core Concepts (The Engine)

If a professor asks, "How does the math actually work?", this is what you need to know.

### 1. The Defense Strategy Math
The core logic resides in `LayeredDefenseStrategy.java`.
It calculates whether an attack is **detected** and **mitigated** using combined probability.
*   If you have two Firewalls, each with a 50% (0.5) detection chance.
*   The chance of the first Firewall missing it is 0.5. The chance of the second missing it is 0.5.
*   Total chance of missing it entirely: `0.5 * 0.5 = 0.25` (25%).
*   Therefore, the **combined detection rate** is `1.0 - 0.25 = 0.75` (75%).
*   *Why did we do this?* Because in reality, layering defenses has diminishing returns, and this math perfectly simulates that.

### 2. Network Zones (Defense-In-Depth)
We divided the network into 3 physical areas (the `Zone` enum):
1.  **EDGE:** The gateway. This holds standard `Servers`. It is protected by a `Firewall`.
2.  **INTERNAL:** The application logic. This holds `ApplicationServers`. Protected by `IDS` (Intrusion Detection System).
3.  **DATA:** The databases (`DatabaseServer`). Protected by `BackupSystem`.

### 3. Path-Based Attacks
Because of zones, attacks no longer hit the "whole network" randomly.
*   **DDoS Attack:** Hardcoded to hit `Zone.EDGE`. If it gets past the firewall, it degrades the health of your Edge Servers.
*   **SQL Injection / Ransomware:** Hardcoded to hit `Zone.DATA`. 
*   *The Trap:* If a user buys 5 Firewalls (Edge) but 0 Backup Systems (Data), a DDoS will fail instantly. But a SQL Injection will bypass the firewalls entirely and instantly destroy the Data zone.

### 4. The Adaptive AI Agent
Located in `AdaptiveAttackerAgent.java`. This is what makes the project truly dynamic.
It runs a 20-wave loop. It tracks state:
*   `consecutiveBlocks`: If an attack is completely mitigated, this counter goes up. If it reaches 3, the AI realizes the current vector is useless and swaps its attack type.
*   `adjustStrategy()`: If the AI notices the defense has an 80% detection rate, it will dynamically artificially buff its `stealth` stat to try and slip past the sensors next round.

---

## 💻 Part 3: The Most Important Code Snippets Explained

If you are asked to open the code and explain what a specific part does, memorize these.

### 1. Polymorphism in Action
Look inside `SimulationEngine.java` at the `runSimulation()` method.
```java
public SimulationResult runSimulation(CyberAttack attack) {
    // ... math happens ...
    
    // THE MAGIC LINE
    if (!mitigated) {
        attack.execute(environment);
    }
}
```
**Explanation for Professor:** "Here is our best example of Polymorphism. The engine does not have bulky `if(attack == DDoS)` statements. It simply calls `.execute()`. The Java runtime reads the object type and dynamically executes the unique damage logic written inside that specific attack class."

### 2. Encapsulation & Data Integrity
Look inside `NetworkComponent.java` at the `setHealth()` method.
```java
public void setHealth(double health) {
    this.health = Math.max(0, Math.min(100, health));
}
```
**Explanation for Professor:** "This is strict Encapsulation. Direct access to the `health` variable is blocked. If an attack does 200 damage, this setter method intercepts it and clamps the maximum drop to 0. The health will legally never drop below exactly 0%."

### 3. The Zone Routing Logic
Look inside `LayeredDefenseStrategy.java` in the `for` loop.
```java
for (DefenseLayer d : defenses) {
    if (d.getZone() != attack.getTargetZone()) {
        continue; // Defense only protects its designated zone
    }
    // ... math logic applies the defense ...
}
```
**Explanation for Professor:** "This is our Phase 1.5 path-routing logic. Before applying defense probability math against an attack, we check if the Defense's Zone matches the Attack's Target Zone. If it doesn't match—for example, an EDGE Firewall trying to stop a DATA Ransomware attack—the engine executes `continue`, entirely skipping that defense layer."

### 4. Programmatic UI Generation
Look inside `MainView.java` at the `refreshNetworkView()` method.
```java
for (NetworkComponent c : controller.getEnvironment().getComponents()) {
    String lbl = "SERVER";
    if (c.getZone() == Zone.EDGE) lbl = "EDGE";
    else if (c.getZone() == Zone.INTERNAL) lbl = "INTERNAL";
    else if (c.getZone() == Zone.DATA) lbl = "DATA";
    
    VBox card = createCard(c.getName(), lbl, "status-safe");
    networkPanel.getChildren().add(card);
}
```
**Explanation for Professor:** "We generate our UI programmatically using Java objects (`VBox`). This loop iterates through our backend network models, reads their Zone property, dynamically applies the correct visual label (e.g., 'DATA'), and pushes it live to the JavaFX screen without ever relying on external drag-and-drop FXML builder files."
