package org.inaetics.dronessimulator.gameengine;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.common.Settings;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.common.vector.D2Vector;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
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
 * Set up are: physics engine, incoming command messages, queue between physics engine and rule processors,
 * discovery handler and architecture state event listening.
 */
public class GameEngine {
    /**
     * The logger
     */
    private final static Logger logger = Logger.getLogger(GameEngine.class);

    /** Physics engine used in the game engine. */
    private volatile IPhysicsEngineDriver m_physicsEngineDriver;

    /** Manager of state outside of physicsengine. */
    private volatile IGameStateManager m_stateManager;

    /** Rule processors to handle any outgoing messages. The last rule processor SendMessages sends all messages off. */
    private volatile IRuleProcessors m_ruleProcessors;

    /** The subscriber to use. */
    private volatile Subscriber m_subscriber;

    /** The identifier mapper to use. */
    private volatile IdentifierMapper m_id_mapper;

    /** The discoverer to use. */
    private volatile Discoverer m_discoverer;

    private volatile ArchitectureEventController m_architectureEventListener;

    private List<String> lobbiedDrones = new ArrayList<>();

    /** Concrete message handlers. */
    private CollisionMessageHandler collisionMessageHandler;
    private DamageMessageHandler damageMessageHandler;
    private FireBulletMessageHandler fireBulletMessageHandler;
    private KillMessageHandler killMessageHandler;
    private MovementMessageHandler movementMessageHandler;
    private StateMessageHandler stateMessageHandler;

    /** The game engine instance to register. */
    private Instance discoveryInstance;

    /**
     * Starts the wrapper. Sets up all handlers, queues and engines. Connects everything if needed.
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
            logger.fatal("Could not subscribe to topic " + MessageTopic.MOVEMENTS + ".", e);
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

                logger.info("Added new drone " + node.getId() + " in simulation/lobby");
            }

        });

        List<NodeEventHandler<RemovedNode>> removeHandlers = new ArrayList<>();

        removeHandlers.add((RemovedNode removedNodeEvent) -> {
            DiscoveryNode node = removedNodeEvent.getNode();
            DiscoveryPath path = node.getPath();

            if( path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();

                lobbiedDrones.remove(protocolId);
                this.m_physicsEngineDriver.removeEntity(protocolId);
                Logger.getLogger(GameEngine.class).info("Removed drone " + protocolId + " from simulation");
            }
        });

        m_discoverer.addHandlers(true, addHandlers, Collections.emptyList(), removeHandlers);

        // Setup Architecture Event listeners!
        m_architectureEventListener.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING, (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            logger.info("Adding " + lobbiedDrones.size() + " drones to simulation");
            int dronesInLobby = lobbiedDrones.size();
            D2Vector center = new D2Vector(Settings.ARENA_WIDTH / 2, Settings.ARENA_DEPTH / 2);
            double spawnRadius = (Math.min(Settings.ARENA_DEPTH, Settings.ARENA_WIDTH) / 2) * 0.9;
            double spawnAngle = (2 * Math.PI) / dronesInLobby;

            int numberSpawned = 0;
            for(String protocolId : lobbiedDrones) {
                int gameengineId = m_id_mapper.getNewGameEngineId();
                D3Vector position = new D3Vector( Math.cos(spawnAngle * numberSpawned) * spawnRadius + center.getX()
                                                , Math.sin(spawnAngle * numberSpawned) * spawnRadius + center.getY()
                                                , 50);
                numberSpawned++;

                this.m_physicsEngineDriver.addNewEntity(new Drone(gameengineId, Drone.DRONE_MAX_HEALTH, position, new D3Vector(), new D3Vector(), new D3PolarCoordinate()), protocolId);
                logger.info("Added new drone " + protocolId + " as " + gameengineId);
            }
        });

        logger.info("Started Game Engine!");
    }

    /**
     * Stops the wrapper. Kills the engine and rule processor threads.
     */
    public void stop() {
        try {
            this.m_discoverer.unregister(discoveryInstance);
        } catch (IOException e) {
            logger.fatal(e);
        }
        logger.info("Stopped Game Engine!");
    }
}
