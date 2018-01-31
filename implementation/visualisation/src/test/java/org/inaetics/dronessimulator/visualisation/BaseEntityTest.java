package org.inaetics.dronessimulator.visualisation;

import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.visualisation.uiupdates.RemoveBaseEntity;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BaseEntityTest {
    private BaseEntity baseEntity;
    private BlockingQueue<UIUpdate> uiUpdates;

    @Before
    public void setup() throws Exception {
        uiUpdates = new LinkedBlockingQueue<>();
        String image = "/bullet.png";
        baseEntity = new BaseEntity(uiUpdates, image) {
        };
    }

    @Test
    public void testUpdateUI() throws Exception {
        //Given
        D3Vector position = new D3Vector(150, 100, 500);
        baseEntity.setPosition(position);
        D3PolarCoordinate direction = new D3PolarCoordinate(0.25 * Math.PI, 0.5 * Math.PI, 100);
        baseEntity.setDirection(direction);

        //When
        baseEntity.updateUI();

        //Then
        Assert.assertEquals(150, baseEntity.getSpriteX(), 0.1);
        Assert.assertEquals(100, baseEntity.getSpriteY(), 0.1);
        Assert.assertEquals(150, baseEntity.imageView.getLayoutX(), 0.1);
        Assert.assertEquals(100, baseEntity.imageView.getLayoutY(), 0.1);
        Assert.assertEquals(45, baseEntity.imageView.getRotate(), 0.1);
        Assert.assertEquals(0.55, baseEntity.imageView.getScaleX(), 0.1);
        Assert.assertEquals(0.55, baseEntity.imageView.getScaleY(), 0.1);
    }

    @Test
    public void testDelete() throws Exception {
        int sizeBefore = uiUpdates.size();
        baseEntity.delete();
        int sizeAfter = uiUpdates.size();
        Assert.assertEquals(sizeBefore + 1, sizeAfter);
        Assert.assertTrue(uiUpdates.contains(new RemoveBaseEntity(baseEntity.imageView)));
    }

}