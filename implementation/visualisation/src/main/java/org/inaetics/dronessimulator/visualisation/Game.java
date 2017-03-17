package org.inaetics.dronessimulator.visualisation;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game extends Application {

    private Pane playfieldLayer;

    private List<BasicDrone> drones = new ArrayList<>();

    private Scene scene;

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        // create layers
        playfieldLayer = new Pane();
        root.getChildren().add( playfieldLayer);

        scene = new Scene( root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

        primaryStage.setScene( scene);
        primaryStage.show();

        createPlayers();

        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                // player input
                drones.forEach(sprite -> sprite.processInput());

                // movement
                drones.forEach(sprite -> sprite.move());

                // update sprites in scene
                drones.forEach(sprite -> sprite.updateUI());
            }

        };
        gameLoop.start();

    }

    private void createPlayers() {

        // drone input
        Input input = new Input(scene);

        // register input listeners
        input.addListeners(); // TODO: remove listeners on game over

        // create drone
        BasicDrone drone = new BasicDrone(playfieldLayer, 0, 0, 0, 0, Settings.PLAYER_SHIP_HEALTH, 0, Settings.PLAYER_SHIP_SPEED, Settings.PLAY_SHIP_ROTATION_SPEED, input);

        // register drone
        drones.add(drone);

    }

    private void removeSprites(  List<? extends Drone> spriteList) {
        Iterator<? extends Drone> iter = spriteList.iterator();
        while( iter.hasNext()) {
            Drone sprite = iter.next();

            if( sprite.isRemovable()) {

                // remove from layer
                sprite.removeFromLayer();

                // remove from list
                iter.remove();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
