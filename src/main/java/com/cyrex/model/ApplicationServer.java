package com.cyrex.model;

/**
 * Concrete network component: application logic server (OOP: inheritance).
 * Resides in the INTERNAL zone.
 */
public class ApplicationServer extends NetworkComponent {
    public ApplicationServer(String name, double securityLevel) {
        super(name, securityLevel, Zone.INTERNAL);
    }

    public ApplicationServer(String name) {
        this(name, 5.0);
    }
}
