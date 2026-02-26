package com.cyrex.ui;

import com.cyrex.engine.SimulationController;
import com.cyrex.engine.SimulationResult;
import com.cyrex.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Main JavaFX view: 3-Panel Layout (Config, Network, Live Log) + Bottom Metrics Bar
 */
public class MainView {
    private final SimulationController controller = SimulationController.getInstance();

    // UI Components
    private final ComboBox<String> attackTypeCombo = new ComboBox<>();
    private final Slider severitySlider = new Slider(1, 10, 5);
    private final Slider stealthSlider = new Slider(1, 10, 2);
    
    // Defense Builder Controls
    private final ComboBox<String> defTypeCombo = new ComboBox<>();
    private final Slider defDetSlider = new Slider(0, 1.0, 0.5);
    private final Slider defMitSlider = new Slider(0, 1.0, 0.5);
    private final Slider defCapSlider = new Slider(1, 10, 3);

    private final FlowPane networkPanel = new FlowPane(10, 10);
    private final Map<NetworkComponent, VBox> componentCards = new HashMap<>();
    private final Map<DefenseLayer, VBox> defenseCards = new HashMap<>();

    private final ObservableList<String> logItems = FXCollections.observableArrayList();
    private final ListView<String> liveLogConsole = new ListView<>(logItems);

    private final ProgressBar healthBar = new ProgressBar(1.0);
    private final Label resilienceScoreLabel = new Label("100.0");
    private final Label detectionRateLabel = new Label("0%");
    private final Label mitigationRateLabel = new Label("0%");

    public MainView() {
        initControls();
        setupInitialTopology();
    }

    private void initControls() {
        attackTypeCombo.getItems().addAll("DDoS", "Ransomware", "SQL Injection");
        attackTypeCombo.getSelectionModel().selectFirst();
        attackTypeCombo.setStyle("-fx-background-color: #30363D; -fx-text-fill: white;");

        severitySlider.setShowTickMarks(true);
        severitySlider.setShowTickLabels(true);
        severitySlider.setMajorTickUnit(1);
        severitySlider.setBlockIncrement(1);

        stealthSlider.setShowTickMarks(true);
        stealthSlider.setShowTickLabels(true);
        stealthSlider.setMajorTickUnit(1);
        stealthSlider.setBlockIncrement(1);

        defTypeCombo.getItems().addAll("Firewall", "IDS", "Backup System");
        defTypeCombo.getSelectionModel().selectFirst();
        defTypeCombo.setStyle("-fx-background-color: #30363D; -fx-text-fill: white;");

        defDetSlider.setShowTickMarks(true); defDetSlider.setMajorTickUnit(0.1); defDetSlider.setBlockIncrement(0.1);
        defMitSlider.setShowTickMarks(true); defMitSlider.setMajorTickUnit(0.1); defMitSlider.setBlockIncrement(0.1);
        defCapSlider.setShowTickMarks(true); defCapSlider.setMajorTickUnit(1); defCapSlider.setBlockIncrement(1);

        liveLogConsole.getStyleClass().add("log-console");
    }

    private void setupInitialTopology() {
        NetworkEnvironment env = controller.getEnvironment();
        
        env.addComponent(new Server("API-Gateway"));
        env.addComponent(new Server("Web-Node-1"));
        env.addComponent(new ApplicationServer("App-Logic-1"));
        env.addComponent(new ApplicationServer("App-Logic-2"));
        env.addComponent(new DatabaseServer("Customer-DB"));
        env.addComponent(new DatabaseServer("Auth-DB"));

        refreshNetworkView();
        
        log("[INFO] System Initialized. Fixed Corporate Topology Loaded.", "INFO");
    }



    public BorderPane getRoot() {
        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        root.getStyleClass().add("root");

        // Header
        Label headerTitle = new Label("CYREX Simulator");
        headerTitle.getStyleClass().add("header-text");
        
        HBox header = new HBox(headerTitle);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-border-width: 0 0 1 0;");
        root.setTop(header);

        // Center 3-Panel
        VBox leftPanels = new VBox(15);
        leftPanels.getChildren().addAll(buildConfigPanel(), buildDefensePanel());
        ScrollPane leftScroll = new ScrollPane(leftPanels);
        leftScroll.setFitToWidth(true);
        leftScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        HBox centerPanels = new HBox(15);
        centerPanels.setPadding(new Insets(15));
        centerPanels.getChildren().addAll(leftScroll, buildNetworkPanel(), buildLogPanel());
        HBox.setHgrow(centerPanels.getChildren().get(1), Priority.ALWAYS); // network panel grows
        root.setCenter(centerPanels);

        // Bottom Metrics
        root.setBottom(buildMetricsBar());

        return root;
    }

