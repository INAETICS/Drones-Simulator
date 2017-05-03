package org.inaetics.dronessimulator.gameengine;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.GameStateManager;
import org.inaetics.dronessimulator.gameengine.gamestatemanager.IGameStateManager;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.IPhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.PhysicsEngineDriver;
import org.inaetics.dronessimulator.gameengine.physicsenginedriver.gameentityupdate.VelocityUpdate;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.IRuleProcessors;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.RuleProcessors;
import org.inaetics.dronessimulator.physicsengine.PhysicsEngine;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Wrapper around PhysicsEngine. Sets up and connects all handlers with each other.
 * Set up are: physicsengine, incoming command messages, queue between physicsengine and ruleprocessors,
 * discovery handler
 * and ruleprocessors
 */
public class GameEngine {
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

    private volatile ConfigurationAdmin configurationAdmin;

    /**
     * Message handler to handle any newly discovered or removed drones
     */
    private DiscoveryHandler discoveryHandler;

    /**
     * Message handler to handle incoming commands from drones
     */
    private SubscriberMessageHandler incomingHandler;

    /**
     * Start the wrapper. Setup all handlers, queues and engines. Connects everything if needed.
     */
    public void start() {
        try {
            System.out.println(configurationAdmin);
            System.out.println(configurationAdmin.getConfiguration("org.ops4j.pax.logging"));
            System.out.println("Properties: " + configurationAdmin.getConfiguration("org.ops4j.pax.logging").getProperties());

            Configuration config = configurationAdmin.getConfiguration("org.ops4j.pax.logging");
            Dictionary<String, Object> props = config.getProperties();

            if(props == null) {
                props = new Hashtable<>();
            } else {
                System.out.println(props.get("org.ops4j.pax.logging.DefaultServiceLog.level"));
                System.out.println(props.get("DefaultServiceLog.level"));
                System.out.println(props.get("level"));
            }

            props.put("org.ops4j.pax.logging.DefaultServiceLog.level", "INFO");
            props.put("log4j.rootLogger", "INFO");

            config.update(props);




        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getLogger(GameEngine.class).info("Starting Game Engine...");
        this.incomingHandler = new SubscriberMessageHandler(this.m_physicsEngineDriver);
        this.discoveryHandler = new DiscoveryHandler(this.m_physicsEngineDriver);

        try {
            this.m_subscriber.addTopic(MessageTopic.MOVEMENTS);
        } catch(IOException e) {
            Logger.getLogger(GameEngine.class).fatal("Could not subscribe to topic " + MessageTopic.MOVEMENTS + ".");
        }

        this.m_subscriber.addHandler(MovementMessage.class, this.incomingHandler);

        //INSERT TEST DATA
        this.discoveryHandler.newDrone(1, new D3Vector());

        Logger.getLogger(GameEngine.class).info("Started Game Engine!");
    }

    /**
     * Stops the wrapper. Kills the engine and ruleprocessor threads
     * @throws Exception - Any exception which might happen during shutting down the wrapper
     */
    public void stop() throws Exception {
        Logger.getLogger(GameEngine.class).info("Stopped Game Engine!");
    }
}
