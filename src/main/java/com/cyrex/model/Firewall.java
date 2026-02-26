package com.cyrex.model;

/**
 * Concrete defense: Firewall - strong on filtering, moderate on detection.
 */
public class Firewall extends DefenseLayer {
    public Firewall(String name, double detectionProbability, double mitigationProbability) {
        super(name, detectionProbability, mitigationProbability, Zone.EDGE);
    }

    public Firewall(String name) {
        this(name, 0.6, 0.7);
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
