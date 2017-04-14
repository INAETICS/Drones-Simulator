package org.inaetics.dronessimulator.discovery.api;

/**
 * Exception for when an instance is registered while an instance with the same name already exists.
 */
public class DuplicateName extends Exception {
    public DuplicateName(Throwable t) {
        super(t);
    }
}
