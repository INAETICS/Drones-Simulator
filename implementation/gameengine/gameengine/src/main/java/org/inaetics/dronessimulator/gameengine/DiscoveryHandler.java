package org.inaetics.dronessimulator.gameengine;

import lombok.AllArgsConstructor;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IIdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;

/**
 * Dummy discovery handler to insert test entity
 */
@AllArgsConstructor
public class DiscoveryHandler {
    private final IPhysicsEngineDriver driver;
    private final IIdentifierMapper id_mapper;

    public void newDrone(String protocolId, D3Vector position) {
        int gameengineId = id_mapper.getNewGameEngineId();

        this.driver.addNewEntity(new Drone(gameengineId, Drone.DRONE_MAX_HEALTH, position, new D3Vector(), new D3Vector()), protocolId);
    }
}
