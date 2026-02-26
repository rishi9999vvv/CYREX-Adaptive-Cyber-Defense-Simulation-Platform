package com.cyrex.model;

import java.util.List;

/**
 * DDoS attack: affects availability, damages multiple components.
 */
public class DDoSAttack extends CyberAttack {
    public DDoSAttack(double severity, double stealthLevel) {
        super("DDoS", severity, stealthLevel, Zone.EDGE);
    }

    @Override
    public void execute(NetworkEnvironment env) {
        List<NetworkComponent> components = env.getComponents();
        double damage = 0.25 * getSeverityMultiplier(); // base 25% per component, scaled by severity
        for (NetworkComponent c : components) {
            if (c.getZone() == this.targetZone) {
                c.applyDamage(damage);
            }
        }
    }
}
