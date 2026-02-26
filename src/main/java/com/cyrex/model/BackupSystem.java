package com.cyrex.model;

/**
 * Concrete defense: Backup system - helps recovery (mitigation after damage).
 */
public class BackupSystem extends DefenseLayer {
    public BackupSystem(String name, double detectionProbability, double mitigationProbability) {
        super(name, detectionProbability, mitigationProbability, Zone.DATA);
    }

    public BackupSystem(String name) {
        this(name, 0.2, 0.6);
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
