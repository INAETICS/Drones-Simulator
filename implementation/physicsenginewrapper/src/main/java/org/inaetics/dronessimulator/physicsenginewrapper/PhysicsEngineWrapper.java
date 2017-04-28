package org.inaetics.dronessimulator.physicsenginewrapper;

import lombok.Getter;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.physicsengine.PhysicsEngine;
import org.inaetics.dronessimulator.physicsengine.entityupdate.VelocityEntityUpdate;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.PhysicsEngineMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.RuleProcessors;
import org.inaetics.dronessimulator.physicsenginewrapper.state.GameStateManager;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Wrapper around PhysicsEngine. Sets up and connects all handlers with each other.
 * Set up are: physicsengine, incoming command messages, queue between physicsengine and ruleprocessors,
 * discovery handler
 * and ruleprocessors
 */
public class PhysicsEngineWrapper {
    /**
     * Publisher to send messages to
     */
    private volatile Publisher m_publisher;
    /**
     * Subscriber to listens messages from
     */
    private volatile Subscriber m_subscriber;

    /**
     * Physicsengine which is wrapped
     */
    private PhysicsEngine physicsEngine;
    /**
     * Message handler to handle incoming commands from drones
     */
    private DroneCommandMessageHandler incomingHandler;
    /**
     * Message handler to handle any newly discovered or removed drones
     */
    private DiscoveryHandler discoveryHandler;

    /**
     * Message handler for messages from physics engine to queue between physicsengine and ruleprocessors
     */
    @Getter
    private PhysicsEngineObserver outgoingHandler;
    /**
     * Manager of state outside of physicsengine
     */
    private GameStateManager stateManager;
    /**
     * Ruleprocessors to handle any outgoing messages. Last ruleprocessor SendMessages send all messages off
     */
    private RuleProcessors ruleProcessors;

    /**
     * Start the wrapper. Setup all handlers, queues and engines. Connects everything if needed.
     */
    public void start() {
        Logger.getLogger(PhysicsEngineWrapper.class).info("Starting Physics Engine Wrapper!");
        this.stateManager = new GameStateManager();
        LinkedBlockingQueue<PhysicsEngineMessage> engineMsgs = new LinkedBlockingQueue<>();
        this.outgoingHandler = new PhysicsEngineObserver(engineMsgs);
        this.physicsEngine = new PhysicsEngine(this.outgoingHandler);
        this.physicsEngine.setTimeBetweenBroadcastms(2000);
        this.incomingHandler = new DroneCommandMessageHandler(this.physicsEngine);
        this.discoveryHandler = new DiscoveryHandler(this.physicsEngine, this.stateManager);


        //INSERT TEST DATA
        this.discoveryHandler.newDrone(1, new D3Vector());
        this.physicsEngine.addUpdate(1, new VelocityEntityUpdate(new D3Vector(1,0,0)));

        this.ruleProcessors = new RuleProcessors( engineMsgs
                                                , this.stateManager
                                                , this.m_publisher
                                                );

        try {
            this.m_subscriber.addTopic(MessageTopic.MOVEMENTS);
        } catch(IOException e) {
            Logger.getLogger(PhysicsEngineWrapper.class).fatal("Could not subscribe to topic " + MessageTopic.MOVEMENTS + ".");
        }

        this.m_subscriber.addHandler(MovementMessage.class, this.incomingHandler);

        Logger.getLogger(PhysicsEngineWrapper.class).info("Starting physics engine!");
        this.physicsEngine.start();

        Logger.getLogger(PhysicsEngineWrapper.class).info("Starting game rule processors!");
        this.ruleProcessors.start();
    }

    /**
     * Stops the wrapper. Kills the engine and ruleprocessor threads
     * @throws Exception - Any exception which might happen during shutting down the wrapper
     */
    public void stop() throws Exception {
        Logger.getLogger(PhysicsEngineWrapper.class).info("Stopping game rule processors!");
        ruleProcessors.quit();
        Logger.getLogger(PhysicsEngineWrapper.class).info("Stopping physics engine!");
        physicsEngine.quit();
        physicsEngine.join();
        Logger.getLogger(PhysicsEngineWrapper.class).info("Stopped physics engine!");
        ruleProcessors.join();
        Logger.getLogger(PhysicsEngineWrapper.class).info("Stopped game rule processors!");
    }
}
