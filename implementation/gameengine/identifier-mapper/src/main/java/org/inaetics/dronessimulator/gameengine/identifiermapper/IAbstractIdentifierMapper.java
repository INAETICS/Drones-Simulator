package org.inaetics.dronessimulator.gameengine.identifiermapper;

public interface IAbstractIdentifierMapper<I1, I2> {
    I2 fromOneToTwo(I1 id);
    I1 fromTwoToOne(I2 id);

    void setMapping(I1 id1, I2 id2);
}
