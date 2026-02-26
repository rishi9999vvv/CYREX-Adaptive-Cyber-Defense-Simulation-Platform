package com.cyrex.engine;

import com.cyrex.model.CyberAttack;
import com.cyrex.model.DDoSAttack;
import com.cyrex.model.RansomwareAttack;
import com.cyrex.model.SQLInjectionAttack;

/**
 * Factory for creating attack instances by type (supports extensibility).
 */
public class AttackFactory {
    public static CyberAttack create(String attackType, double severity, double stealth) {
        if (attackType == null) attackType = "DDoS";
        return switch (attackType.toUpperCase()) {
            case "DDOS" -> new DDoSAttack(severity, stealth);
            case "RANSOMWARE" -> new RansomwareAttack(severity, stealth);
            case "SQL INJECTION", "SQLINJECTION" -> new SQLInjectionAttack(severity, stealth);
            default -> new DDoSAttack(severity, stealth);
        };
    }

}
