package com.cyrex.engine;

import com.cyrex.model.*;
import com.cyrex.strategy.DefenseStrategy;
import com.cyrex.strategy.LayeredDefenseStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton controller: holds the current network environment and strategy,
 * and delegates simulation to SimulationEngine. Entry point for UI.
 */
public class SimulationController {
    private static final SimulationController INSTANCE = new SimulationController();
    private final NetworkEnvironment environment = new NetworkEnvironment();
    private final SimulationEngine engine = new SimulationEngine();
    private DefenseStrategy currentStrategy = new LayeredDefenseStrategy();
    private final Map<String, DefenseStrategy> strategies = new HashMap<>();
    private SimulationResult lastResult;

    static {
        INSTANCE.strategies.put("Layered Defense", new LayeredDefenseStrategy());
    }

    private SimulationController() {}

    public static SimulationController getInstance() {
        return INSTANCE;
    }

    public NetworkEnvironment getEnvironment() {
        return environment;
    }

    public void setStrategy(String strategyName) {
        DefenseStrategy s = strategies.get(strategyName);
        if (s != null) {
            currentStrategy = s;
        }
    }

    public DefenseStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    public Map<String, DefenseStrategy> getStrategies() {
        return new HashMap<>(strategies);
    }

    public SimulationResult runSimulation(CyberAttack attack) {
        lastResult = engine.runSimulation(environment, attack, currentStrategy);
        return lastResult;
    }

    public SimulationResult runSimulationWithStrategy(CyberAttack attack, String strategyName) {
        DefenseStrategy s = strategies.get(strategyName);
        if (s == null) s = currentStrategy;
        lastResult = engine.runSimulation(environment, attack, s);
        return lastResult;
    }

    public SimulationResult getLastResult() {
        return lastResult;
    }
}
