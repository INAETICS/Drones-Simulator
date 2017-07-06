package org.inaetics.dronessimulator.visualisation;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Provides a means of creating an animation of a sprite image
 */
public class SpriteAnimation extends Transition {
    /** Image of the animation */
    private final ImageView imageView;
    /** Number of images in the sprite */
    private final int count;
    /** Number of columns in the sprite */
    private final int columns;
    /** X offset in pixels */
    private int offsetX;
    /** Y offset in pixels */
    private int offsetY;
    /** Width on an image in the sprite */
    private final int width;
    /** height of an image in the sprite */
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

    /**
     * Change the viewport to the new image in the sprite
     * @param frac - frac
     */
    protected void interpolate(double frac) {
        final int index = Math.min((int) Math.floor(count * frac), count - 1);
        final int x = (index % columns) * width + offsetX;
        final int y = (index / columns) * height + offsetY;
        imageView.setViewport(new Rectangle2D(x, y, width, height));
    }
}