    // --- LEFT PANEL: CONFIG ---
    private VBox buildConfigPanel() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("panel-bg");
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(250);

        Label title = new Label("ATTACK CONFIG");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-font-size: 16px;");
        
        SVGPath attackIcon = new SVGPath();
        attackIcon.setContent("M12,2L1,21H23M12,6L19.53,19H4.47M11,10V14H13V10M11,16V18H13V16");
        attackIcon.setFill(Color.web("#FF4C4C"));
        HBox titleBox = new HBox(10, attackIcon, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label typeLbl = new Label("Attack Type");
        typeLbl.getStyleClass().add("label-text");

        Label sevLbl = new Label("Severity (1-10)");
        sevLbl.getStyleClass().add("label-text");

        Label stlLbl = new Label("Stealth (1-10)");
        stlLbl.getStyleClass().add("label-text");

        Button stressBtn = new Button("START AI STRESS TEST");
        stressBtn.getStyleClass().add("cyrex-button");
        stressBtn.setStyle("-fx-background-color: #FFD33D; -fx-text-fill: #0D1117;");
        stressBtn.setMaxWidth(Double.MAX_VALUE);
        stressBtn.setOnAction(e -> startAIStressTest());

        panel.getChildren().addAll(titleBox, typeLbl, attackTypeCombo, sevLbl, severitySlider, stlLbl, stealthSlider, new Region(), stressBtn);
        VBox.setVgrow(panel.getChildren().get(7), Priority.ALWAYS); // push buttons to bottom
        return panel;
    }

    private VBox buildDefensePanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("panel-bg");
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(250);

        Label title = new Label("DEFENSE BUILDER");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-font-size: 16px;");

        SVGPath defIcon = new SVGPath();
        defIcon.setContent("M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1Z");
        defIcon.setFill(Color.web("#1F6FEB"));
        HBox titleBox = new HBox(10, defIcon, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label typeLbl = new Label("Layer Type");
        typeLbl.getStyleClass().add("label-text");

        Label detLbl = new Label("Detection %");
        detLbl.getStyleClass().add("label-text");

        Label mitLbl = new Label("Mitigation %");
        mitLbl.getStyleClass().add("label-text");

        Label capLbl = new Label("Capacity (Hits)");
        capLbl.getStyleClass().add("label-text");

        Button addBtn = new Button("ADD LAYER");
        addBtn.getStyleClass().add("cyrex-button");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addDefenseLayer());

