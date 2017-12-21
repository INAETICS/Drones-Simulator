package org.inaetics.dronessimulator.gameengine.test;

import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapperService;
import org.inaetics.dronessimulator.test.concurrent.ConcurrentExecute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TestGameIdentifierMapper {
    private IdentifierMapper id_mapper;

    @Before
    public void init() {
        this.id_mapper = new IdentifierMapperService();
    }

    @Test
    public void testConcurrent() {
        ConcurrentExecute concurrentAddAndGenerate = new ConcurrentExecute(100);
        int amount = 30000;
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

        concurrentAddAndGenerate.addJob(amount, 10, (i) -> id_mapper.setMapping(i, Integer.toString(i)));

        concurrentAddAndGenerate.addJob(amount, 10, (i) -> queue.add(id_mapper.getNewGameEngineId()));

        concurrentAddAndGenerate.start();
        concurrentAddAndGenerate.waitTillDone();

        for(int i = 0; i < amount; i++) {
            Assert.assertEquals(Integer.toString(i), id_mapper.fromGameEngineToProtocolId(i).get());
            Assert.assertEquals(new Integer(i), id_mapper.fromProtocolToGameEngineId(Integer.toString(i)).get());
            Assert.assertTrue("Created id: " + i, queue.contains(i + 1));
        }

        Assert.assertEquals(amount, queue.size());

        ConcurrentExecute concurrentRemove = new ConcurrentExecute(100);

        concurrentRemove.addJob(amount, 10, (i) -> id_mapper.removeMapping(i));

        concurrentRemove.start();
        concurrentRemove.waitTillDone();

        for(int i = 0; i < amount; i++) {
            Assert.assertFalse(id_mapper.fromProtocolToGameEngineId(Integer.toString(i)).isPresent());
            Assert.assertFalse(id_mapper.fromGameEngineToProtocolId(i).isPresent());
        }
    }
}
