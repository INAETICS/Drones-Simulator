package org.inaetics.dronessimulator.discovery.api.test;


import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.mocks.MockDiscoveryStoredNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestDiscoveryDiscoveryTree {
    private DiscoveryNode root;

    @Before
    public void init() {
        this.root = new DiscoveryNode("/", null, DiscoveryPath.ROOT_PATH);
    }

    @Test
    public void testSetNewValue() {
        boolean[] valueSet = {false};
        this.root.addChild(new DiscoveryNode("child1"));

        this.root.addChangeValueHandler((ChangedValue event) -> {
            Assert.assertEquals(DiscoveryPath.ROOT_PATH.addSegments("child1"), event.getNode().getPath());
            Assert.assertEquals("valueKey", event.getKey());
            Assert.assertEquals(null, event.getOldValue());
            Assert.assertEquals("initValue", event.getNewValue());

            Assert.assertFalse(valueSet[0]);
            valueSet[0] = true;
        });

        this.root.getChild("child1").setValueWithEvent("valueKey", "initValue");

        Assert.assertTrue(valueSet[0]);
    }

    @Test
    public void testAddChild() {
        boolean[] addedChild = {false};
        DiscoveryNode child = new DiscoveryNode("child");

        this.root.addAddNodeHandler((AddedNode addedNode) -> {
            Assert.assertEquals(child, addedNode.getNode());

            Assert.assertFalse(addedChild[0]);
            addedChild[0] = true;
        });

        this.root.addChildWithEvent(child);

        Assert.assertTrue(addedChild[0]);
    }

    @Test
    public void testRemoveNode() {
        boolean[] removedChild = {false};
        DiscoveryNode child = new DiscoveryNode("child1");
        this.root.addChild(child);

        this.root.addRemoveNodeHandler((RemovedNode removedNode) -> {
            Assert.assertEquals(child, removedNode.getNode());

            Assert.assertFalse(removedChild[0]);
            removedChild[0] = true;
        });

        this.root.removeChildWithEvent("child1");

        Assert.assertTrue(removedChild[0]);
    }

    @Test
    public void testUpdateTreeAddChangeAndRemove() {
        MockDiscoveryStoredNode storedRoot = new MockDiscoveryStoredNode("/");

        // Setup an existing stored node with a changed value and a new value
        Map<String, String> existingValues = new HashMap<>();
        existingValues.put("existingKey", "newValue1");
        existingValues.put("newKey1", "newValue2");
        MockDiscoveryStoredNode storedExistingNode = new MockDiscoveryStoredNode("existingNode", existingValues);
        storedRoot.addChild(storedExistingNode);

        // Setup a new stored node with a new value
        Map<String, String> newValues = new HashMap<>();
        newValues.put("newKey2", "newValue3");
        MockDiscoveryStoredNode storedNewNode = new MockDiscoveryStoredNode("newNode", newValues);
        storedRoot.addChild(storedNewNode);


        // Setup the existing discovered node with the existing value and the removed value
        DiscoveryNode existingNode = new DiscoveryNode("existingNode");
        existingNode.setValue("existingKey", "oldValue1");
        existingNode.setValue("removedKey1", "removedValue1");

        this.root.addChild(existingNode);

        // Setup the removed node with removed value
        DiscoveryNode removedNode_ = new DiscoveryNode("removedNode");
        removedNode_.setValue("removedKey2", "removedValue2");

        existingNode.addChild(removedNode_);


        // Use array trick so value can be altered in lambda
        boolean[] valueChanged = {false};
        boolean[] value1Added = {false};
        boolean[] newNodeIsAdded = {false};
        boolean[] value2Added = {false};
        boolean[] value1Removed = {false};
        boolean[] value2Removed = {false};
        boolean[] nodeRemoved = {false};


        root.addAddNodeHandler((AddedNode addedNodeEvent) -> {
            DiscoveryNode addedNode = addedNodeEvent.getNode();
            Assert.assertEquals("newNode", addedNode.getId());

            Assert.assertFalse(newNodeIsAdded[0]);
            newNodeIsAdded[0] = true;
        });

        root.addChangeValueHandler((ChangedValue changedValue) -> {
            switch(changedValue.getKey()) {
                case "existingKey":
                    Assert.assertEquals("oldValue1", changedValue.getOldValue());
                    Assert.assertEquals("newValue1", changedValue.getNewValue());
                    Assert.assertEquals(existingNode, changedValue.getNode());

                    Assert.assertFalse(valueChanged[0]);
                    valueChanged[0] = true;
                    break;
                case "newKey1":
                    Assert.assertEquals(null, changedValue.getOldValue());
                    Assert.assertEquals("newValue2", changedValue.getNewValue());
                    Assert.assertEquals(existingNode, changedValue.getNode());

                    Assert.assertFalse(value1Added[0]);
                    value1Added[0] = true;
                    break;

                case "newKey2":
                    Assert.assertEquals(null, changedValue.getOldValue());
                    Assert.assertEquals("newValue3", changedValue.getNewValue());

                    Assert.assertFalse(value2Added[0]);
                    value2Added[0] = true;
                    break;

                case "removedKey1":
                    Assert.assertEquals("removedValue1", changedValue.getOldValue());
                    Assert.assertEquals(null, changedValue.getNewValue());
                    Assert.assertEquals(existingNode, changedValue.getNode());

                    Assert.assertFalse(value1Removed[0]);
                    value1Removed[0] = true;
                    break;

                case "removedKey2":
                    Assert.assertEquals("removedValue2", changedValue.getOldValue());
                    Assert.assertEquals(null, changedValue.getNewValue());
                    Assert.assertEquals(removedNode_, changedValue.getNode());

                    Assert.assertFalse(value2Removed[0]);
                    value2Removed[0] = true;
                    break;
                default:
                    assert false;

            }
        });

        root.addRemoveNodeHandler((RemovedNode removedNode) -> {
            Assert.assertEquals(removedNode_, removedNode.getNode());

            Assert.assertFalse(nodeRemoved[0]);
            nodeRemoved[0] = true;
        });

        try {
        root.updateTree(storedRoot);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // All handlers should have happenend and only once
        Assert.assertTrue(valueChanged[0]);
        Assert.assertTrue(value1Added[0]);
        Assert.assertTrue(newNodeIsAdded[0]);
        Assert.assertTrue(value2Added[0]);
        Assert.assertTrue(value1Removed[0]);
        Assert.assertTrue(value2Removed[0]);
        Assert.assertTrue(nodeRemoved[0]);
    }
}
