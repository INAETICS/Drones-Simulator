package org.inaetics.dronessimulator.common.vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class D2VectorTest {
    private D2Vector vector1;
    private D2Vector vector2;
    private D2Vector vector3;

    @Before
    public void setUp() throws Exception {
        vector1 = new D2Vector(2, 3);
        vector2 = new D2Vector(3, 4);
        vector3 = new D2Vector(4, 5);
    }

    @Test
    public void add() throws Exception {
        Assert.assertEquals(new D2Vector(5, 7), vector1.add(vector2));
    }

    @Test
    public void sub() throws Exception {
        Assert.assertEquals(new D2Vector(1, 1), vector3.sub(vector2));
    }

    @Test
    public void length() throws Exception {
        Assert.assertEquals(5, vector2.length(), 0.1);
    }

    @Test
    public void scale() throws Exception {
        Assert.assertEquals(new D2Vector(4, 6), vector1.scale(2));
    }
}