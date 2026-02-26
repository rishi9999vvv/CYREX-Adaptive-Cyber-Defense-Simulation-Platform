package com.cyrex.engine;

import com.cyrex.model.NetworkComponent;
import com.cyrex.model.NetworkEnvironment;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Computes aggregate system health and damage impact from the network environment.
 */
public final class SystemHealth {
    private SystemHealth() {}

    public static double getSystemHealthPercentage(NetworkEnvironment env) {
        List<NetworkComponent> components = env.getComponents();
        if (components.isEmpty()) return 100.0;
        double sum = 0;
        for (NetworkComponent c : components) {
            sum += c.getHealth();
        }
        return sum / components.size();
    }

    public static double getDamageImpact(NetworkEnvironment env) {
        List<NetworkComponent> components = env.getComponents();
        if (components.isEmpty()) return 0;
        double sum = 0;
        for (NetworkComponent c : components) {
            sum += (100 - c.getHealth()) / 100.0;
        }
        return sum / components.size(); // 0 = no damage, 1 = total damage
    }

    public static List<String> getCompromisedNodeNames(NetworkEnvironment env) {
        return env.getComponents().stream()
                .filter(NetworkComponent::isCompromised)
                .map(NetworkComponent::getName)
                .collect(Collectors.toList());
    }
}
