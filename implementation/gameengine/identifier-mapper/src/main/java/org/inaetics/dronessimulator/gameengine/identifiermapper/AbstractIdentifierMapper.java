package org.inaetics.dronessimulator.gameengine.identifiermapper;

import java.util.concurrent.ConcurrentHashMap;

public class AbstractIdentifierMapper<I1, I2> implements IAbstractIdentifierMapper<I1, I2> {
    private final ConcurrentHashMap<I1, I2> oneToTwo;
    private final ConcurrentHashMap<I2, I1> twoToOne;


    public AbstractIdentifierMapper() {
        this.oneToTwo = new ConcurrentHashMap<>();
        this.twoToOne = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized I2 fromOneToTwo(I1 id) {
        return this.oneToTwo.get(id);
    }

    @Override
    public synchronized I1 fromTwoToOne(I2 id) {
        return this.twoToOne.get(id);
    }

    @Override
    public synchronized void setMapping(I1 id1, I2 id2) {
        this.oneToTwo.put(id1, id2);
        this.twoToOne.put(id2, id1);
    }
}
