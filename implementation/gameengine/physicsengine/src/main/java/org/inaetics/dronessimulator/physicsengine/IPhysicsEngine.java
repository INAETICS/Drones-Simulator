package org.inaetics.dronessimulator.physicsengine;

import org.inaetics.dronessimulator.physicsengine.entityupdate.EntityUpdate;

import java.util.Collection;
import java.util.List;

/**
 * Created by sebastiaan on 3-5-17.
 */
public interface IPhysicsEngine {
    void setObserver(PhysicsEngineEventObserver engineObserver);

    void setTimeBetweenBroadcastms(long i);

    void addInsert(Entity entity);

    void addUpdate(Integer entityId, EntityUpdate entityUpdate);

    void addUpdates(Integer entityId, Collection<EntityUpdate> collect);

    void addRemoval(Integer entityId);

    void addRemovals(Collection<Integer> entityIds);
}
