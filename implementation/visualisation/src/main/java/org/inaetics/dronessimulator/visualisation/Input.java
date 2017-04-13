package org.inaetics.dronessimulator.visualisation;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;

import java.util.BitSet;

/**
 * Created by langstra on 10-3-17.
 */
public class Input {

    /**
     * Bitset which registers if any {@link KeyCode} keeps being pressed or if it is released.
     */
    private BitSet keyboardBitSet = new BitSet();

    // -------------------------------------------------
    // default key codes
    // will vary when you let the user customize the key codes or when you add support for a 2nd player
    // -------------------------------------------------

    private KeyCode upKey = KeyCode.UP;
    private KeyCode downKey = KeyCode.DOWN;
    private KeyCode leftKey = KeyCode.LEFT;
    private KeyCode rightKey = KeyCode.RIGHT;
    private KeyCode ascendKey = KeyCode.PAGE_UP;
    private KeyCode descendKey = KeyCode.PAGE_DOWN;
    private KeyCode aKey = KeyCode.A;
    private KeyCode sKey = KeyCode.S;
    private KeyCode primaryWeaponKey = KeyCode.SPACE;
    private KeyCode secondaryWeaponKey = KeyCode.CONTROL;

    private D3Vector position;
    private D3PoolCoordinate direction;
    private D3Vector velocity;
    private D3Vector acceleration;

    Scene scene;

    public Input(Scene scene) {
        this.scene = scene;
        this.position = new D3Vector(0, 0, 0);
        this.acceleration = new D3Vector(0, 0, 0);
        this.direction = new D3PoolCoordinate(0, 0, 1);
        this.velocity = new D3Vector(0, 0, 0);
    }

    public void addListeners() {

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedEventHandler);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedEventHandler);

    }

    public void removeListeners() {

        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressedEventHandler);
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleasedEventHandler);

    }

    /**
     * "Key Pressed" handler for all input events: register pressed key in the bitset
     */
    private EventHandler<KeyEvent> keyPressedEventHandler = new EventHandler<KeyEvent>() {
        public void handle(KeyEvent event) {

            // register key down
            keyboardBitSet.set(event.getCode().ordinal(), true);

        }
    };

    /**
     * "Key Released" handler for all input events: unregister released key in the bitset
     */
    private EventHandler<KeyEvent> keyReleasedEventHandler = new EventHandler<KeyEvent>() {
        public void handle(KeyEvent event) {

            // register key up
            keyboardBitSet.set(event.getCode().ordinal(), false);

        }
    };


    // -------------------------------------------------
    // Evaluate bitset of pressed keys and return the player input.
    // If direction and its opposite direction are pressed simultaneously, then the direction isn't handled.
    // -------------------------------------------------

    public boolean isMoveUp() {
        return keyboardBitSet.get(upKey.ordinal()) && !keyboardBitSet.get(downKey.ordinal());
    }

    public boolean isMoveDown() {
        return keyboardBitSet.get(downKey.ordinal()) && !keyboardBitSet.get(upKey.ordinal());
    }

    public boolean isMoveLeft() {
        return keyboardBitSet.get(leftKey.ordinal()) && !keyboardBitSet.get(rightKey.ordinal());
    }

    public boolean isMoveRight() {
        return keyboardBitSet.get(rightKey.ordinal()) && !keyboardBitSet.get(leftKey.ordinal());
    }

    public boolean isTurnLeft() {
        return keyboardBitSet.get(aKey.ordinal()) && !keyboardBitSet.get(sKey.ordinal());
    }

    public boolean isTurnRight() {
        return keyboardBitSet.get(sKey.ordinal()) && !keyboardBitSet.get(aKey.ordinal());
    }

    public boolean isAscending() {
        return keyboardBitSet.get(ascendKey.ordinal()) && !keyboardBitSet.get(descendKey.ordinal());
    }

    public boolean isDescending() {
        return keyboardBitSet.get(descendKey.ordinal()) && !keyboardBitSet.get(ascendKey.ordinal());
    }

    public boolean isFirePrimaryWeapon() {
        return keyboardBitSet.get(primaryWeaponKey.ordinal());
    }

    public boolean isFireSecondaryWeapon() {
        return keyboardBitSet.get(secondaryWeaponKey.ordinal());
    }

    public void processInput() {

        // ------------------------------------
        // movement
        // ------------------------------------

        // vertical direction
        if (isMoveUp()) {
            this.position = position.add(new D3Vector(0, -2, 0));
        } else if (isMoveDown()) {
            this.position = position.add(new D3Vector(0, 2, 0));
        } else {
        }

        // horizontal direction
        if (isMoveLeft()) {
            this.position = position.add(new D3Vector(-2, 0, 0));
        } else if (isMoveRight()) {
            this.position = position.add(new D3Vector(2, 0, 0));
        }

        if (isTurnLeft()) {
            this.direction = this.direction.rotate(-0.05, 0);
        } else if (isTurnRight()) {
            this.direction = this.direction.rotate(0.05, 0);
        }

        if (isAscending()) {
            this.position = position.add(new D3Vector(0, 0, 1));
        } else if (isDescending()) {
            this.position = position.add(new D3Vector(0, 0, -1));
        }

    }

    /**
     * Returns the position of the object from the pubsub
     * @return D3Vector position of the object
     */
    public D3Vector getPosition() {
        return this.position;
    }

    /**
     * Returns the direction of the object from the pubsub
     * @return D3PoolCoordinate direction of the object
     */
    public D3PoolCoordinate getDirection() {
        return this.direction;
    }

//    public D3Vector getAcceleration() {
//        return this.acceleration;
//    }
//
//    public D3Vector getVelocity() {
//        return this.velocity;
//    }

    public boolean destroyDrone() { return false; }

}
