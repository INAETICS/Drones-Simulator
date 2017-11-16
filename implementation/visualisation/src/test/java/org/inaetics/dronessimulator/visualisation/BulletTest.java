package org.inaetics.dronessimulator.visualisation;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

public class BulletTest {

    @Test
    public void testConstructor() {
        Bullet bullet = new Bullet(new LinkedBlockingQueue<>());
        Assert.assertEquals(Settings.BULLET_HEIGHT, bullet.imageView.getFitHeight(), 0.1);
        Assert.assertEquals(1280, bullet.imageView.getImage().getHeight(), 0.1);
        Assert.assertEquals(1280, bullet.imageView.getImage().getWidth(), 0.1);
    }

}