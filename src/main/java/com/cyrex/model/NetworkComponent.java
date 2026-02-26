package com.cyrex.model;

/**
 * Abstract base class for all network components (OOP: inheritance).
 * Encapsulates common state: name, security level, compromise status, health.
 */
public abstract class NetworkComponent {
    private String name;
    private double securityLevel;
    private boolean compromised;
    private double health; // 0-100
    private Zone zone;

    public NetworkComponent(String name, double securityLevel, Zone zone) {
        this.name = name;
        this.securityLevel = Math.max(1, Math.min(10, securityLevel));
        this.compromised = false;
        this.health = 100.0;
        this.zone = zone;
    }

    public NetworkComponent(String name, double securityLevel) {
        this(name, securityLevel, Zone.EDGE);
    }

    public String getName() {
        return name;
    }

    public double getSecurityLevel() {
        return securityLevel;
    }

    public boolean isCompromised() {
        return compromised;
    }

    public void setCompromised(boolean compromised) {
        this.compromised = compromised;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = Math.max(0, Math.min(100, health));
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    /** Reduce health by a damage factor (0.0 to 1.0). */
    public void applyDamage(double damageFactor) {
        setHealth(health * (1.0 - damageFactor));
        if (health <= 0) {
            setCompromised(true);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + name + ", health=" + String.format("%.1f", health) + "%, compromised=" + compromised + "]";
    }
}
