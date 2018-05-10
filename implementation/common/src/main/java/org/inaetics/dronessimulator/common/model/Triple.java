package org.inaetics.dronessimulator.common.model;

import java.util.Objects;

/**
 * 3-tuple with arbitrary types.
 *
 * @param <A> Type of the first value.
 * @param <B> Type of the second value.
 * @param <C> Type of the third value.
 */
public class Triple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(a, triple.a) &&
                Objects.equals(b, triple.b) &&
                Objects.equals(c, triple.c);
    }

    @Override
    public int hashCode() {

        return Objects.hash(a, b, c);
    }

    @Override
    public String toString() {
        return "Triple{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
