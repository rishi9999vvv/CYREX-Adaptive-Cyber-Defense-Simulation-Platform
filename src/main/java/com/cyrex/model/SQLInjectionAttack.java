package com.cyrex.model;

import java.util.List;

/**
 * SQL Injection: targets database servers primarily.
 */
public class SQLInjectionAttack extends CyberAttack {
    public SQLInjectionAttack(double severity, double stealthLevel) {
        super("SQL Injection", severity, stealthLevel, Zone.DATA);
    }

    @Override
    public void execute(NetworkEnvironment env) {
        double damage = 0.4 * getSeverityMultiplier();
        for (NetworkComponent c : env.getComponents()) {
            if (c.getZone() == this.targetZone) {
                if (c instanceof DatabaseServer) {
                    c.applyDamage(damage);
                } else {
                    c.applyDamage(damage * 0.3); // some collateral
                }
            }
        }
    }
}
