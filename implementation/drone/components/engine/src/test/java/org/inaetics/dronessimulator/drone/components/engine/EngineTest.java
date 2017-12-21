package org.inaetics.dronessimulator.drone.components.engine;


import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EngineTest {
    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
    private Engine engine;
    private MockPublisher publisher;
    private DroneInit drone;
    private GPS gps;
    private D3Vector current_velocity;
    private D3Vector current_acceleration;

    @Before
    public void setup() {
        publisher = new MockPublisher();
        drone = mock(DroneInit.class);
        when(drone.getIdentifier()).thenReturn("drone_id");
        gps = mock(GPS.class);
        current_velocity = new D3Vector(19, 0, 0);
        current_acceleration = new D3Vector(1, 1, 1);
        when(gps.getVelocity()).thenReturn(current_velocity);
        when(gps.getAcceleration()).thenReturn(current_acceleration);
        engine = new Engine(publisher, gps, drone, null);
        environmentVariables.set("MAX_DRONE_ACCELERATION", "10");
        environmentVariables.set("MAX_DRONE_VELOCITY", "20");
    }

    @Test
    public void limit_acceleration() throws Exception {
        //nul-vectors should be untouched
        Assert.assertEquals(new D3Vector(0, 0, 0), Engine.limit_acceleration(new D3Vector(0, 0, 0)));

        //Permitted acceleration should be passed
        Assert.assertEquals(new D3Vector(5, 5, 5), Engine.limit_acceleration(new D3Vector(5, 5, 5)));
        Assert.assertEquals(new D3Vector(-5, -5, -5), Engine.limit_acceleration(new D3Vector(-5, -5, -5)));
        Assert.assertEquals(new D3Vector(-5, 5, -5), Engine.limit_acceleration(new D3Vector(-5, 5, -5)));
        Assert.assertEquals(new D3Vector(-5, 5, 5), Engine.limit_acceleration(new D3Vector(-5, 5, 5)));

        //Excessive acceleration should be limited
        Assert.assertEquals(new D3Vector(10, 0, 0), Engine.limit_acceleration(new D3Vector(1000, 0, 0)));
        Assert.assertEquals(new D3Vector(-10, 0, 0), Engine.limit_acceleration(new D3Vector(-1000, 0, 0)));
    }

    @Test
    public void maximize_acceleration() throws Exception {
        //nul-vectors should be untouched
        Assert.assertEquals(new D3Vector(0, 0, 0), Engine.maximize_acceleration(new D3Vector(0, 0, 0)));

        //Less than max accelaration should be upgraded to the max
        Assert.assertEquals(new D3Vector(5.77350269189625764509148780501957, 5.77350269189625764509148780501957, 5.77350269189625764509148780501957), Engine.maximize_acceleration(new D3Vector(5, 5, 5)));

        //More than max acceleration should be kept the same
        Assert.assertEquals(new D3Vector(15, 15, 15), Engine.maximize_acceleration(new D3Vector(15, 15, 15)));

    }

    @Test
    @Ignore
    //TODO Lookup how this function is used and create a better test based on the spec, not the current implementation and documentation.
    public void stagnate_acceleration() throws Exception {
        Assert.assertEquals("We should keep the same acceleration if we do not accelerate", new D3Vector(0, 0, 0), engine.stagnate_acceleration(new D3Vector(0, 0, 0)));
        Assert.assertEquals("When we exceed the 90% mark of velocity, then we should limit the accelaration to 75% of the original acceleration.", current_acceleration.scale(0.25), engine.stagnate_acceleration(new D3Vector(1, 1, 1)));
    }

    @Test
    @Ignore
    //TODO Lookup how this function is used and create a better test based on the spec, not the current implementation and documentation.
    public void changeAcceleration() throws Exception {
        //Small acceleration should be possible
        engine.changeAcceleration(new D3Vector(1, 1, 1));
        MovementMessage msg = new MovementMessage();
        msg.setAcceleration(new D3Vector(1, 1, 1));
        msg.setIdentifier("drone_id");
        Assert.assertTrue("The message is not found. These messages were found: " + publisher.getReceivedMessages(), publisher.getReceivedMessages().size() == 1);
        //Large acceleration should be limited

        //The drone should never fly faster than its max velocity
        //The drone should never accelerate faster than its max acceleration
    }

    @Test
    public void testChangeAcceleration() {
        gps.setAcceleration(D3Vector.fromString("(x:-3.4427316345680223, y:-9.363483685842366, z:-0.6875842910570499)"));
        gps.setPosition(D3Vector.fromString("(x:366.30329775852334, y:183.53828228906738, z:53.59830231645528)"));
        gps.setVelocity(D3Vector.fromString("(x:-10.047801568808978, y:-17.60877350319855, z:0.9541791137955318)"));

//        D3Vector in = D3Vector.fromString("(x:-23.613400167909106, y:172.40476530548727, z:95.8106379471248)");
//        engine.changeAcceleration(in);
        engine.changeAcceleration(new D3Vector());
    }

}