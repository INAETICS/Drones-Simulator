package org.inaetics.dronessimulator.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 3-tuple with arbitrary types.
 *
 * @param <A> Type of the first value.
 * @param <B> Type of the second value.
 * @param <C> Type of the third value.
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@Getter
public class Triple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;
}
