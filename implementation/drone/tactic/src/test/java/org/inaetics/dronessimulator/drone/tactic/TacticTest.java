package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventHandler;
import org.inaetics.dronessimulator.architectureevents.LifeCycleStep;
import org.inaetics.dronessimulator.common.TimeoutTimer;
import org.inaetics.dronessimulator.common.Tuple;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.MockDiscoverer;
import org.inaetics.dronessimulator.discovery.api.instances.TacticInstance;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.inaetics.dronessimulator.test.MockPublisher;
import org.inaetics.dronessimulator.test.MockSubscriber;
import org.inaetics.dronessimulator.test.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.inaetics.dronessimulator.test.TestUtils.*;
import static org.mockito.Mockito.*;

public class TacticTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
    private Publisher publisher;
    private MockSubscriber subscriber;
    private DroneInit drone;
    private Tactic tacticMock;
    private Tactic tactic;
    private MockDiscoverer discoverer;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TacticTest.class);

    @Before
    public void setUp() throws Exception {
        Tuple<MockPublisher, MockSubscriber> publisherSubscriberTuple = getConnectedMockPubSub();
        publisher = publisherSubscriberTuple.getLeft();
        subscriber = publisherSubscriberTuple.getRight();
        discoverer = spy(new MockDiscoverer());
        drone = new DroneInit();
        tacticMock = spy(new DoNothingTactic());
        tactic = TacticTesterHelper.getTactic(tacticMock, publisher, subscriber, discoverer, drone, "engine", "radio", "radar", "gps", "gun");
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
    }

    @Test
    public void testWorkWithSlowTactic() throws Exception {
        double timeout = getField(getField(tactic, "ticker"), "timeout");
        long durationLongCommand = (long) timeout * 5;
        boolean[] isInterrupted = new boolean[1];
        Tactic tacticSlow = new DoNothingTactic() {
            @Override
            protected void calculateTactics() {
                try {
                    //Make a long running command, like thread.sleep
                    Thread.sleep(durationLongCommand);
                } catch (InterruptedException e) {
                    //Great
                    isInterrupted[0] = true;
                }
            }
        };
        long executeStart = System.currentTimeMillis();
        tacticSlow.work();
        long executeEnd = System.currentTimeMillis();
        Assert.assertTrue(isInterrupted[0]);
        Assert.assertTrue(executeEnd - executeStart < durationLongCommand);
    }

    @Test
    public void testStartAndStopTactic() throws Exception {
        ArchitectureEventController architectureEventController = getField(tactic, "architectureEventController");
        Instance instance = new TacticInstance(tactic.getIdentifier());
        AtomicBoolean started = getField(tactic, "started");
        AtomicBoolean pauseToken = getField(tactic, "pauseToken");
        AtomicBoolean quit = getField(tactic, "quit");

        //First start it
        tactic.startTactic();
        Assert.assertTrue(subscriber.getHandlers().get(KillMessage.class).contains(tactic));
        Assert.assertEquals(getField(tactic, "simulationInstance"), instance);
        Assert.assertTrue(tactic.isAlive());

        //Get all handlers
        Map<LifeCycleStep, List<ArchitectureEventHandler>> handlers = getField(architectureEventController, "handlers");
        List<ArchitectureEventHandler> configHandlers = handlers.get(new LifeCycleStep(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG));
        List<ArchitectureEventHandler> startHandlers = handlers.get(new LifeCycleStep(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING));
        List<ArchitectureEventHandler> pauzeHandlers = handlers.get(new LifeCycleStep(SimulationState.RUNNING, SimulationAction.PAUSE, SimulationState.PAUSED));
        List<ArchitectureEventHandler> resumeHandlers = handlers.get(new LifeCycleStep(SimulationState.PAUSED, SimulationAction.RESUME, SimulationState.RUNNING));
        List<ArchitectureEventHandler> stopHandlers = handlers.get(new LifeCycleStep(SimulationState.RUNNING, SimulationAction.STOP, SimulationState.INIT));

        Assert.assertEquals(8, handlers.size());
        Assert.assertThat(handlers.keySet(), hasItem(new LifeCycleStep(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG)));

        //Let the tactic configure itself
        configHandlers.forEach(handler -> handler.handle(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG));
        Assert.assertThat(discoverer.getRegisteredInstances(), hasItem(instance));

        //Start it
        startHandlers.forEach(handler -> handler.handle(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING));
        Assert.assertTrue(started.get());
        verify(tacticMock, atMost(1)).initializeTactics();

        //Pauze it
        pauzeHandlers.forEach(handler -> handler.handle(SimulationState.RUNNING, SimulationAction.PAUSE, SimulationState.PAUSED));
        Assert.assertTrue(pauseToken.get());

        //Resume it
        resumeHandlers.forEach(handler -> handler.handle(SimulationState.PAUSED, SimulationAction.RESUME, SimulationState.RUNNING));
        Assert.assertFalse(pauseToken.get());

        //Now stop it
        stopHandlers.forEach(handler -> handler.handle(SimulationState.RUNNING, SimulationAction.STOP, SimulationState.INIT));
        tactic.stopTactic();
        Assert.assertTrue(quit.get());
        verify(tacticMock, atMost(1)).finalizeTactics();
        Assert.assertEquals(0, discoverer.getRegisteredInstances().size());
        long starttime = System.currentTimeMillis();
//        await().atMost(1000, TimeUnit.MILLISECONDS).until(() -> !started.get());
//        Assert.assertFalse(started.get());
    }

    @Test
    public void handleMessage() throws Exception {
        if (!log.isDebugEnabled()) {
            exit.expectSystemExitWithStatus(10);
            exit.checkAssertionAfterwards(() -> {
                verify(tacticMock, atMost(1)).stopThread();
            });
        }
        KillMessage msg = new KillMessage();
        //Not the correct identifier so nothing should happen
        msg.setIdentifier("");
        tactic.handleMessage(msg);
        verify(tacticMock, atMost(0)).stopThread();
        msg.setIdentifier(tactic.getIdentifier());
        tactic.handleMessage(msg);
        verify(tacticMock, atMost(1)).stopThread();
    }

    @Test
    public void hasComponentsAndValidateRequiredComponents() throws Exception {
        Assert.assertTrue(tactic.hasComponents("engine", "radio", "radar", "gps", "gun"));
        Assert.assertTrue(tactic.hasComponents("engine", "radio", "radar", "gps", "gun", "nonsense"));
        Assert.assertTrue(tactic.hasComponents("engine"));
        Assert.assertTrue(tactic.hasComponents("radio"));
        Assert.assertTrue(tactic.hasComponents("radar"));
        Assert.assertTrue(tactic.hasComponents("gps"));
        Assert.assertTrue(tactic.hasComponents("gun"));
        Assert.assertTrue(tactic.hasComponents("nonsense"));
        //Remove the gun
        setField(tactic, "gun", null);
        Assert.assertFalse(tactic.hasComponents("gun"));

        assertThrows(Tactic.MissingComponentsException.class, () -> tactic.validateRequiredComponents("gun"));
        tactic.validateRequiredComponents("radio"); //This should just execute without any exception
    }

    @Test
    public void getAvailableComponents() throws Exception {
        Assert.assertEquals(new HashSet<>(Arrays.asList("engine", "radio", "radar", "gps", "gun")), tactic.getAvailableComponents());
        //Remove the gun
        setField(tactic, "gun", null);
        Assert.assertEquals(new HashSet<>(Arrays.asList("engine", "radio", "radar", "gps")), tactic.getAvailableComponents());
    }

}