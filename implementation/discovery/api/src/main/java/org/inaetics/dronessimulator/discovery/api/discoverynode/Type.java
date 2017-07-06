package org.inaetics.dronessimulator.discovery.api.discoverynode;

/**
 * Identifier for an instance type.
 *
 * This class provides a number of predefined types.
 * Is used with the discovery as the first part of the path
 */
public class Type {
    /** Drone type */
    public static final Type DRONE = new Type("drone");
    /** Service type */
    public static final Type SERVICE = new Type("service");
    /** RabbitMQ type */
    public static final Type RABBITMQ = new Type("rabbitmq");

    /** The string representation of this type. */
    private final String str;

    /**
     * Instantiates a new type with the given string representation.
     * @param str The string representation of this type.
     */
    public Type(String str) {
        this.str = str;
    }

    /**
     * Returns the string representation of this type.
     * @return The string representation of this type.
     */
    public String getStr() {
        return str;
    }
}
