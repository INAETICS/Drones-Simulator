package org.inaetics.dronessimulator.visualisation;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

/**
 * Created by langstra on 22-5-17.
 */
public abstract class BaseEntity {

    ImageView imageView;

    D3Vector position;
    D3PoolCoordinate direction;

    double spriteX;
    double spriteY;
    double spriteZ;

    Pane layer;

    int width;

    BaseEntity(Pane layer, String image) {
        this.layer = layer;

        this.width = Settings.DRONE_WIDTH;
        this.imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
        this.imageView.setPreserveRatio(true);
        addToLayer();
    }

    void addToLayer() {
        this.layer.getChildren().addAll(this.imageView);
    }

    void updateUI() {
        setSpriteX(position.getX() - imageView.getFitWidth() / 2);
        setSpriteY(position.getY() - imageView.getFitHeight() / 2);
        imageView.relocate(getSpriteX(), getSpriteY());
        imageView.setRotate(getRotation());
        imageView.setScaleX(getScale());
        imageView.setScaleY(getScale());
    }

    double getSpriteX() {
        return spriteX;
    }

    double getSpriteY() {
        return spriteY;
    }

    double getSpriteZ() {
        return spriteZ;
    }

    void setSpriteX(double x) {
        spriteX = x;
    }

    void setSpriteY(double y) {
        spriteY = y;
    }

    void setSpriteZ(double z) {
        spriteZ = z;
    }

    double getScale() {
        return scale(position.getZ(), 0, 1000, 0.1, 1.0);
    }

    void setPosition(D3Vector position) {
        this.position = position;
    }

    void setDirection(D3PoolCoordinate direction) {
        this.direction = direction;
    }

    private double getRotation() {
        return this.direction.getAngle1Degrees();
    }

    public static double scale(final double valueIn, final double baseMin, final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }
}
