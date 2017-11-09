package org.inaetics.dronessimulator.gameengine.messagehandlers;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Log4j
public class KillMessageHandler implements MessageHandler {
    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    private final Discoverer discoverer;

    @Override
    public void handleMessage(Message message) {
        // Kill the entity
        KillMessage killMessage = (KillMessage) message;
        physicsEngineDriver.removeEntity(killMessage.getIdentifier());
        if (EntityType.DRONE.equals(killMessage.getEntityType())) {
            try {
                HashMap<String, String> props = new HashMap<>();
                props.put("state", "killed");
                discoverer.updateProperties(new DroneInstance(killMessage.getIdentifier()), props);
            } catch (IOException e) {
                log.error(e);
            }
        }
        log.info("Received killmessage for " + killMessage.getIdentifier() + " " + killMessage.getEntityType());
    }
}
