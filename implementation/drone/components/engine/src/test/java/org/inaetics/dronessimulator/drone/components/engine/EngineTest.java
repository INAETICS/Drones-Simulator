package org.inaetics.dronessimulator.drone.components.engine;


import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EngineTest {
    private Engine engine;

    @Before
    public void setup() {
        engine = new Engine();
    }

    @Test
    public void limit_acceleration() throws Exception {
        //nul-vectors should be untouched
        Assert.assertEquals(new D3Vector(0, 0, 0), engine.limit_acceleration(new D3Vector(0, 0, 0)));

        //Permitted acceleration should be passed
        Assert.assertEquals(new D3Vector(5, 5, 5), engine.limit_acceleration(new D3Vector(5, 5, 5)));

        //Excessive acceleration should be limited
        Assert.assertEquals(new D3Vector(10, 0, 0), engine.limit_acceleration(new D3Vector(1000, 0, 0)));
    }

    @Test
    public void maximize_acceleration() throws Exception {
        //nul-vectors should be untouched
        Assert.assertEquals(new D3Vector(0, 0, 0), engine.maximize_acceleration(new D3Vector(0, 0, 0)));

        //Less than max accelaration should be upgraded to the max
        Assert.assertEquals(new D3Vector(5.77350269189625764509148780501957, 5.77350269189625764509148780501957, 5.77350269189625764509148780501957), engine.maximize_acceleration(new D3Vector(5, 5, 5)));

        //More than max acceleration should be kept the same
        Assert.assertEquals(new D3Vector(15, 15, 15), engine.maximize_acceleration(new D3Vector(15, 15, 15)));

    }

    @Test
    public void stagnate_acceleration() throws Exception {
        Assert.fail("Not yet implemented");
    }

    @Test
    public void changeAcceleration() throws Exception {
        Assert.fail("Not yet implemented (continue here: https://sonarcloud.io/component_measures?id=org.inaetics%3Adronessimulator&metric=coverage&selected=org.inaetics.dronessimulator.drone.components%3Acomponents-engine%3Asrc%2Fjava%2Forg%2Finaetics%2Fdronessimulator%2Fdrone%2Fcomponents%2Fengine%2FEngine.java)");
    }

}