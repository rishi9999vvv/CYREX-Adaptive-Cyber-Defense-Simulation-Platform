package com.cyrex.strategy;

import com.cyrex.model.CyberAttack;
import com.cyrex.model.DefenseLayer;
import com.cyrex.model.NetworkEnvironment;

import java.util.List;

/**
 * Strategy pattern: defines how defenses are applied during simulation.
 * Different strategies order or weight defenses differently.
 */
public interface DefenseStrategy {
    /**
     * Apply defenses in strategy-defined order; returns effective detection and mitigation results.
     */
    DefenseResult applyDefenses(NetworkEnvironment env, CyberAttack attack, List<DefenseLayer> defenses);

    String getStrategyName();

    /** Result of applying defenses: was attack detected and/or mitigated? */
    record DefenseResult(boolean detected, boolean mitigated, double effectiveDetectionRate, double effectiveMitigationRate) {}
}
