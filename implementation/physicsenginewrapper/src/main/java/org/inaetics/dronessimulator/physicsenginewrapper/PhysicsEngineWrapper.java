package org.inaetics.dronessimulator.physicsenginewrapper;

import lombok.Getter;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.MovementMessage;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.physicsengine.PhysicsEngine;
import org.inaetics.dronessimulator.physicsengine.entityupdate.VelocityEntityUpdate;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.RuleProcessors;
import org.inaetics.dronessimulator.physicsenginewrapper.state.PhysicsEngineStateManager;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class PhysicsEngineWrapper {
    private volatile Publisher m_publisher;
    private volatile Subscriber m_subscriber;

    private PhysicsEngine physicsEngine;
    private DroneCommandMessageHandler incomingHandler;
    private DiscoveryHandler discoveryHandler;

    @Getter
    private PhysicsEngineObserver outgoingHandler;
    private PhysicsEngineStateManager stateManager;
    private RuleProcessors ruleProcessors;

    public void start() {
        Logger.getLogger(PhysicsEngineWrapper.class).info("Starting Physics Engine Wrapper!");
        this.stateManager = new PhysicsEngineStateManager();
        this.outgoingHandler = new PhysicsEngineObserver();
        this.physicsEngine = new PhysicsEngine();
        this.physicsEngine.setTimeBetweenBroadcastms(20);
        this.physicsEngine.setObserver(this.outgoingHandler);
        this.incomingHandler = new DroneCommandMessageHandler(this.physicsEngine);
        this.discoveryHandler = new DiscoveryHandler(this.physicsEngine, this.stateManager);


        //INSERT TEST DATA
        this.discoveryHandler.newDrone(1, new D3Vector());
        this.physicsEngine.addUpdate(1, new VelocityEntityUpdate(new D3Vector(5,0,0)));

        this.ruleProcessors = new RuleProcessors( this.outgoingHandler.getOutgoingQueue()
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
