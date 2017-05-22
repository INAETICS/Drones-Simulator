package org.inaetics.dronessimulator.discovery.etcd;

import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.DiscoveryHandler;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.RemovedNode;
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
            AtomicBoolean catchprop1Added = new AtomicBoolean(false);
            AtomicBoolean catchprop1Set = new AtomicBoolean(false);
            AtomicBoolean removedDiscoverValue = new AtomicBoolean(false);
            AtomicBoolean removedDiscoverDir = new AtomicBoolean(false);

            this.changeHandler.start();

            // REGISTER INSTANCE THAT NEEDS TO BE REPLAYED
            Map<String, String> catchupProperties = new HashMap<>();
            catchupProperties.put("catchprop1", "catchupval1");

            this.discoverer.register(new Instance("catchup", "group", "hello!", catchupProperties, false));

            List<DiscoveryHandler<AddedNode>> addHandlers = new ArrayList<>();
            List<DiscoveryHandler<ChangedValue<String>>> changedValueHandlers = new ArrayList<>();
            List<DiscoveryHandler<RemovedNode<String>>> removedHandlers = new ArrayList<>();

            addHandlers.add((AddedNode addedNode) -> {
                switch(addedNode.getKey()) {
                    case "/instances/catchup":
                        Assert.assertFalse(catchupAdded.get());
                        catchupAdded.set(true);
                        break;
                    case "/instances/catchup/group/hello!/catchprop1":
                        Assert.assertFalse(catchprop1Added.get());
                        catchprop1Added.set(true);
                        break;
                }
            });

            changedValueHandlers.add((ChangedValue<String> changedValue) -> {
                switch(changedValue.getKey()) {
                    case "/instances/catchup/group/hello!/catchprop1":
                        Assert.assertFalse(catchprop1Set.get());
                        catchprop1Set.set(true);

                        Assert.assertEquals(null, changedValue.getOldValue());
                        Assert.assertEquals("catchupval1", changedValue.getNewValue());
                        break;
                }
            });

            removedHandlers.add((RemovedNode<String> removedNode) -> {
                switch(removedNode.getKey()) {
                    case "/instances/catchup/discover/name/discover":
                        Assert.assertEquals("discoverValue", removedNode.getValue());
                        Assert.assertFalse(removedDiscoverValue.get());
                        removedDiscoverValue.set(true);
                        break;
                    case "/instances/catchup/discover/name":
                        Assert.assertEquals(null, removedNode.getValue());
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

            Instance discoverInstance = new Instance("catchup", "discover", "name", discoverProperties, false);
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
            Assert.assertTrue(catchprop1Added.get());
            Assert.assertTrue(catchprop1Set.get());
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
