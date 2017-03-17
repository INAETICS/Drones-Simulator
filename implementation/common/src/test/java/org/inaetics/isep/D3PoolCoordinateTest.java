package org.inaetics.isep;

import org.junit.Assert;
import org.junit.Test;


public class D3PoolCoordinateTest {
    @Test
    public void testToVector() {
        // Based on D3Vector.toPoolCoordinate test but with a pass over toVector
        D3Vector v_zero_zero_zero = new D3Vector(0,0,0);
        D3Vector v_zero_zero_pos = new D3Vector(0,0,1);
        D3Vector v_zero_zero_neg = new D3Vector(0,0,-1);
        D3Vector v_pos_zero_zero = new D3Vector(1,0,0);
        D3Vector v_neg_zero_zero = new D3Vector(-1,0,0);
        D3Vector v_zero_pos_zero = new D3Vector(0,1,0);
        D3Vector v_zero_neg_zero = new D3Vector(0,-1,0);
        D3Vector v_pos_pos_zero = new D3Vector(1,1,0);
        D3Vector v_pos_zero_pos = new D3Vector(1,0, 1);
        D3Vector v_zero_pos_pos = new D3Vector(0,1,1);
        D3Vector v_pos_neg_zero = new D3Vector(1,-1,0);
        D3Vector v_pos_zero_neg = new D3Vector(1,0,-1);
        D3Vector v_zero_pos_neg = new D3Vector(0,1,-1);
        D3Vector v_neg_pos_zero = new D3Vector(-1,1,0);
        D3Vector v_neg_zero_pos = new D3Vector(-1,0,1);
        D3Vector v_zero_neg_pos = new D3Vector(0,-1,1);
        D3Vector v_neg_neg_zero = new D3Vector(-1,-1,0);
        D3Vector v_neg_zero_neg = new D3Vector(-1,0,-1);
        D3Vector v_zero_neg_neg = new D3Vector(0,-1,-1);
        D3Vector v_pos_pos_pos = new D3Vector(1,1,1);
        D3Vector v_pos_pos_neg = new D3Vector(1,1,-1);
        D3Vector v_pos_neg_pos = new D3Vector(1,-1,1);
        D3Vector v_neg_pos_pos = new D3Vector(-1,1,1);
        D3Vector v_neg_neg_neg = new D3Vector(-1,-1,-1);
        D3Vector v_neg_neg_pos = new D3Vector(-1,-1,1);
        D3Vector v_neg_pos_neg = new D3Vector(-1,1,-1);
        D3Vector v_pos_neg_neg = new D3Vector(1,-1,-1);

        D3PoolCoordinate p;

        // For zero,zero,zero
        p = v_zero_zero_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_zero_zero.length(), p.getLength(), 0.01);


        // For pos
        p = v_pos_zero_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_zero_zero.length(), p.getLength(), 0.01);

        p = v_zero_pos_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.5 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_pos_zero.length(), p.getLength(), 0.01);

        p = v_zero_zero_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0.5 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_zero_pos.length(), p.getLength(), 0.01);

        // For neg
        p = v_neg_zero_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_zero_zero.length(), p.getLength(), 0.01);

        p = v_zero_neg_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.5 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_neg_zero.length(), p.getLength(), 0.01);

        p = v_zero_zero_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", -0.5 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_zero_neg.length(), p.getLength(), 0.01);


        // For pos,pos
        p = v_zero_pos_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.5 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_pos_pos.length(), p.getLength(), 0.01);

        p = v_pos_zero_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_zero_pos.length(), p.getLength(), 0.01);

        p = v_pos_pos_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.25 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_pos_zero.length(), p.getLength(), 0.01);

        // For neg, pos
        p = v_zero_neg_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.5 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_neg_pos.length(), p.getLength(), 0.01);

        p = v_neg_zero_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_zero_pos.length(), p.getLength(), 0.01);

        p = v_neg_pos_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.75 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_pos_zero.length(), p.getLength(), 0.01);

        // For pos, neg
        p = v_zero_pos_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.5 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", -0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_pos_neg.length(), p.getLength(), 0.01);

        p = v_pos_zero_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0, p.getAngle1(), 0.01);
        Assert.assertEquals("a2", -0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_zero_neg.length(), p.getLength(), 0.01);

        p = v_pos_neg_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.75 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_neg_zero.length(), p.getLength(), 0.01);

        // For neg, neg
        p = v_zero_neg_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.5 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", -0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_zero_neg_neg.length(), p.getLength(), 0.01);

        p = v_neg_zero_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", Math.PI, p.getAngle1(), 0.01);
        Assert.assertEquals("a2", -0.25 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_zero_neg.length(), p.getLength(), 0.01);

        p = v_neg_neg_zero.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.25 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", 0 * Math.PI ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_neg_zero.length(), p.getLength(), 0.01);


        // For pos, pos, pos
        p = v_pos_pos_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.25 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_pos_pos.length(), p.getLength(), 0.01);

        // For pos, pos, neg
        p = v_pos_pos_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.25 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(-1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_pos_neg.length(), p.getLength(), 0.01);

        p = v_pos_neg_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.75 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_neg_pos.length(), p.getLength(), 0.01);

        p = v_neg_pos_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.75 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_pos_pos.length(), p.getLength(), 0.01);

        // For neg, neg, neg
        p = v_neg_neg_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.25 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(-1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_neg_neg.length(), p.getLength(), 0.01);

        // For neg, neg, pos
        p = v_neg_neg_pos.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.25 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_neg_pos.length(), p.getLength(), 0.01);

        p = v_neg_pos_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 0.75 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(-1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_neg_pos_neg.length(), p.getLength(), 0.01);

        p = v_pos_neg_neg.toPoolCoordinate().toVector().toPoolCoordinate();
        Assert.assertEquals("a1", 1.75 * Math.PI ,p.getAngle1(), 0.01);
        Assert.assertEquals("a2", Math.atan(-1/Math.sqrt(2)) ,p.getAngle2(), 0.01);
        Assert.assertEquals("l", v_pos_neg_neg.length(), p.getLength(), 0.01);
    }
}
