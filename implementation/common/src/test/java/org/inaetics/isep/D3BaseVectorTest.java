package org.inaetics.isep;

import org.junit.Assert;
import org.junit.Test;

public class D3BaseVectorTest {
    @Test
    public void intersectionTest() {
        D3BaseVector bv1;
        D3BaseVector bv2;
        D3Vector i;

        bv1 = new D3BaseVector(1,1,0,0,0,0);
        bv2 = new D3BaseVector(-1,1,0,0,0,0);
        i = bv1.intersection(bv2);

        Assert.assertEquals("x", 0, i.getX(), 0.01);
        Assert.assertEquals("y", 0, i.getY(), 0.01);
        Assert.assertEquals("z", 0, i.getZ(), 0.01);


        bv1 = new D3BaseVector(1,1,0,10,20,30);
        bv2 = new D3BaseVector(-1,1,0,10,20,30);
        i = bv1.intersection(bv2);

        Assert.assertEquals("x", 10, i.getX(), 0.01);
        Assert.assertEquals("y", 20, i.getY(), 0.01);
        Assert.assertEquals("z", 30, i.getZ(), 0.01);


        bv1 = new D3BaseVector(1,2,3,20,30,15);
        bv2 = new D3BaseVector(3,6,-5,24,38,55);
        i = bv1.intersection(bv2);

        Assert.assertEquals("x", 30, i.getX(), 0.01);
        Assert.assertEquals("y", 50, i.getY(), 0.01);
        Assert.assertEquals("z", 45, i.getZ(), 0.01);

        bv1 = new D3BaseVector(1,2,5,20,30,15);
        bv2 = new D3BaseVector(1,2,5,20,30,55);
        i = bv1.intersection(bv2);

        Assert.assertNull(i);
    }

}
