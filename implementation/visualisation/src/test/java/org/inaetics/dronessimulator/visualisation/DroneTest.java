package org.inaetics.dronessimulator.visualisation;

import javafx.scene.text.Text;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.visualisation.uiupdates.Explosion;
import org.inaetics.dronessimulator.visualisation.uiupdates.RemoveDrone;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class DroneTest {
    private BlockingQueue<UIUpdate> uiUpdates;
    private Drone drone;
    private int hp;
    private double scale;

    @Before
    public void setup() throws Exception {
        uiUpdates = new LinkedBlockingQueue<>();
        String image = "/drones/drone_sprite.png";
        drone = new Drone(uiUpdates, image) {
        };
        hp = 56;
        scale = 0.55;
        D3Vector position = new D3Vector(150, 100, 500);
        drone.setPosition(position);
        D3PolarCoordinate direction = new D3PolarCoordinate(0.25 * Math.PI, 0.5 * Math.PI, 100);
        drone.setDirection(direction);
    }

    @Test
    public void testUpdateUI() throws Exception {
        //Given
        drone.setCurrentHP(hp);

        //When
        drone.updateUI();

        //Then
        Assert.assertEquals("HP: 56/100 Height: 500", getText(drone).getText());
        Assert.assertEquals(114.8 - getText(drone).getLayoutBounds().getMinX(), getText(drone).getLayoutX(), 0.1);
        Assert.assertEquals(44.8 - getText(drone).getLayoutBounds().getMinY(), getText(drone).getLayoutY(), 0.1);
    }

    @Test
    public void testDelete() throws Exception {
        //Given
        int sizeBefore = uiUpdates.size();
        //When
        drone.delete();

        //Then
        int sizeAfter = uiUpdates.size();
        Assert.assertEquals(sizeBefore + 2, sizeAfter);
        Assert.assertThat(uiUpdates, hasItem(new Explosion(scale, drone.imageView)));
        Assert.assertThat(uiUpdates, hasItem(new RemoveDrone(drone.imageView, getText(drone))));

    }

    private Text getText(Drone drone) throws NoSuchFieldException, IllegalAccessException {
        Field field = Drone.class.getDeclaredField("heightText");
        field.setAccessible(true);
        return (Text) field.get(drone);
    }

}