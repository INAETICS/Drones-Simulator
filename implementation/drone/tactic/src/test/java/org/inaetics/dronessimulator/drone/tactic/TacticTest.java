package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventHandler;
import org.inaetics.dronessimulator.architectureevents.LifeCycleStep;
import org.inaetics.dronessimulator.common.TimeoutTimer;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.test.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.inaetics.dronessimulator.test.TestUtils.getField;
import static org.mockito.Mockito.*;

public class TacticTest {
    private Publisher publisher;
    private TacticTesterHelper.MockSubscriber subscriber;
    private DroneInit drone;
    private Tactic tacticMock;
    private Tactic tactic;

    @Before
    public void setUp() throws Exception {
        Tuple<Publisher, TacticTesterHelper.MockSubscriber> publisherSubscriberTuple = TacticTesterHelper.getConnectedMockPubSub();
        publisher = publisherSubscriberTuple.getLeft();
        subscriber = publisherSubscriberTuple.getRight();
        drone = new DroneInit();
        tacticMock = spy(new DoNothingTactic());
        tactic = TacticTesterHelper.getTactic(tacticMock, publisher, subscriber, drone, "engine", "radio", "radar", "gun");
    }

    @Test
    public void testWork() throws Exception {
        TimeoutTimer ticker = getField(tactic, "ticker");
        double timeout = getField(ticker, "timeout");
        //Check before that it is not yet run
        verify(tacticMock, times(0)).calculateTactics();
        //Validate that the timeout works well by first making it that the time is not exceeded and then that it is exceeded.
        ticker.reset();
        tactic.work();
        verify(tacticMock, times(0)).calculateTactics();
        TestUtils.setField(ticker, "lastTime", System.currentTimeMillis() - (long) timeout - 1L);
        tactic.work();
        verify(tacticMock, times(1)).calculateTactics();
        //Kill al long running command
        boolean[] isInterrupted = new boolean[1];
        doAnswer((Answer<Void>) invocation -> {
            try {
                //Make a long running command, like thread.sleep
                Thread.sleep((long) (timeout * 2));
            } catch (InterruptedException e) {
                //Great
                isInterrupted[0] = true;
            }
            return null;
        }).when(tacticMock).calculateTactics();
        long executeStart = System.currentTimeMillis();
        tactic.work();
        long executeEnd = System.currentTimeMillis();
        Assert.assertTrue(isInterrupted[0]);
        Assert.assertTrue(executeEnd - executeStart < (timeout * 2));
    }

    @Test
    public void testStartAndStopTactic() throws Exception {
        ArchitectureEventController architectureEventController = getField(tactic, "m_architectureEventController");
        //First start it
        tactic.startTactic();
        Map<LifeCycleStep, List<ArchitectureEventHandler>> handlers = getField(architectureEventController, "handlers");
        //Let the tactic configure itself
        List<ArchitectureEventHandler> configHandlers = handlers.get(new LifeCycleStep(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG));
        configHandlers.forEach(handler -> handler.handle(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG));

        //TODO check the discovery
        Assert.assertEquals(handlers.size(), 8);
        Assert.assertThat(handlers.keySet(), hasItem(new LifeCycleStep(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG)));
        Assert.assertNotNull(getField(tactic, "simulationInstance"));
        Assert.assertTrue(subscriber.getHandlers().get(KillMessage.class).contains(tactic));
        Assert.assertTrue(tactic.isAlive());

        //Now stop it
        tactic.stopTactic();
        //TODO validate the actions that are taken by stop tactic
    }

    @Test
    public void stopTactic() throws Exception {
    }

    @Test
    public void destroy() throws Exception {
    }

    @Test
    public void handleMessage() throws Exception {
    }

    @Test
    public void getIdentifier() throws Exception {
    }

    @Test
    public void hasComponents() throws Exception {
    }

    @Test
    public void validateRequiredComponents() throws Exception {
    }

    @Test
    public void getAvailableComponents() throws Exception {
    }

}