package com.cyrex.engine;

import com.cyrex.model.CyberAttack;
import com.cyrex.model.DefenseLayer;
import com.cyrex.model.NetworkEnvironment;
import com.cyrex.strategy.DefenseStrategy;
import com.cyrex.strategy.DefenseStrategy.DefenseResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Core simulation: runs attack against environment using selected strategy,
 * computes detection, mitigation, damage, and resilience score.
 */
public class SimulationEngine {
    private static final double RESILIENCE_DETECTION_WEIGHT = 0.4;
    private static final double RESILIENCE_MITIGATION_WEIGHT = 0.4;
    private static final double RESILIENCE_HEALTH_WEIGHT = 0.2;

    /**
     * Run one simulation: reset health, apply strategy for detection/mitigation,
     * optionally execute attack (if not mitigated), then compute metrics.
     */
    public SimulationResult runSimulation(NetworkEnvironment env, CyberAttack attack, DefenseStrategy strategy) {
        env.resetHealth();
        List<DefenseLayer> defenses = new ArrayList<>(env.getDefenses());
        DefenseResult result = strategy.applyDefenses(env, attack, defenses);
        boolean mitigated = result.mitigated();
        if (!mitigated) {
            attack.execute(env);
        }
        double detectionRate = result.effectiveDetectionRate();
        double mitigationRate = result.effectiveMitigationRate();
        double damageImpact = SystemHealth.getDamageImpact(env);
        double systemHealth = SystemHealth.getSystemHealthPercentage(env);
        double defenseEfficiency = (detectionRate + mitigationRate) / 2.0 * 100.0;
        double resilience = calculateResilienceScore(detectionRate, mitigationRate, systemHealth);
        List<String> compromised = SystemHealth.getCompromisedNodeNames(env);
        return new SimulationResult(
                result.detected(),
                mitigated,
                compromised,
                systemHealth,
                defenseEfficiency,
                resilience,
                damageImpact,
                strategy.getStrategyName()
        );
    }

    /**
     * Resilience = (DetectionRate * 0.4) + (MitigationRate * 0.4) + (RemainingHealth * 0.2)
     * Normalized to 0-100.
     */
    public static double calculateResilienceScore(double detectionRate, double mitigationRate, double systemHealth) {
        double raw = (detectionRate * RESILIENCE_DETECTION_WEIGHT)
                + (mitigationRate * RESILIENCE_MITIGATION_WEIGHT)
                + ((systemHealth / 100.0) * RESILIENCE_HEALTH_WEIGHT);
        return Math.max(0, Math.min(100, raw * 100));
    }
}
