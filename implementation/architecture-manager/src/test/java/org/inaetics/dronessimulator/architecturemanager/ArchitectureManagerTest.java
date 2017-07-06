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
import org.inaetics.dronessimulator.discovery.etcd.EtcdDiscoverer;
import org.inaetics.dronessimulator.discovery.etcd.EtcdDiscovererService;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.pubsub.api.serializer.Serializer;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.RabbitPublisher;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ArchitectureManagerTest {
    SimulationState[] validCurrentStates = new SimulationState[]{
            SimulationState.NOSTATE,
            SimulationState.INIT,
            SimulationState.CONFIG,
            SimulationState.CONFIG,
            SimulationState.RUNNING,
            SimulationState.RUNNING,
            SimulationState.RUNNING,
            SimulationState.PAUSED,
            SimulationState.PAUSED,
            SimulationState.DONE,
    };
    SimulationAction[] validActions = new SimulationAction[]{
            SimulationAction.INIT,
            SimulationAction.CONFIG,
            SimulationAction.STOP,
            SimulationAction.START,
            SimulationAction.STOP,
            SimulationAction.PAUSE,
            SimulationAction.GAMEOVER,
            SimulationAction.STOP,
            SimulationAction.RESUME,
            SimulationAction.STOP,
    };
    SimulationState[] validFinalStates = new SimulationState[]{
            SimulationState.INIT,
            SimulationState.CONFIG,
            SimulationState.INIT,
            SimulationState.RUNNING,
            SimulationState.INIT,
            SimulationState.PAUSED,
            SimulationState.DONE,
            SimulationState.INIT,
            SimulationState.RUNNING,
            SimulationState.INIT,
    };

    int numActions = 10;

    SimulationState[] allStates = new SimulationState[]{
            SimulationState.NOSTATE,
            SimulationState.INIT,
            SimulationState.CONFIG,
            SimulationState.RUNNING,
            SimulationState.PAUSED,
            SimulationState.DONE,
    };
    SimulationAction[] allActions = new SimulationAction[]{
            SimulationAction.INIT,
            SimulationAction.CONFIG,
            SimulationAction.START,
            SimulationAction.PAUSE,
            SimulationAction.RESUME,
            SimulationAction.GAMEOVER,
            SimulationAction.STOP,
    };

    @Test
    public void testNextState() throws Exception {
        for (SimulationState startState : allStates) {
            for (SimulationAction action : allActions) {
                SimulationState endState = ArchitectureManager.nextState(startState, action);

                for (int i = 0; i < numActions; i++) {
                    if (startState == validCurrentStates[i] && action == validActions[i]) {
                        String msg = String.format("%s.%s", startState.toString(), action.toString());
                        assertEquals(msg, validFinalStates[i], endState);
                    }
                }
            }
        }
    }

    @Test
    public void testLifecycleUpdates() throws Exception {
        EtcdDiscovererService discoverer = new EtcdDiscovererService();

        Serializer serializer = new JavaSerializer();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        RabbitPublisher publisher = new RabbitPublisher(connectionFactory, serializer);
        RabbitSubscriber subscriber = new RabbitSubscriber(connectionFactory, "architecture_test", serializer);

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

        // Actual test
        RequestArchitectureStateChangeMessage testMessage = new RequestArchitectureStateChangeMessage();
        testMessage.setAction(SimulationAction.CONFIG);
        publisher.send(MessageTopic.ARCHITECTURE, testMessage);

        Thread.sleep(100);
        assertTrue("state changed", isReceived.get());

        // Stop everything
        manager.stop();
        subscriber.disconnect();
        publisher.disconnect();
        discoverer.stop();
    }

}