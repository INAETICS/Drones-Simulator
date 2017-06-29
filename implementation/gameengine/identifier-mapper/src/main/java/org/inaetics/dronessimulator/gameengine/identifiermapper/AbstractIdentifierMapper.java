package org.inaetics.dronessimulator.gameengine.identifiermapper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract identifier mapper implementation.
 * @param <I1> The type of identifiers from the first system.
 * @param <I2> The type of identifiers from the second system.
 */
public class AbstractIdentifierMapper<I1, I2> implements IAbstractIdentifierMapper<I1, I2> {
    /** Mappings from system one to system two. */
    private final ConcurrentHashMap<I1, I2> oneToTwo;

    /** Mappings from system two to system one. */
    private final ConcurrentHashMap<I2, I1> twoToOne;

    /**
     * Instantiates a new abstract identifier mapper.
     */
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

    @Override
    public synchronized void removeMapping(I1 id1, I2 id2) {
        if(id1 != null) {
            this.oneToTwo.remove(id1);
        }

        if(id2 != null) {
            this.twoToOne.remove(id2);
        }
    }
}
