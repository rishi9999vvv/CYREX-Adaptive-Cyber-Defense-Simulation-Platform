package com.cyrex.model;

/**
 * Concrete defense: Intrusion Detection System - high detection, lower direct mitigation.
 */
public class IDS extends DefenseLayer {
    public IDS(String name, double detectionProbability, double mitigationProbability) {
        super(name, detectionProbability, mitigationProbability, Zone.INTERNAL);
    }

    public IDS(String name) {
        this(name, 0.8, 0.4);
    }

    @Override
    public boolean detect(CyberAttack attack) {
        return Math.random() < (detectionProbability * attack.getStealthPenalty());
    }

    @Override
    public boolean mitigate(CyberAttack attack) {
        return Math.random() < mitigationProbability;
    }
}
