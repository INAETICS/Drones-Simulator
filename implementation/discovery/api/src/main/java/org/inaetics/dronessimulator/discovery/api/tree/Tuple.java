package org.inaetics.dronessimulator.discovery.api.tree;

/**
 * Generic tuple consisting of 2 values
 * @param <T1> Type of value 1
 * @param <T2> Type of value 2
 */
public class Tuple<T1, T2> {
    /**
     * The first value
     */
    private final T1 t1;

    /**
     * The second value
     */
    private final T2 t2;

    /**
     * Construct a tuple of values
     * @param t1 The first value
     * @param t2 The second value
     */
    public Tuple(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T2 getT2() {
        return t2;
    }

    public T1 getT1() {
        return t1;
    }
}
