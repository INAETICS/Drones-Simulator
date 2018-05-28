package org.inaetics.dronessimulator.gameengine.core;

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
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;
import org.inaetics.dronessimulator.discovery.api.instances.GameEngineInstance;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.core.messagehandlers.*;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.IRuleProcessors;
import org.inaetics.pubsub.api.pubsub.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper around PhysicsEngine. Sets up and connects all handlers with each other.
 * Set up are: physics engine, incoming command messages, queue between physics engine and rule processors,
 * discovery handler and architecture state event listening.
 */
public class GameEngine implements Subscriber {
    /**
     * Physics engine used in the game engine.
     */
    private volatile IPhysicsEngineDriver m_physicsEngineDriver;

    /**
     * Manager of state outside of physicsengine.
     */
    private volatile IGameStateManager m_stateManager;

    /**r
     * Rule processors to handle any outgoing messages. The last rule processor SendMessages sends all messages off.
     */
    private volatile IRuleProcessors m_ruleProcessors;

    /**
     * The identifier mapper to use.
     */
    private volatile IdentifierMapper m_id_mapper;

    /**
     * The discoverer to use.
     */
    private volatile Discoverer m_discoverer;

    private volatile ArchitectureEventController m_architectureEventListener;

    private final List<String> lobbiedDrones = new ArrayList<>();

    /**
     * Concrete message handlers.
     */
    private CollisionMessageHandler collisionMessageHandler;
    private DamageMessageHandler damageMessageHandler;
    private FireBulletMessageHandler fireBulletMessageHandler;
    private KillMessageHandler killMessageHandler;
    private MovementMessageHandler movementMessageHandler;
    private StateMessageHandler stateMessageHandler;

    /*** The game engine instance to register.
    */private GameEngineInstance discoveryInstance;

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GameEngine.class);

    /**
     * Starts the wrapper. Sets up all handlers, queues and engines. Connects everything if needed.
     */
    public void start() throws DuplicateName, IOException {
        log.info("======\n\n======== Starting Game Engine...");
        this.collisionMessageHandler = new CollisionMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.damageMessageHandler = new DamageMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.fireBulletMessageHandler = new FireBulletMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.killMessageHandler = new KillMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager, this.m_discoverer);
        this.movementMessageHandler = new MovementMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);
        this.stateMessageHandler = new StateMessageHandler(this.m_physicsEngineDriver, this.m_id_mapper, this.m_stateManager);




        // Setup discoverer
        discoveryInstance = new GameEngineInstance();
        m_discoverer.register(discoveryInstance);

        List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();

        addHandlers.add((AddedNode addedNodeEvent) -> {
            DiscoveryNode node = addedNodeEvent.getNode();
            DiscoveryPath path = node.getPath();

            if (path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                lobbiedDrones.add(node.getId());

                log.info("Added new drone " + node.getId() + " in simulation/lobby");
            }

        });

        List<NodeEventHandler<RemovedNode>> removeHandlers = new ArrayList<>();

        removeHandlers.add((RemovedNode removedNodeEvent) -> {
            DiscoveryNode node = removedNodeEvent.getNode();
            DiscoveryPath path = node.getPath();

            if (path.startsWith(DiscoveryPath.type(Type.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();

                lobbiedDrones.remove(protocolId);
                this.m_physicsEngineDriver.removeEntity(protocolId);
                log.info("Removed drone " + protocolId + " from simulation");
            }
        });

        m_discoverer.addHandlers(true, addHandlers, Collections.emptyList(), removeHandlers);

        // Setup Architecture Event listeners!
        m_architectureEventListener.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG, (SimulationState fromState, SimulationAction
                action, SimulationState toState) -> {
            log.info("Adding " + lobbiedDrones.size() + " drones to simulation");
            int dronesInLobby = lobbiedDrones.size();
            D2Vector center = new D2Vector(Settings.ARENA_WIDTH / 2, Settings.ARENA_DEPTH / 2);
            double spawnRadius = (Math.min(Settings.ARENA_DEPTH, Settings.ARENA_WIDTH) / 2) * 0.75;
            double spawnAngle = (2 * Math.PI) / dronesInLobby;

            int numberSpawned = 0;
            for (String protocolId : lobbiedDrones) {
                int gameengineId = m_id_mapper.getNewGameEngineId();
                D3Vector position = new D3Vector(Math.cos(spawnAngle * numberSpawned) * spawnRadius + center.getX()
                        , Math.sin(spawnAngle * numberSpawned) * spawnRadius + center.getY()
                        , Settings.ARENA_HEIGHT / 2);
                numberSpawned++;
                String team = DroneInstance.getTeamname(m_discoverer, protocolId);
                this.m_physicsEngineDriver.addNewEntity(new Drone(gameengineId, team, Drone.DRONE_MAX_HEALTH,
                        position, new D3Vector(), new D3Vector(), new D3PolarCoordinate(), position), protocolId);
                log.info("Added new drone " + protocolId + " as " + gameengineId);
            }
        });

        log.info("Started Game Engine!");
    }

    /**
     * Stops the wrapper. Kills the engine and rule processor threads.
     */
    public void stop() {
        try {
            this.m_discoverer.unregister(discoveryInstance);
        } catch (IOException e) {
            log.fatal(e);
        }
        log.info("Stopped Game Engine!");
    }

    @Override
    public void receive(Object obj, MultipartCallbacks multipartCallbacks) {
        System.out.println("GameEngine Received an object *******");
        if(obj instanceof CollisionMessage){
            collisionMessageHandler.handleMessage( (CollisionMessage) obj);
        }
        else if(obj instanceof DamageMessage){
            damageMessageHandler.handleMessage( (DamageMessage) obj);
        }
        else if(obj instanceof FireBulletMessage){
            fireBulletMessageHandler.handleMessage( (FireBulletMessage) obj);
        }
        else if(obj instanceof KillMessage){
            killMessageHandler.handleMessage( (KillMessage) obj);
        }
        else if(obj instanceof MovementMessage){
            movementMessageHandler.handleMessage( (MovementMessage) obj);
        }
        else if(obj instanceof TargetMoveLocationMessage){
            m_physicsEngineDriver.changeTargetLocationEntity((
                    (TargetMoveLocationMessage) obj).getIdentifier(), ((TargetMoveLocationMessage) obj).getTargetLocation());
        }
        else if(obj instanceof StateMessage){
            stateMessageHandler.handleMessage( (StateMessage) obj);
        }else{
            System.out.println("GameEngine.receive() got unknown message type");
        }

    }
}
