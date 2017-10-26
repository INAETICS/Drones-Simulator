package org.inaetics.dronessimulator.common;

import lombok.EqualsAndHashCode;

/**
 * 2-tuple with arbitrary types.
 * @param <Left> Type of the first value.
 * @param <Right> Type of the second value.
 */
@EqualsAndHashCode
public class Tuple<Left, Right> {
    /** The first value. */
    private final Left left;

    /** The second value. */
    private final Right right;

    /**
     * Instantiates a new tuple with the given values.
     * @param left The first value.
     * @param right The second value.
     */
    public Tuple(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Returns the first value in this tuple.
     * @return The first value.
     */
    public Left getLeft() {
        return this.left;
    }

    /**
     * Returns the second value in this tuple.
     * @return The second value.
     */
    public Right getRight() {
        return this.right;
    }
}
