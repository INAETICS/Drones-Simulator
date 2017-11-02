package org.inaetics.dronessimulator.architectureevents;

import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.mocks.MockDiscoverer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArchitectureEventControllerServiceTest {
    private MockDiscoverer discoverer;
    private ArchitectureEventControllerService controllerService;

    @Before
    public void setup() {
        discoverer = new MockDiscoverer();
        controllerService = new ArchitectureEventControllerService(discoverer);
    }

    @Test
    public void testStartAndTestHandleNewState() {
        //Start adds a change value handler to discovery for any state changes. After the handlers are added, the state changes that were added before are also replayed and this updates the current state.

        //Given
        //Add some events
        discoverer.addHappenedEvent(mock(AddedNode.class));
        discoverer.addHappenedEvent(mock(RemovedNode.class));
        ChangedValue mockedChangeEvent = mock(ChangedValue.class);
        discoverer.addHappenedEvent(mockedChangeEvent);

        //When the change event is received, it should transfer the state to running and call the approriate handler
        DiscoveryNode node = mock(DiscoveryNode.class);
        when(node.getValue("current_life_cycle")).thenReturn("CONFIG.START.RUNNING");
        when(node.getId()).thenReturn("architecture");
        when(node.getPath()).thenReturn(DiscoveryPath.config(Type.SERVICE, Group.SERVICES, "architecture"));
        final boolean[] stateChangeHandlerIsCalled = {false};
        controllerService.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING, (fromState, action, toState) -> stateChangeHandlerIsCalled[0] = true);
        when(mockedChangeEvent.getNode()).thenReturn(node);
        when(mockedChangeEvent.getKey()).thenReturn("current_life_cycle");

        //When
        controllerService.start();

        //Then
        Assert.assertTrue(discoverer.getAddedHandlers().isEmpty());
        Assert.assertTrue(discoverer.getRemovedHandlers().isEmpty());
        Assert.assertTrue(discoverer.getChangedHandlers().size() == 1);
        Assert.assertTrue("The state change handler was not called.", stateChangeHandlerIsCalled[0]);
    }

    @Test
    public void testNoLifecycle() {
        //Given
        //Add some events
        discoverer.addHappenedEvent(mock(AddedNode.class));
        discoverer.addHappenedEvent(mock(RemovedNode.class));
        ChangedValue mockedChangeEvent = mock(ChangedValue.class);
        discoverer.addHappenedEvent(mockedChangeEvent);

        //When the change event is received, it should transfer the state to running and call the approriate handler
        DiscoveryNode node = mock(DiscoveryNode.class);
        when(node.getValue("current_life_cycle")).thenReturn(null);
        when(node.getId()).thenReturn("architecture");
        when(node.getPath()).thenReturn(DiscoveryPath.config(Type.SERVICE, Group.SERVICES, "architecture"));
        final boolean[] stateChangeHandlerIsCalled = {false};
        controllerService.addHandler(SimulationState.CONFIG, SimulationAction.START, SimulationState.RUNNING, (fromState, action, toState) -> stateChangeHandlerIsCalled[0] = true);
        when(mockedChangeEvent.getNode()).thenReturn(node);
        when(mockedChangeEvent.getKey()).thenReturn("current_life_cycle");

        //When
        controllerService.start();
    }

    @Test
    public void testOSGIConstructor() {
        //This test checks if you can create the object without parameters. This is mostly to improve coverage results.
        controllerService = new ArchitectureEventControllerService();
        Assert.assertNotNull(controllerService);
    }
}
