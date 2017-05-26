package org.inaetics.dronessimulator.visualisation;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SpriteAnimation extends Transition {
    private final ImageView imageView;
    private final int count;
    private final int columns;
    private int offsetX;
    private int offsetY;
    private final int width;
    private final int height;

    /**
     * Creates an animation that will run for indefinite time.
     * Use setCycleCount(1) to run animation only once. Remember to remove the imageView afterwards.
     *
     * @param imageView - the imageview of the sprite
     * @param duration - How long should one animation cycle take
     * @param count - Number of frames
     * @param columns - Number of colums the sprite has
     * @param offsetX - Offset x
     * @param offsetY - Offset y
     * @param width - Width of each frame
     * @param height - Height of each frame
     */
    public SpriteAnimation(
            ImageView imageView,
            Duration duration,
            int count, int columns,
            int offsetX, int offsetY,
            int width, int height
    ) {
        this.imageView = imageView;
        this.count = count;
        this.columns = columns;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        setCycleDuration(duration);
        setCycleCount(Animation.INDEFINITE);
        setInterpolator(Interpolator.LINEAR);
        this.imageView.setViewport(new Rectangle2D(offsetX, offsetY, width, height));

    }

    public void setOffsetX(int x) {
        this.offsetX = x;
    }

    public void setOffsetY(int y) {
        this.offsetY = y;
    }

    protected void interpolate(double frac) {
        final int index = Math.min((int) Math.floor(count * frac), count - 1);
        final int x = (index % columns) * width + offsetX;
        final int y = (index / columns) * height + offsetY;
        imageView.setViewport(new Rectangle2D(x, y, width, height));
    }
}
