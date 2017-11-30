package org.inaetics.dronessimulator.test.concurrent;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.inaetics.dronessimulator.common.vector.D3Vector;

public class D3VectorMatcher extends TypeSafeMatcher<D3Vector> {
    private final double delta;
    private final D3Vector value;

    public D3VectorMatcher(D3Vector value, double error) {
        this.delta = error;
        this.value = value;
    }

    @Factory
    public static Matcher<D3Vector> closeTo(D3Vector operand, double error) {
        return new D3VectorMatcher(operand, error);
    }

    public boolean matchesSafely(D3Vector item) {
        return this.actualDelta(item) <= 0.0D;
    }

    public void describeMismatchSafely(D3Vector item, Description mismatchDescription) {
        mismatchDescription.appendValue(item).appendText(" differed by ").appendValue(this.actualDelta(item));
    }

    public void describeTo(Description description) {
        description.appendText("a D3Vector value within ").appendValue(this.delta).appendText(" of ").appendValue(this
                .value);
    }

    private double actualDelta(D3Vector item) {
        return Math.abs(item.sub(this.value).length()) - this.delta;
    }
}
