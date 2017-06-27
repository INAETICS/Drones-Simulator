package org.inaetics.dronessimulator.gameengine;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.D2Vector;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.messagehandlers.*;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.IRuleProcessors;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Wrapper around PhysicsEngine. Sets up and connects all handlers with each other.
 * Set up are: physicsengine, incoming command messages, queue between physicsengine and ruleprocessors,
 * discovery handler
 * and ruleprocessors
 */
public class GameEngine {
    public static final float arenaWidth = 1000;
    public static final float arenaDepth = 1000;
    /**
     * Physicsengine which is used in game engine
     */
    private volatile IPhysicsEngineDriver m_physicsEngineDriver;

    /**
     * Manager of state outside of physicsengine
     */
    private volatile IGameStateManager m_stateManager;
    /**
     * Ruleprocessors to handle any outgoing messages. Last ruleprocessor SendMessages send all messages off
     */
    private volatile IRuleProcessors m_ruleProcessors;

    private volatile Subscriber m_subscriber;

    private volatile IdentifierMapper m_id_mapper;

    private volatile Discoverer m_discoverer;

    private volatile ArchitectureEventController m_architectureEventListener;

    private List<String> lobbiedDrones = new ArrayList<>();

    /**
     * Message handler to handle incoming commands from drones
     */
    private CollisionMessageHandler collisionMessageHandler;
    private DamageMessageHandler damageMessageHandler;
    private FireBulletMessageHandler fireBulletMessageHandler;
    private KillMessageHandler killMessageHandler;
    private MovementMessageHandler movementMessageHandler;
    private StateMessageHandler stateMessageHandler;

    private Instance discoveryInstance;

    /**
     * Start the wrapper. Setup all handlers, queues and engines. Connects everything if needed.
     */
    public void start() throws DuplicateName, IOException {
        Logger.getLogger(GameEngine.class).info("Starting Game Engine...");
        this.collisionMessageHandler = new CollisionMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.damageMessageHandler = new DamageMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.fireBulletMessageHandler = new FireBulletMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.killMessageHandler = new KillMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.movementMessageHandler = new MovementMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.stateMessageHandler = new StateMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);

        // Setup subscriber
        try {
            this.m_subscriber.addTopic(MessageTopic.MOVEMENTS);
            this.m_subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch(IOException e) {
            Logger.getLogger(GameEngine.class).fatal("Could not subscribe to topic " + MessageTopic.MOVEMENTS + ".");
        }

        this.m_subscriber.addHandler(CollisionMessage.class, this.collisionMessageHandler);
        this.m_subscriber.addHandler(DamageMessage.class, this.damageMessageHandler);
        this.m_subscriber.addHandler(FireBulletMessage.class, this.fireBulletMessageHandler);
        this.m_subscriber.addHandler(KillMessage.class, this.killMessageHandler);
        this.m_subscriber.addHandler(MovementMessage.class, this.movementMessageHandler);
        this.m_subscriber.addHandler(StateMessage.class, this.stateMessageHandler);


        // Setup discoverer
        discoveryInstance = new Instance(Type.SERVICE, Group.SERVICES, "gameengine" , new HashMap<>());
        m_discoverer.register(discoveryInstance);

        List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();

        addHandlers.add((AddedNode addedNodeEvent) -> {
            DiscoveryNode node = addedNodeEvent.getNode();
            DiscoveryPath path = node.getPath();

            if( path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                lobbiedDrones.add(node.getId());

                Logger.getLogger(GameEngine.class).info("Added new drone in lobby " + node.getId());
            }

        });

        List<NodeEventHandler<RemovedNode>> removeHandlers = new ArrayList<>();

        removeHandlers.add((RemovedNode removedNodeEvent) -> {
            DiscoveryNode node = removedNodeEvent.getNode();
            DiscoveryPath path = node.getPath();

            if( path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();

                this.m_physicsEngineDriver.removeEntity(protocolId);
                Logger.getLogger(GameEngine.class).info("Removed drone " + protocolId);
            }
        });


        m_discoverer.addHandlers(true, addHandlers, Collections.emptyList(), removeHandlers);

        // Setup Architecture Event listeners!
        m_architectureEventListener.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING, (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            int dronesInLobby = lobbiedDrones.size();
            D2Vector center = new D2Vector(arenaWidth / 2, arenaDepth / 2);
            float spawnRadius = Math.min(arenaDepth, arenaWidth) / 2;
            double spawnAngle = (2 * Math.PI) / dronesInLobby;

            int numberSpawned = 0;
            for(String protocolId : lobbiedDrones) {
                int gameengineId = m_id_mapper.getNewGameEngineId();
                D3Vector position = new D3Vector( Math.cos(spawnAngle * numberSpawned) * spawnRadius + center.getX()
                                                , Math.sin(spawnAngle * numberSpawned) * spawnRadius + center.getY()
                                                , 50);
                numberSpawned++;

                this.m_physicsEngineDriver.addNewEntity(new Drone(gameengineId, Drone.DRONE_MAX_HEALTH, position, new D3Vector(), new D3Vector()), protocolId);
                Logger.getLogger(GameEngine.class).info("Added new drone " + protocolId + " as " + gameengineId);
            }

            // Clear lobby for next start
            lobbiedDrones = new ArrayList<>();
        });

        Logger.getLogger(GameEngine.class).info("Started Game Engine!");
    }

    /**
     * Stops the wrapper. Kills the engine and ruleprocessor threads
     * @throws Exception - Any exception which might happen during shutting down the wrapper
     */
    public void stop() throws Exception {
        this.m_discoverer.unregister(discoveryInstance);
        Logger.getLogger(GameEngine.class).info("Stopped Game Engine!");
    }
}
