package org.inaetics.dronessimulator.discovery.api.test;


import org.inaetics.dronessimulator.discovery.api.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryDirNode;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryValueNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class TestDiscoveryDiscoveryTree {
    private DiscoveryDirNode root;

    @Before
    public void init() {
        this.root = new DiscoveryDirNode("/");
    }


    @Test
    public void testAddChildSetValue() {
        AddedNode addEvent = this.root.addChildWithEvent(new DiscoveryValueNode("/firstChild"));

        this.root.getChild("/firstChild").addChangeValueHandler((ChangedValue<String> changedValue) -> {
            Assert.assertEquals("/firstChild", changedValue.getKey());
            Assert.assertEquals(null, changedValue.getOldValue());
            Assert.assertEquals("value1", changedValue.getNewValue());
        });

        Optional<ChangedValue<String>> m_changeEvent = this.root.getChild("/firstChild").setValueWithEvent("value1");

        this.root.addAddNodeHandler((AddedNode addedNode) -> {
            Assert.assertEquals(addEvent, addedNode);
        });

        Assert.assertEquals("/firstChild", addEvent.getKey());

        Assert.assertTrue(m_changeEvent.isPresent());

        ChangedValue<String> changeEvent = m_changeEvent.get();

        Assert.assertEquals("/firstChild", changeEvent.getKey());
        Assert.assertEquals(null, changeEvent.getOldValue());
        Assert.assertEquals("value1", changeEvent.getNewValue());
    }

    @Test
    public void testAddDirAndChildSetValue() {
        DiscoveryDirNode firstDir = new DiscoveryDirNode("/firstDir");

        this.root.addAddNodeHandler((AddedNode addedDir) -> {
            if(addedDir.getKey().equals("/firstDir")) {
                // Assert true;
            } else {
                assert false;
            }
        });

        firstDir.addAddNodeHandler((AddedNode addedValue) -> {
            Assert.assertEquals("/firstDir/firstValue", addedValue.getKey());
        });

        firstDir.addChildWithEvent(new DiscoveryValueNode("/firstDir/firstValue"));



        AddedNode addEvent = this.root.addChildWithEvent(firstDir);

        Optional<ChangedValue<String>> m_changeEvent = this.root.getChild("/firstDir").getChild("/firstDir/firstValue").setValueWithEvent("value1");

        Assert.assertEquals("/firstDir", addEvent.getKey());
        Assert.assertTrue(m_changeEvent.isPresent());

        ChangedValue<String> changeEvent = m_changeEvent.get();

        Assert.assertEquals("/firstDir/firstValue", changeEvent.getKey());
        Assert.assertEquals(null, changeEvent.getOldValue());
        Assert.assertEquals("value1", changeEvent.getNewValue());
    }

    @Test
    public void testRemoveNode() {
        DiscoveryValueNode firstChild = new DiscoveryValueNode("/firstValue");
        firstChild.setValueWithEvent("value!");
        this.root.addChild(firstChild);

        Assert.assertNotNull(this.root.getChild("/firstValue"));

        this.root.addRemoveNodeHandler((RemovedNode<String> removedNode) -> {
            Assert.assertEquals("/firstValue", removedNode.getKey());
            Assert.assertEquals("value!", removedNode.getValue());
        });

        RemovedNode<String> removeEvent = this.root.removeChildWithEvent("/firstValue");

        Assert.assertNull(this.root.getChild("/firstValue"));
        Assert.assertEquals(0, this.root.getChildren().size());

        Assert.assertEquals("/firstValue", removeEvent.getKey());
        Assert.assertEquals("value!", removeEvent.getValue());
    }

    @Test
    public void testUpdateTreeAddAndChange() {
        MockDiscoveryStoredNode storedRoot = new MockDiscoveryStoredNode("/");
        MockDiscoveryStoredNode storedExistingDir = new MockDiscoveryStoredNode("/existingDir");
        MockDiscoveryStoredNode storedChangedValue = new MockDiscoveryStoredNode("/existingDir/changedValue", "changedValue");
        MockDiscoveryStoredNode storedNewDir = new MockDiscoveryStoredNode("/existingDir/newDir");
        MockDiscoveryStoredNode storedNewValue = new MockDiscoveryStoredNode("/existingDir/newDir/newValue", "newValue");

        storedRoot.addChild(storedExistingDir);
            storedExistingDir.addChild(storedChangedValue);
        storedRoot.addChild(storedNewDir);
            storedNewDir.addChild(storedNewValue);

        DiscoveryDirNode removedDir = new DiscoveryDirNode("/removedDir");
        DiscoveryDirNode existingDir = new DiscoveryDirNode("/existingDir");
        DiscoveryValueNode changedValue = new DiscoveryValueNode("/existingDir/changedValue");
        changedValue.setValue("unchangedValue");

        existingDir.addChild(changedValue);
        root.addChild(existingDir);
        root.addChild(removedDir);

        // Use array trick so value can be altered in lambda
        boolean[] newDirIsAdded = {false};
        boolean[] newValueIsAdded = {false};
        boolean[] newValueIsChanged = {false};
        boolean[] changedValueIsChanged = {false};
        boolean[] removedDirIsRemoved = {false};

        root.addAddNodeHandler((AddedNode addedNode) -> {
            if(addedNode.getKey().equals("/existingDir/newDir")) {
                // Assert true
                Assert.assertFalse(newDirIsAdded[0]);
                newDirIsAdded[0] = true;
            } else if(addedNode.getKey().equals("/existingDir/newDir/newValue")) {
                // Assert true
                Assert.assertFalse(newValueIsAdded[0]);
                newValueIsAdded[0] = true;
            } else {
                assert false;
            }
        });

        root.addChangeValueHandler((ChangedValue<String> changedValue_) -> {
            if(changedValue_.getKey().equals("/existingDir/newDir/newValue")) {
                Assert.assertEquals(null, changedValue_.getOldValue());
                Assert.assertEquals("newValue", changedValue_.getNewValue());
                Assert.assertFalse(newValueIsChanged[0]);
                newValueIsChanged[0] = true;
            } else if(changedValue_.getKey().equals("/existingDir/changedValue")) {
                Assert.assertEquals("unchangedValue", changedValue_.getOldValue());
                Assert.assertEquals("changedValue", changedValue_.getNewValue());
                Assert.assertFalse(changedValueIsChanged[0]);
                changedValueIsChanged[0] = true;
            } else {
                assert false;
            }
        });

        root.addRemoveNodeHandler((RemovedNode<String> removedNode) -> {
            Assert.assertEquals("/removedDir", removedNode.getKey());
            Assert.assertEquals(null, removedNode.getValue());
            Assert.assertFalse(removedDirIsRemoved[0]);
            removedDirIsRemoved[0] = true;
        });

        root.updateTree(storedRoot);

        // All handlers should have happenend and only once
        Assert.assertTrue(newDirIsAdded[0]);
        Assert.assertTrue(newValueIsAdded[0]);
        Assert.assertTrue(newValueIsChanged[0]);
        Assert.assertTrue(changedValueIsChanged[0]);
        Assert.assertTrue(removedDirIsRemoved[0]);
    }
}
