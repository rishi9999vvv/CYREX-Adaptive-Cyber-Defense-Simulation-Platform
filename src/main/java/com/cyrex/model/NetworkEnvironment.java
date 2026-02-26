package com.cyrex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the network topology: components and defense layers.
 * Used by SimulationEngine to run attacks and apply defenses.
 */
public class NetworkEnvironment {
    private final List<NetworkComponent> components = new ArrayList<>();
    private final List<DefenseLayer> defenses = new ArrayList<>();

    public List<NetworkComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public List<DefenseLayer> getDefenses() {
        return Collections.unmodifiableList(defenses);
    }

    public void addComponent(NetworkComponent component) {
        if (component != null) {
            components.add(component);
        }
    }

    public void addDefense(DefenseLayer defense) {
        if (defense != null) {
            defenses.add(defense);
        }
    }

    public void removeComponent(NetworkComponent component) {
        components.remove(component);
    }

    public void removeDefense(DefenseLayer defense) {
        defenses.remove(defense);
    }

    public void resetHealth() {
        for (NetworkComponent c : components) {
            c.setHealth(100.0);
            c.setCompromised(false);
        }
    }

    public void clearDefenses() {
        defenses.clear();
    }
}
