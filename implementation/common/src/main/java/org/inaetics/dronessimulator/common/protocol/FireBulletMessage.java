package org.inaetics.dronessimulator.common.protocol;

import java.util.Objects;

/**
 * Message specifying a bullet is fired
 */
//@EqualsAndHashCode(callSuper = true)
public class FireBulletMessage extends CreateEntityMessage {
    /**
     * The damage of the bullet
     */
    private int damage;
    /**
     * Who fired the bullet
     */
    private String firedById;

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getFiredById() {
        return firedById;
    }

    public void setFiredById(String firedById) {
        this.firedById = firedById;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FireBulletMessage)) return false;
        if (!super.equals(o)) return false;
        FireBulletMessage that = (FireBulletMessage) o;
        return damage == that.damage &&
                Objects.equals(firedById, that.firedById);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), damage, firedById);
    }

    @Override
    public String toString() {
        return String.format("(FireBulletMessage %s fired by %s, %s)", this.getIdentifier(), this.getFiredById(), this.getDamage());
    }
}
