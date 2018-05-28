package org.inaetics.dronessimulator.gameengine.core.messagehandlers;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.io.IOException;
import java.util.HashMap;

public class KillMessageHandler implements MessageHandler<KillMessage> {
    public KillMessageHandler(IPhysicsEngineDriver physicsEngineDriver, IdentifierMapper id_mapper, IGameStateManager stateManager, Discoverer discoverer) {
        this.physicsEngineDriver = physicsEngineDriver;
        this.id_mapper = id_mapper;
        this.stateManager = stateManager;
        this.discoverer = discoverer;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(KillMessageHandler.class);

    /** The physics engine to update entities in. */
    private final IPhysicsEngineDriver physicsEngineDriver;

    /** The mapping between protocol and physics engine ids. */
    private final IdentifierMapper id_mapper;

    /** The game state manager for the entities. */
    private final IGameStateManager stateManager;

    private final Discoverer discoverer;

    @Override
    public void handleMessage(KillMessage killMessage) {
        // Kill the entity
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
