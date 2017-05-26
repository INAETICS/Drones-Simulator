package org.inaetics.dronessimulator.discovery.etcd;

import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestDiscoveryEtcdChangeHandler {
    private EtcdDiscoverer discoverer;
    private EtcdChangeHandler changeHandler;


    @Before
    public void init() {
        this.discoverer = new EtcdDiscoverer(URI.create("http://localhost:4001/"));
        this.changeHandler = new EtcdChangeHandler(discoverer);
    }

    @Test
    public void testChanges() throws DuplicateName, IOException {
        try {
            AtomicBoolean catchupAdded = new AtomicBoolean(false);
            AtomicBoolean helloAdded = new AtomicBoolean(false);
            AtomicBoolean catchprop1Set = new AtomicBoolean(false);

            AtomicBoolean nameAdded = new AtomicBoolean(false);
            AtomicBoolean discoverValueAdded = new AtomicBoolean(false);
            AtomicBoolean removedDiscoverValue = new AtomicBoolean(false);
            AtomicBoolean removedDiscoverDir = new AtomicBoolean(false);

            this.changeHandler.start();

            // REGISTER INSTANCE THAT NEEDS TO BE REPLAYED
            Map<String, String> catchupProperties = new HashMap<>();
            catchupProperties.put("catchprop1", "catchupval1");

            this.discoverer.register(new Instance("catchup", "group", "hello!", catchupProperties));

            List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();
            List<NodeEventHandler<ChangedValue>> changedValueHandlers = new ArrayList<>();
            List<NodeEventHandler<RemovedNode>> removedHandlers = new ArrayList<>();

            addHandlers.add((AddedNode addedNodeEvent) -> {
                DiscoveryNode addedNode = addedNodeEvent.getNode();
                switch(addedNode.getId()) {
                    case "catchup":
                        Assert.assertFalse(catchupAdded.get());
                        catchupAdded.set(true);
                        break;
                    case "hello!":
                        Assert.assertFalse(helloAdded.get());
                        helloAdded.set(true);
                        break;
                    case "name":
                        Assert.assertFalse(nameAdded.get());
                        nameAdded.set(true);
                        break;
                }
            });

            AtomicBoolean issetDiscover = new AtomicBoolean(false);

            changedValueHandlers.add((ChangedValue changedValue) -> {
                switch(changedValue.getKey()) {
                    case "catchprop1":
                        // catchprop1 is not being unset
                        if(changedValue.getNewValue() != null) {
                            Assert.assertFalse(catchprop1Set.get());
                            catchprop1Set.set(true);

                            Assert.assertEquals(null, changedValue.getOldValue());
                            Assert.assertEquals("catchupval1", changedValue.getNewValue());
                        }
                        break;
                    case "discover":
                        if(!issetDiscover.get()) {
                            Assert.assertEquals(null, changedValue.getOldValue());
                            Assert.assertEquals("discoverValue", changedValue.getNewValue());

                            Assert.assertFalse(discoverValueAdded.get());
                            discoverValueAdded.set(true);
                            issetDiscover.set(true);
                        } else {
                            Assert.assertEquals("discoverValue", changedValue.getOldValue());
                            Assert.assertEquals(null, changedValue.getNewValue());

                            Assert.assertFalse(removedDiscoverValue.get());
                            removedDiscoverValue.set(true);
                        }
                }
            });

            removedHandlers.add((RemovedNode removedNodeEvent) -> {
                DiscoveryNode removedNode = removedNodeEvent.getNode();
                switch(removedNode.getId()) {
                    case "name":
                        Assert.assertFalse(removedDiscoverDir.get());
                        removedDiscoverDir.set(true);
                        break;
                }
            });

            this.changeHandler.addHandlers(true, addHandlers, changedValueHandlers, removedHandlers);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Map<String, String> discoverProperties = new HashMap<>();
            discoverProperties.put("discover", "discoverValue");

            Instance discoverInstance = new Instance("catchup", "discover", "name", discoverProperties);
            this.discoverer.register(discoverInstance);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.discoverer.unregister(discoverInstance);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Assert.assertTrue(catchupAdded.get());
            Assert.assertTrue(helloAdded.get());
            Assert.assertTrue(catchprop1Set.get());
            Assert.assertTrue(nameAdded.get());
            Assert.assertTrue(discoverValueAdded.get());
            Assert.assertTrue(removedDiscoverValue.get());
            Assert.assertTrue(removedDiscoverDir.get());
        } finally {
            this.changeHandler.quit();
            try {
                this.changeHandler.join(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @After
    public void teardown() {
        try {
            this.discoverer.unregisterAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
