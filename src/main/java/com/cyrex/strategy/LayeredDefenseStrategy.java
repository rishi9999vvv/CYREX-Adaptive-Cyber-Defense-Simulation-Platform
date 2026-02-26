package com.cyrex.strategy;

import com.cyrex.model.CyberAttack;
import com.cyrex.model.DefenseLayer;
import com.cyrex.model.NetworkEnvironment;

import java.util.List;
import java.util.Random;

/**
 * Layered defense: apply defenses in sequence. Detection and mitigation
 * compound (each layer has a chance to detect/mitigate).
 */
public class LayeredDefenseStrategy implements DefenseStrategy {
    private static final Random RND = new Random();

    @Override
    public DefenseResult applyDefenses(NetworkEnvironment env, CyberAttack attack, List<DefenseLayer> defenses) {
        if (defenses == null || defenses.isEmpty()) {
            return new DefenseResult(false, false, 0, 0);
        }
        double stealth = attack.getStealthPenalty();
        double combinedDetection = 0;
        double combinedMitigation = 0;
        // Layered: probability that at least one layer detects / mitigates
        double pNotDetect = 1.0;
        double pNotMitigate = 1.0;
        for (DefenseLayer d : defenses) {
            if (d.getZone() != attack.getTargetZone()) {
                continue; // Defense only protects its designated zone
            }
            double det = d.getEffectiveDetectionProbability() * stealth;
            double mit = d.getMitigationProbability();
            pNotDetect *= (1 - det);
            pNotMitigate *= (1 - mit);
            d.decrementCapacity();
        }
        double detectionRate = 1 - pNotDetect;
        double mitigationRate = 1 - pNotMitigate;
        boolean detected = RND.nextDouble() < detectionRate;
        boolean mitigated = RND.nextDouble() < mitigationRate;
        return new DefenseResult(detected, mitigated, detectionRate, mitigationRate);
    }

    @Override
    public String getStrategyName() {
        return "Layered Defense";
    }
}
