package com.cyrex.model;

/**
 * Abstract base for defense layers (Firewall, IDS, Backup).
 * Encapsulates detection/mitigation probability and resource cost.
 */
public abstract class DefenseLayer {
    private final String name;
    protected double detectionProbability; // 0.0 - 1.0
    protected double mitigationProbability; // 0.0 - 1.0
    protected int capacity = 3;
    protected Zone zone;

    protected DefenseLayer(String name, double detectionProbability, double mitigationProbability, Zone zone) {
        this.name = name;
        this.detectionProbability = clamp(detectionProbability);
        this.mitigationProbability = clamp(mitigationProbability);
        this.zone = zone;
    }

    private static double clamp(double v) {
        return Math.max(0, Math.min(1, v));
    }

    public String getName() {
        return name;
    }

    public double getDetectionProbability() {
        return detectionProbability;
    }

    public void setDetectionProbability(double prob) {
        this.detectionProbability = clamp(prob);
    }

    public double getEffectiveDetectionProbability() {
        return capacity <= 0 ? (detectionProbability * 0.5) : detectionProbability;
    }

    public double getMitigationProbability() {
        return mitigationProbability;
    }

    public void setMitigationProbability(double prob) {
        this.mitigationProbability = clamp(prob);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void decrementCapacity() {
        if (capacity > 0) capacity--;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public abstract boolean detect(CyberAttack attack);
    public abstract boolean mitigate(CyberAttack attack);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + name + "]";
    }
}
