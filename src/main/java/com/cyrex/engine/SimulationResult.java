package com.cyrex.engine;

import java.util.List;

/**
 * Immutable result of a single simulation run.
 */
public class SimulationResult {
    private final boolean attackDetected;
    private final boolean attackMitigated;
    private final List<String> compromisedNodes;
    private final double systemHealthPercent;
    private final double defenseEfficiencyPercent;
    private final double resilienceScore;
    private final double damageImpact;
    private final String strategyName;

    public SimulationResult(boolean attackDetected, boolean attackMitigated,
                            List<String> compromisedNodes, double systemHealthPercent,
                            double defenseEfficiencyPercent, double resilienceScore,
                            double damageImpact, String strategyName) {
        this.attackDetected = attackDetected;
        this.attackMitigated = attackMitigated;
        this.compromisedNodes = compromisedNodes != null ? List.copyOf(compromisedNodes) : List.of();
        this.systemHealthPercent = systemHealthPercent;
        this.defenseEfficiencyPercent = defenseEfficiencyPercent;
        this.resilienceScore = resilienceScore;
        this.damageImpact = damageImpact;
        this.strategyName = strategyName != null ? strategyName : "";
    }

    public boolean isAttackDetected() { return attackDetected; }
    public boolean isAttackMitigated() { return attackMitigated; }
    public List<String> getCompromisedNodes() { return compromisedNodes; }
    public double getSystemHealthPercent() { return systemHealthPercent; }
    public double getDefenseEfficiencyPercent() { return defenseEfficiencyPercent; }
    public double getResilienceScore() { return resilienceScore; }
    public double getDamageImpact() { return damageImpact; }
    public String getStrategyName() { return strategyName; }
}
