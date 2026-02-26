package com.cyrex.model;

import java.util.List;

/**
 * Ransomware: high impact on first components, can spread.
 */
public class RansomwareAttack extends CyberAttack {
    public RansomwareAttack(double severity, double stealthLevel) {
        super("Ransomware", severity, stealthLevel, Zone.DATA);
    }

    @Override
    public void execute(NetworkEnvironment env) {
        List<NetworkComponent> targets = env.getComponents().stream()
                .filter(c -> c.getZone() == this.targetZone)
                .toList();
        if (targets.isEmpty()) return;
        double damage = 0.5 * getSeverityMultiplier(); // high single-target damage
        targets.get(0).applyDamage(damage);
        if (targets.size() > 1) {
            targets.get(1).applyDamage(damage * 0.5);
        }
    }
}
