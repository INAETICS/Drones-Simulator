package org.inaetics.dronessimulator.common.protocol;

public class FireBulletMessage extends CreateEntityMessage {
    private int damage;
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

    public String toString() {
        return String.format("(FireBulletMessage %s fired by %s, %s)", this.getIdentifier(), this.getFiredById(), this.getDamage());
    }
}
