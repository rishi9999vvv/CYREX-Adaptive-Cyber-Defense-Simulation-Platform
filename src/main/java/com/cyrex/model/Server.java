package com.cyrex.model;

/**
 * Concrete network component: a generic server (OOP: inheritance from NetworkComponent).
 */
public class Server extends NetworkComponent {
    public Server(String name, double securityLevel) {
        super(name, securityLevel, Zone.EDGE);
    }

    public Server(String name) {
        this(name, 5.0);
    }
}
