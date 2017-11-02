package org.inaetics.dronessimulator.physicsengine.entityupdate;

import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.physicsengine.Entity;
import org.inaetics.dronessimulator.physicsengine.Size;
import org.junit.Assert;
import org.junit.Test;

public class DirectionEntityUpdateTest {
    @Test
    public void testUpdate() throws Exception {
        //Given
        Entity entity = new Entity(1, new Size(16, 16, 16));
        D3PolarCoordinate newDirection = new D3PolarCoordinate(45, 45, 10);
        DirectionEntityUpdate update = new DirectionEntityUpdate(newDirection);

        //When
        update.update(entity);

        //Then
        Assert.assertEquals(newDirection, entity.getDirection());
    }

}