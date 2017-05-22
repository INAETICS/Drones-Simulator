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

public class TestDiscoveryEtcdChangeHandler {
    private EtcdDiscoverer discoverer;
    private EtcdChangeHandler changeHandler;


    @Before
    public void init() {
        this.discoverer = new EtcdDiscoverer(URI.create("http://localhost:4001/"));
        this.changeHandler = new EtcdChangeHandler(discoverer);
    }

    @Test
    public void testChanges() {
        try {
            boolean[] catchupAdded = {false};
            boolean[] catchprop1Added = {false};
            boolean[] catchprop1Set = {false};

            this.changeHandler.start();

            // REGISTER INSTANCE THAT NEEDS TO BE REPLAYED
            Map<String, String> catchupProperties = new HashMap<>();
            catchupProperties.put("catchprop1", "catchupval1");
            try {
                this.discoverer.register(new Instance("catchup", "group", "hello!", catchupProperties, false));
            } catch (DuplicateName | IOException duplicateName) {
                duplicateName.printStackTrace();
            }

            List<DiscoveryHandler<AddedNode>> addHandlers = new ArrayList<>();
            List<DiscoveryHandler<ChangedValue<String>>> changedValueHandlers = new ArrayList<>();
            List<DiscoveryHandler<RemovedNode<String>>> removedHandlers = new ArrayList<>();

            addHandlers.add((AddedNode addedNode) -> {
                System.out.println(" Added: " + addedNode.getKey());
                switch(addedNode.getKey()) {
                    case "/instances/catchup":
                        Assert.assertFalse(catchupAdded[0]);
                        catchupAdded[0] = true;
                        break;
                    case "/instances/catchup/group/hello!/catchprop1":
                        Assert.assertFalse(catchprop1Added[0]);
                        catchprop1Added[0] = true;
                        break;
                }
            });

            changedValueHandlers.add((ChangedValue<String> changedValue) -> {
                System.out.println(" Changed: " + changedValue.getKey() + " " + changedValue.getOldValue() + " " + changedValue.getNewValue());
                switch(changedValue.getKey()) {
                    case "/instances/catchup/group/hello!/catchprop1":
                        Assert.assertFalse(catchprop1Set[0]);
                        catchprop1Set[0] = true;

                        Assert.assertEquals(null, changedValue.getOldValue());
                        Assert.assertEquals("catchupval1", changedValue.getNewValue());
                        break;
                }
            });

            removedHandlers.add((RemovedNode<String> removedNode) -> {
                Assert.assertEquals("/newNode", removedNode.getKey());
                Assert.assertEquals("newvalue!", removedNode.getValue());
            });

            this.changeHandler.addHandlers(true, addHandlers, changedValueHandlers, removedHandlers);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Assert.assertTrue(catchupAdded[0]);
            Assert.assertTrue(catchprop1Added[0]);
            Assert.assertTrue(catchprop1Set[0]);
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
        /*try {
            this.discoverer.unregisterAll();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
