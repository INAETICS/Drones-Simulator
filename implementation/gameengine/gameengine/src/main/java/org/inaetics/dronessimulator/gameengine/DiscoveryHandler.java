package org.inaetics.dronessimulator.gameengine;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;

/**
 * Dummy discovery handler to insert test entity
 */
@AllArgsConstructor
public class DiscoveryHandler {
    private final IPhysicsEngineDriver driver;

    public void newDrone(int id, D3Vector position) {
        this.driver.addNewEntity(new Drone(1, 100, position, new D3Vector(), new D3Vector()));
    }
}
