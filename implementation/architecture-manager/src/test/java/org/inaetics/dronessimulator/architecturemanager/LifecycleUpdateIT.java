package org.inaetics.dronessimulator.architecturemanager;

import com.rabbitmq.client.ConnectionFactory;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventControllerService;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventHandler;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.RequestArchitectureStateChangeMessage;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.etcd.EtcdDiscoverer;
import org.inaetics.dronessimulator.discovery.etcd.EtcdDiscovererService;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.RabbitPublisher;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class LifecycleUpdateIT {

    private EtcdDiscovererService discoverer;
    private RabbitPublisher publisher;
    private RabbitSubscriber subscriber;

    @Before
    public void setup() {
        discoverer = new EtcdDiscovererService();

        Serializer serializer = new JavaSerializer();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //TODO replace this with a call to RabbitConnectionInfo from #10
        connectionFactory.setUsername("yourUser");
        connectionFactory.setPassword("yourPass");
        publisher = new RabbitPublisher(connectionFactory, serializer);
        subscriber = new RabbitSubscriber(connectionFactory, "architecture_test", serializer);
    }

    @Test
    public void testLifecycleUpdates() throws Exception {
        ArchitectureManager manager = new ArchitectureManager(discoverer, subscriber);
        ArchitectureEventControllerService controller = new ArchitectureEventControllerService(discoverer);
        AtomicBoolean isReceived = new AtomicBoolean(false);
        ArchitectureEventHandler handler = (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
            isReceived.set(toState == SimulationState.CONFIG);
        };

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
        discoverer.updateProperties(new Instance(Type.SERVICE, Group.SERVICES, "architecture"), stateMap); //TODO replace this with the Architecture instance from issue #10.

        // Actual test
        RequestArchitectureStateChangeMessage testMessage = new RequestArchitectureStateChangeMessage();
        testMessage.setAction(SimulationAction.CONFIG);
        publisher.send(MessageTopic.ARCHITECTURE, testMessage);

        int attempts = 0;
        while (attempts <= 3 && !isReceived.get()) {
            Thread.sleep(100);
            if (attempts == 3)
                assertTrue("state did not change to CONFIG. Current state: UNKNOWN", isReceived.get()); //TODO replace with actual value after mering #10
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