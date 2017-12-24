package org.inaetics.dronessimulator.architecturemanager;

import com.rabbitmq.client.ConnectionFactory;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventControllerService;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventHandler;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;
import org.inaetics.dronessimulator.discovery.api.instances.ArchitectureInstance;
import org.inaetics.dronessimulator.discovery.etcd.EtcdDiscovererService;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.common.RabbitConnectionInfo;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.RabbitPublisher;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class LifecycleUpdateIT {

    private EtcdDiscovererService discoverer;
    private RabbitPublisher publisher;
    private RabbitSubscriber subscriber;

    @Before
    public void setup() {
        discoverer = new EtcdDiscovererService();
        discoverer.start();

        Serializer serializer = new JavaSerializer();
        RabbitConnectionInfo connectionInfo = RabbitConnectionInfo.createInstance(discoverer);
        ConnectionFactory connectionFactory = null;
        try {
            connectionFactory = connectionInfo.createConnectionFactory();
        } catch (RabbitConnectionInfo.ConnectionInfoExpiredException e) {
            e.printStackTrace();
            System.out.println("Using default fallback");
            connectionFactory = new ConnectionFactory();
            connectionFactory.setUsername("yourUser");
            connectionFactory.setPassword("yourPass");
        }
        publisher = new RabbitPublisher(connectionFactory, serializer, discoverer);
        subscriber = new RabbitSubscriber(connectionFactory, "architecture_test", serializer, discoverer);
    }

    @Test
    public void testLifecycleUpdates() throws Exception {
        ArchitectureManager manager = new ArchitectureManager(discoverer, subscriber);
        ArchitectureEventControllerService controller = new ArchitectureEventControllerService(discoverer);
        AtomicBoolean isReceived = new AtomicBoolean(false);
        ArchitectureEventHandler handler = (SimulationState fromState, SimulationAction action, SimulationState toState) -> isReceived.set(toState == SimulationState.CONFIG);

        controller.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG, handler);

        // Start everything
        discoverer.start();
        publisher.connect();
        subscriber.connect();
        manager.start();
        controller.start();

        //Set the state for etcd
        HashMap<String, String> stateMap = new HashMap<>();
        stateMap.put("current_life_cycle", "NOSTATE.INIT.INIT");
        discoverer.updateProperties(new ArchitectureInstance(), stateMap);

        // Actual test
        RequestArchitectureStateChangeMessage testMessage = new RequestArchitectureStateChangeMessage();
        testMessage.setAction(SimulationAction.CONFIG);
        publisher.send(MessageTopic.ARCHITECTURE, testMessage);

        int attempts = 0;
        while (attempts <= 3 && !isReceived.get()) {
            Thread.sleep(100);
            if (attempts == 3) {
                String currentState = "UNKNOWN";
                DiscoveryStoredNode stateNode = discoverer.getNode(new ArchitectureInstance());
                if (stateNode != null) {
                    Map<String, String> stateNodeValues = stateNode.getValues();
                    if (stateNodeValues != null && !stateNodeValues.isEmpty()) {
                        currentState = stateNodeValues.get("current_life_cycle");
                    }
                }
                assertTrue("state did not change to CONFIG. Current state: " + currentState, isReceived.get());
            }
            attempts++;
        }
        // Stop everything
        manager.stop();
    }

    @After
    public void tearDown() throws IOException {

        subscriber.disconnect();
        publisher.disconnect();
        discoverer.stop();
    }

}