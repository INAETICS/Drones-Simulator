package org.inaetics.dronessimulator.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 2-tuple with arbitrary types.
 *
 * @param <Left>  Type of the first value.
 * @param <Right> Type of the second value.
 */
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class Tuple<Left, Right> {
    /**
     * The first value.
     */
    @Getter
    private final Left left;

    /**
     * The second value.
     */
    @Getter
    private final Right right;
}
