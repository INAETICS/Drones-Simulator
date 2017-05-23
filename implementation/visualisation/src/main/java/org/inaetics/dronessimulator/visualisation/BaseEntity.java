package org.inaetics.dronessimulator.visualisation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

public abstract class BaseEntity {

    ImageView imageView;

    D3Vector position;
    private D3PoolCoordinate direction;

    private double spriteX;
    private double spriteY;
    private double spriteZ;

    Pane pane;

    BaseEntity(Pane pane, String image) {
        this.pane = pane;
        this.imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
        this.imageView.setPreserveRatio(true);
        addToLayer();
    }

    public void addToLayer() {
        this.pane.getChildren().addAll(this.imageView);
    }

    /**
     * Update the position, rotation and scale of the entity
     */
    void updateUI() {
        setSpriteX(position.getX() - imageView.getFitWidth() / 2);
        setSpriteY(position.getY() - imageView.getFitHeight() / 2);
        imageView.relocate(getSpriteX(), getSpriteY());
        imageView.setRotate(getRotation());
        imageView.setScaleX(getScale());
        imageView.setScaleY(getScale());
    }

    public void delete() {
        this.pane.getChildren().remove(this.imageView);
    }

    public double getSpriteX() {
        return spriteX;
    }

    public double getSpriteY() {
        return spriteY;
    }

    public double getSpriteZ() {
        return spriteZ;
    }

    private void setSpriteX(double x) {
        spriteX = x;
    }

    private void setSpriteY(double y) {
        spriteY = y;
    }

    private void setSpriteZ(double z) {
        spriteZ = z;
    }

    public double getScale() {
        return scale(position.getZ(), 0, 1000, 0.1, 1.0);
    }

    public void setPosition(D3Vector position) {
        this.position = position;
    }

    public void setDirection(D3PoolCoordinate direction) {
        this.direction = direction;
    }

    private double getRotation() {
        return this.direction.getAngle1Degrees();
    }

    public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }
}
