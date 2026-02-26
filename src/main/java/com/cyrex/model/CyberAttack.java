package com.cyrex.model;

/**
 * Abstract base for cyber attacks (OOP: polymorphism).
 * Subclasses implement execute() for different attack behaviors.
 */
public abstract class CyberAttack {
    protected double severity;       // 1-10
    protected double stealthLevel;   // 1-10
    protected Zone targetZone;

    private final String name;

    protected CyberAttack(String name, double severity, double stealthLevel, Zone targetZone) {
        this.name = name;
        this.severity = Math.max(1, Math.min(10, severity));
        this.stealthLevel = Math.max(1, Math.min(10, stealthLevel));
        this.targetZone = targetZone;
    }

    public String getName() {
        return name;
    }

    public double getSeverity() {
        return severity;
    }

    public double getStealthLevel() {
        return stealthLevel;
    }

    public Zone getTargetZone() {
        return targetZone;
    }

    /**
     * Execute attack against the environment. Engine will call this and use
     * detection/mitigation from defenses; this method can apply damage to components.
     */
    public abstract void execute(NetworkEnvironment env);

    /** Severity multiplier for damage (0.1 to 1.0 based on 1-10 scale). */
    public double getSeverityMultiplier() {
        return (severity / 10.0);
    }

    /** Stealth reduces effective detection probability (0.5 to 1.0 based on 1-10 scale). */
    public double getStealthPenalty() {
        // High stealth -> lower multiplier. 
        // 1 -> 1.0 penalty. 10 -> 0.55 penalty.
        return 1.0 - ((stealthLevel - 1) * 0.05);
    }
}
