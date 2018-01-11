package org.inaetics.dronessimulator.common.protocol;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Message specifying a bullet is fired
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FireBulletMessage extends CreateEntityMessage {
    /**
     * The damage of the bullet
     */
    private int damage;
    /**
     * Who fired the bullet
     */
    private String firedById;

    @Override
    public String toString() {
        return String.format("(FireBulletMessage %s fired by %s, %s)", this.getIdentifier(), this.getFiredById(), this.getDamage());
    }
}
