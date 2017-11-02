package org.inaetics.dronessimulator.discovery.etcd;

import mousio.etcd4j.responses.EtcdKeysResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscoveryStoredEtcdNodeTest {
    private DiscoveryStoredEtcdNode storedNode;
    private DiscoveryStoredEtcdNode storedNodeDir;
    private EtcdKeysResponse.EtcdNode etcdNode;

    @Before
    public void setup() throws Exception {
        EtcdKeysResponse.EtcdNode etcdNodeChild = mock(EtcdKeysResponse.EtcdNode.class);
        when(etcdNodeChild.getKey()).thenReturn("etcdNodeChildKey");
        when(etcdNodeChild.getValue()).thenReturn("etcdNodeChildValue");
        when(etcdNodeChild.isDir()).thenReturn(false);

        etcdNode = mock(EtcdKeysResponse.EtcdNode.class);
        when(etcdNode.getKey()).thenReturn("etcdNodeKey");
        when(etcdNode.getValue()).thenReturn("etcdNodeValue");
        when(etcdNode.isDir()).thenReturn(true);
        when(etcdNode.getNodes()).thenReturn(Collections.singletonList(etcdNodeChild));

        EtcdKeysResponse.EtcdNode etcdNodeDir = mock(EtcdKeysResponse.EtcdNode.class);
        when(etcdNodeDir.isDir()).thenReturn(true);
        when(etcdNodeDir.getNodes()).thenReturn(Collections.singletonList(etcdNode));

        storedNode = new DiscoveryStoredEtcdNode(etcdNode);
        storedNodeDir = new DiscoveryStoredEtcdNode(etcdNodeDir);
    }

    @Test
    public void testGetId() throws Exception {
        Assert.assertEquals("etcdNodeKey", storedNode.getId());
    }

    @Test
    public void testGetValues() throws Exception {
        HashMap<String, String> expectedMap = new HashMap<>();
        expectedMap.put("etcdNodeChildKey", "etcdNodeChildValue");
        Assert.assertEquals(expectedMap, storedNode.getValues());
    }

    @Test
    public void testGetChildren() throws Exception {
        ArrayList<DiscoveryStoredEtcdNode> expectedList = new ArrayList<>();
        expectedList.add(new DiscoveryStoredEtcdNode(etcdNode));
        Assert.assertThat(expectedList, CoreMatchers.hasItems(storedNodeDir.getChildren().toArray(new DiscoveryStoredEtcdNode[]{})));
    }

    @Test
    public void testGetNodeName() throws Exception {
        Assert.assertEquals("", DiscoveryStoredEtcdNode.getNodeName(null));
        Assert.assertEquals("a", DiscoveryStoredEtcdNode.getNodeName("/a"));
        Assert.assertEquals("b", DiscoveryStoredEtcdNode.getNodeName("/a/b"));
        Assert.assertEquals("", DiscoveryStoredEtcdNode.getNodeName("/a/"));
        Assert.assertEquals("", DiscoveryStoredEtcdNode.getNodeName(""));
    }

}