package org.inaetics.dronessimulator.common;

import java.util.Objects;

/**
 * 2-tuple with arbitrary types.
 *
 * @param <Left>  Type of the first value.
 * @param <Right> Type of the second value.
 */

public class Tuple<Left, Right> {

    public Tuple(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    /**
     * The first value.
     */
    private final Left left;

    public Left getLeft() {
        return left;
    }

    /**
     * The second value.
     */
    private final Right right;

    public Right getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(left, tuple.left) &&
                Objects.equals(right, tuple.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
