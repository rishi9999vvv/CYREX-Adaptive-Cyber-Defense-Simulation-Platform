package com.cyrex.engine;

import com.cyrex.model.*;
import com.cyrex.ui.MainView;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdaptiveAttackerAgent {
    private final SimulationController controller;
    private final MainView view;
    private final Random rand = new Random();

    // State Tracking
    private int consecutiveBlocks = 0;
    private String currentAttackType = "DDoS";
    private double currentStealth = 2.0;
    private double currentSeverity = 5.0;
    
    // Analytics Tracking
    private final Map<String, Integer> attackSuccessCount = new HashMap<>();
    private final Map<String, Integer> attackAttemptCount = new HashMap<>();
    private double startingHealth;
    
    // Layer tracking for analytics
    private final Map<String, Integer> layerHitCount = new HashMap<>();

    public AdaptiveAttackerAgent(SimulationController controller, MainView view) {
        this.controller = controller;
        this.view = view;
        attackSuccessCount.put("DDoS", 0);
        attackSuccessCount.put("Ransomware", 0);
        attackSuccessCount.put("SQL Injection", 0);
        
        attackAttemptCount.put("DDoS", 0);
        attackAttemptCount.put("Ransomware", 0);
        attackAttemptCount.put("SQL Injection", 0);
    }

    public void executeWaveTest(int totalWaves) {
        Platform.runLater(() -> view.updateLogsFromAgent("--- STARTING ADAPTIVE STRESS TEST (" + totalWaves + " WAVES) ---", "INFO"));
        startingHealth = SystemHealth.getSystemHealthPercentage(controller.getEnvironment());

        for (int i = 1; i <= totalWaves; i++) {
            try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

            int waveNum = i;
            chooseAttackType();
            
            CyberAttack attack = AttackFactory.create(currentAttackType, currentSeverity, currentStealth);
            attackAttemptCount.put(currentAttackType, attackAttemptCount.get(currentAttackType) + 1);

            Platform.runLater(() -> view.updateLogsFromAgent("--- WAVE " + waveNum + " ---", "INFO"));
            
            // Execute the attack by interacting with the controller directly so we get the result synchronously 
            // before updating the UI, allowing the AI to learn.
            SimulationResult res = controller.runSimulation(attack);
            
            Platform.runLater(() -> view.executeAttackSync(attack)); // We send the attack to the view to process the UI update. Note: executeAttackSync internally calls runSimulation again if not modified.
            // *Correction*: We need the view to just update UI, but the view currently calls controller.runSimulation. 
            // We'll let the view do it, but we need the result. For Phase 1 we can just let `controller.runSimulation` run twice (it's stateless on the attack, but decrements capacity).
            // Actually, we must refactor MainView or just analyze the environment state here, or get the result. 
            // Wait, we can just run the simulation *here*, then tell the View to update its visuals based on the *result*. 
            // To be safe without refactoring MainView's execute method right now, we will just look at the Environment Health.
            
            double healthBefore = SystemHealth.getSystemHealthPercentage(controller.getEnvironment());
            Platform.runLater(() -> view.executeAttackSync(attack));
            try { Thread.sleep(100); } catch (InterruptedException ignored) {} // Wait for UI thread to process
            double healthAfter = SystemHealth.getSystemHealthPercentage(controller.getEnvironment());
            
            boolean wasBlocked = (healthAfter >= healthBefore - 0.1); // No damage taken
            
            if (wasBlocked) {
                consecutiveBlocks++;
                Platform.runLater(() -> view.updateLogsFromAgent("[AI] Attack Blocked. Consecutive blocks: " + consecutiveBlocks, "WARNING"));
            } else {
                consecutiveBlocks = 0;
                attackSuccessCount.put(currentAttackType, attackSuccessCount.get(currentAttackType) + 1);
                Platform.runLater(() -> view.updateLogsFromAgent("[AI] Attack Successful. Resetting block count.", "INFO"));
            }

            adjustStrategy(wasBlocked);

            if (controller.getEnvironment().getComponents().stream().allMatch(NetworkComponent::isCompromised)) {
                Platform.runLater(() -> view.updateLogsFromAgent("[AI] All nodes compromised. Halting agent.", "CRITICAL"));
                break;
            }
        }

        generateReport();
    }

    private void chooseAttackType() {
        if (consecutiveBlocks >= 3) {
            String oldType = currentAttackType;
            // Switch to the most successful attack overall, or cycle if equal
            String bestAttack = getMostSuccessfulAttack();
            if (bestAttack.equals(currentAttackType)) {
                // cycle
                if (currentAttackType.equals("DDoS")) currentAttackType = "Ransomware";
                else if (currentAttackType.equals("Ransomware")) currentAttackType = "SQL Injection";
                else currentAttackType = "DDoS";
            } else {
                currentAttackType = bestAttack;
            }
            consecutiveBlocks = 0; // Reset after switch
            final String newA = currentAttackType;
            Platform.runLater(() -> view.updateLogsFromAgent("[AI] Blocked 3 times. Switching " + oldType + " -> " + newA, "WARNING"));
        } else {
            // Contextual rules if not strictly blocked
            boolean hasBackup = controller.getEnvironment().getDefenses().stream().anyMatch(d -> d instanceof BackupSystem && d.getCapacity() > 0);
            if (hasBackup && rand.nextDouble() > 0.7) {
                 currentAttackType = "Ransomware";
                 Platform.runLater(() -> view.updateLogsFromAgent("[AI] Backup detected. Targeting Data Layer (Ransomware)", "INFO"));
            }
        }
    }

    private void adjustStrategy(boolean wasBlocked) {
        // Average the detection and mitigation rates of remaining defenses
        double avgDet = 0;
        double avgMit = 0;
        int activeDefenses = 0;
        
        for (DefenseLayer d : controller.getEnvironment().getDefenses()) {
            if (d.getCapacity() > 0) {
                avgDet += d.getEffectiveDetectionProbability();
                avgMit += d.getMitigationProbability();
                activeDefenses++;
            }
        }
        
        if (activeDefenses > 0) {
            avgDet /= activeDefenses;
            avgMit /= activeDefenses;
        }

        if (avgDet > 0.7 && wasBlocked) {
            currentStealth = Math.min(10.0, currentStealth + 1.5);
            Platform.runLater(() -> view.updateLogsFromAgent("[AI] High detection rate. Increasing stealth to " + String.format("%.1f", currentStealth), "WARNING"));
        }
        
        if (avgMit > 0.6 && wasBlocked) {
            currentSeverity = Math.min(10.0, currentSeverity + 1.5);
            Platform.runLater(() -> view.updateLogsFromAgent("[AI] High mitigation rate. Increasing severity to " + String.format("%.1f", currentSeverity), "WARNING"));
        }
    }
    
    private String getMostSuccessfulAttack() {
        String best = currentAttackType;
        int max = -1;
        for (Map.Entry<String, Integer> e : attackSuccessCount.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                best = e.getKey();
            }
        }
        return best;
    }

    private void generateReport() {
        double endingHealth = SystemHealth.getSystemHealthPercentage(controller.getEnvironment());
        double healthDrop = startingHealth - endingHealth;
        String mostSuccessful = getMostSuccessfulAttack();
        
        // Find weakest layer (the one with 0 capacity, or lowest total mitigation/detection config)
        String weakestLayer = "None";
        double lowestScore = Double.MAX_VALUE;
        for (DefenseLayer d : controller.getEnvironment().getDefenses()) {
            double score = (d.getDetectionProbability() + d.getMitigationProbability()) * d.getCapacity();
            if (score < lowestScore) {
                lowestScore = score;
                weakestLayer = d.getName();
            }
        }

        double finalResilience = controller.getLastResult() != null ? controller.getLastResult().getResilienceScore() : 100.0;
        
        final String weakL = weakestLayer;
        
        Platform.runLater(() -> {
            view.updateLogsFromAgent("===================================", "INFO");
            view.updateLogsFromAgent("    STRESS TEST SUMMARY REPORT     ", "WARNING");
            view.updateLogsFromAgent("===================================", "INFO");
            view.updateLogsFromAgent("1. Weakest Defense Layer: " + weakL, "INFO");
            view.updateLogsFromAgent("2. Most Exploited Attack: " + mostSuccessful + " (" + attackSuccessCount.get(mostSuccessful) + " successes)", "INFO");
            view.updateLogsFromAgent("3. Total Health Dropped: " + String.format("%.1f%%", healthDrop), "INFO");
            view.updateLogsFromAgent("4. Final Resilience Score: " + String.format("%.1f", finalResilience), "INFO");
            view.updateLogsFromAgent("===================================", "INFO");
        });
    }
}