        Button clearBtn = new Button("CLEAR ARCHITECTURE");
        clearBtn.getStyleClass().add("cyrex-button");
        clearBtn.setStyle("-fx-background-color: #FF4C4C; -fx-text-fill: white;");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> clearDefenses());

        panel.getChildren().addAll(titleBox, typeLbl, defTypeCombo, detLbl, defDetSlider, mitLbl, defMitSlider, capLbl, defCapSlider, new Region(), addBtn, clearBtn);
        VBox.setVgrow(panel.getChildren().get(9), Priority.ALWAYS); // Spacer
        return panel;
    }

    private void addDefenseLayer() {
        String type = defTypeCombo.getValue();
        double det = defDetSlider.getValue();
        double mit = defMitSlider.getValue();
        int cap = (int) defCapSlider.getValue();
        
        DefenseLayer newLayer = null;
        int count = controller.getEnvironment().getDefenses().size() + 1;
        if (type.equals("Firewall")) {
            newLayer = new Firewall("Firewall-" + count);
        } else if (type.equals("IDS")) {
            newLayer = new IDS("IDS-" + count);
        } else {
            newLayer = new BackupSystem("Backup-" + count);
        }
        
        if (newLayer != null) {
            newLayer.setDetectionProbability(det);
            newLayer.setMitigationProbability(mit);
            newLayer.setCapacity(cap);
            controller.getEnvironment().addDefense(newLayer);
            refreshNetworkView();
            log("Added " + newLayer.getName() + " [Det: " + String.format("%.1f", det) + " | Mit: " + String.format("%.1f", mit) + " | Cap: " + cap + "]", "INFO");
        }
    }

    private void clearDefenses() {
        controller.getEnvironment().clearDefenses();
        refreshNetworkView();
        log("Defense architecture cleared.", "WARNING");
    }

    // --- CENTER PANEL: NETWORK VIEW ---
    private VBox buildNetworkPanel() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("panel-bg");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("NETWORK TOPOLOGY");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-font-size: 16px;");

        SVGPath netIcon = new SVGPath();
        netIcon.setContent("M16,11V15H13V19H17V23H7V19H11V15H8V11H16M16,1H8V5H11V9H13V5H16V1Z");
        netIcon.setFill(Color.web("#00FF9C"));
        HBox titleBox = new HBox(10, netIcon, title);
        titleBox.setAlignment(Pos.CENTER);

        networkPanel.setAlignment(Pos.CENTER);
        ScrollPane scroll = new ScrollPane(networkPanel);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        panel.getChildren().addAll(titleBox, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return panel;
    }

    private void refreshNetworkView() {
        networkPanel.getChildren().clear();
        componentCards.clear();
        defenseCards.clear();

        for (DefenseLayer d : controller.getEnvironment().getDefenses()) {
            VBox card = createCard(d.getName(), "DEFENSE", "status-safe");
            defenseCards.put(d, card);
            networkPanel.getChildren().add(card);
        }

        for (NetworkComponent c : controller.getEnvironment().getComponents()) {
            String lbl = "SERVER";
            if (c.getZone() == Zone.EDGE) lbl = "EDGE";
            else if (c.getZone() == Zone.INTERNAL) lbl = "INTERNAL";
            else if (c.getZone() == Zone.DATA) lbl = "DATA";
            
            VBox card = createCard(c.getName(), lbl, "status-safe");
            componentCards.put(c, card);
            networkPanel.getChildren().add(card);
        }
    }

    private VBox createCard(String name, String type, String statusClass) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(120, 100);
        card.getStyleClass().addAll("panel-bg", statusClass);

        Label typeLbl = new Label(type);
        typeLbl.setStyle("-fx-text-fill: #1F6FEB; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-wrap-text: true;");
        nameLbl.setAlignment(Pos.CENTER);

        card.getChildren().addAll(typeLbl, iconNode(type), nameLbl);
        return card;
    }

    private SVGPath iconNode(String type) {
        SVGPath path = new SVGPath();
        switch (type) {
            case "DATABASE": path.setContent("M12,3C7.58,3 4,4.79 4,7C4,9.21 7.58,11 12,11C16.42,11 20,9.21 20,7C20,4.79 16.42,3 12,3M4,9V13C4,15.21 7.58,17 12,17C16.42,17 20,15.21 20,13V9C20,11.21 16.42,13 12,13C7.58,13 4,11.21 4,9M4,15V19C4,21.21 7.58,23 12,23C16.42,23 20,21.21 20,19V15C20,17.21 16.42,19 12,19C7.58,19 4,17.21 4,15Z"); break;
            case "SERVER": path.setContent("M4,1H20A1,1 0 0,1 21,2V6A1,1 0 0,1 20,7H4A1,1 0 0,1 3,6V2A1,1 0 0,1 4,1M4,9H20A1,1 0 0,1 21,10V14A1,1 0 0,1 20,15H4A1,1 0 0,1 3,14V10A1,1 0 0,1 4,9M4,17H20A1,1 0 0,1 21,18V22A1,1 0 0,1 20,23H4A1,1 0 0,1 3,22V18A1,1 0 0,1 4,17M9,4H10V5H9V4M9,12H10V13H9V12M9,20H10V21H9V20M5,4H7V5H5V4M5,12H7V13H5V12M5,20H7V21H5V20Z"); break;
            default: path.setContent("M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1Z"); break;
        }
        path.setFill(Color.web("#8B949E"));
        return path;
    }

    private void updateNetworkCards() {
        for (Map.Entry<NetworkComponent, VBox> entry : componentCards.entrySet()) {
            NetworkComponent c = entry.getKey();
            VBox card = entry.getValue();
            card.getStyleClass().removeAll("status-safe", "status-warning", "status-danger");
            
            if (c.isCompromised()) {
                card.getStyleClass().add("status-danger");
            } else if (c.getHealth() < 100) {
                card.getStyleClass().add("status-warning");
            } else {
                card.getStyleClass().add("status-safe");
            }
        }
    }

    // --- RIGHT PANEL: LIVE LOG ---
    private VBox buildLogPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("panel-bg");
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(300);

        Label title = new Label("LIVE LOG CONSOLE");
        title.getStyleClass().add("header-text");
        title.setStyle("-fx-font-size: 16px;");

        liveLogConsole.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    if (item.contains("[CRITICAL]") || item.contains("[DAMAGE]")) {
                        setStyle("-fx-text-fill: #FF4C4C; -fx-background-color: transparent;");
                    } else if (item.contains("[WARNING]") || item.contains("[FAILURE]")) {
                        setStyle("-fx-text-fill: #FFD33D; -fx-background-color: transparent;");
                    } else if (item.contains("[SUCCESS]")) {
                        setStyle("-fx-text-fill: #00FF9C; -fx-background-color: transparent;");
                    } else {
                        setStyle("-fx-text-fill: #00FF9C; -fx-background-color: transparent;"); // Default neon green
                    }
                }
            }
        });

        panel.getChildren().addAll(title, liveLogConsole);
        VBox.setVgrow(liveLogConsole, Priority.ALWAYS);
        return panel;
    }

    private void log(String msg, String level) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formatted = String.format("[%s] %s %s", time, level.isEmpty() ? "" : "["+level+"]", msg);
        Platform.runLater(() -> {
            logItems.add(formatted);
            liveLogConsole.scrollTo(logItems.size() - 1);
        });
    }

    // --- BOTTOM PANEL: METRICS ---
    private HBox buildMetricsBar() {
        HBox bar = new HBox(30);
        bar.setPadding(new Insets(15));
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: #161B22; -fx-border-color: #30363D; -fx-border-width: 1 0 0 0;");

        healthBar.setPrefWidth(200);
        healthBar.setStyle("-fx-accent: #00FF9C;");
        VBox hb = new VBox(5, createPropLabel("SYSTEM HEALTH"), healthBar);
        hb.setAlignment(Pos.CENTER);

        VBox dt = new VBox(5, createPropLabel("DETECTION RATE"), detectionRateLabel);
        dt.setAlignment(Pos.CENTER);
        detectionRateLabel.getStyleClass().add("metric-label");

        VBox mt = new VBox(5, createPropLabel("MITIGATION RATE"), mitigationRateLabel);
        mt.setAlignment(Pos.CENTER);
        mitigationRateLabel.getStyleClass().add("metric-label");

        resilienceScoreLabel.setStyle("-fx-text-fill: #00FF9C; -fx-font-size: 32px; -fx-font-weight: bold;");
        VBox rs = new VBox(5, createPropLabel("RESILIENCE SCORE"), resilienceScoreLabel);
        rs.setAlignment(Pos.CENTER);

        bar.getChildren().addAll(hb, dt, mt, new Region(), rs);
        HBox.setHgrow(bar.getChildren().get(3), Priority.ALWAYS); // Spacer
        return bar;
    }

    private Label createPropLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #8B949E; -fx-font-size: 10px; -fx-font-weight: bold;");
        return l;
    }

    private void startAIStressTest() {
        log("--- [ADAPTIVE AI STRESS TEST INITIATED] ---", "WARNING");
        new Thread(() -> {
            com.cyrex.engine.AdaptiveAttackerAgent agent = new com.cyrex.engine.AdaptiveAttackerAgent(controller, this);
            agent.executeWaveTest(20);
        }).start();
    }

    public void updateLogsFromAgent(String msg, String level) {
        log(msg, level);
    }
    
    public void executeAttackSync(CyberAttack attack) {

        log("Launched: " + attack.getName() + " (Sev: " + String.format("%.1f", attack.getSeverity()) + ", Stl: " + String.format("%.1f", attack.getStealthLevel()) + ")", "INFO");

        SimulationResult res = controller.runSimulation(attack);

        if (res.isAttackDetected()) {
            log(attack.getName() + " detected by defense layers.", "SUCCESS");
        } else {
            log("Defenses bypassed! " + attack.getName() + " went undetected.", "FAILURE");
        }

        if (res.isAttackMitigated()) {
            log(attack.getName() + " successfully mitigated.", "SUCCESS");
        } else {
            log("Mitigation failed. Payload executed.", "FAILURE");
            if (res.getDamageImpact() > 0) {
                log(String.format("Damage sustained! System Health dropped to %.1f%%", res.getSystemHealthPercent()), "DAMAGE");
            }
            if (!res.getCompromisedNodes().isEmpty()) {
                log("Compromised Nodes: " + String.join(", ", res.getCompromisedNodes()), "CRITICAL");
            }
        }

        log(String.format("Resilience Calculated: %.1f", res.getResilienceScore()), "INFO");
        
        updateNetworkCards();
        healthBar.setProgress(res.getSystemHealthPercent() / 100.0);
        if (res.getSystemHealthPercent() < 50) {
            healthBar.setStyle("-fx-accent: #FF4C4C;");
        } else if (res.getSystemHealthPercent() < 80) {
            healthBar.setStyle("-fx-accent: #FFD33D;");
        } else {
            healthBar.setStyle("-fx-accent: #00FF9C;");
        }

        detectionRateLabel.setText(res.isAttackDetected() ? "100%" : "0%");
        mitigationRateLabel.setText(res.isAttackMitigated() ? "100%" : "0%");
        resilienceScoreLabel.setText(String.format("%.1f", res.getResilienceScore()));
    }
}
