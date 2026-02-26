package com.cyrex.model;

/**
 * Concrete network component: database server (OOP: inheritance).
 * Typically more critical and may have different baseline security.
 */
public class DatabaseServer extends NetworkComponent {
    public DatabaseServer(String name, double securityLevel) {
        super(name, securityLevel, Zone.DATA);
    }

    public DatabaseServer(String name) {
        this(name, 6.0);
    }
}
