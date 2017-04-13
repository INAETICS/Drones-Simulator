package org.inaetics.dronessimulator.visualisation;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.inaetics.dronesimulator.common.protocol.MessageTopic;
import org.inaetics.dronesimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game extends Application {

    public volatile Subscriber subscriber;

    public Game() {
    }

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
                drones.forEach(drone -> drone.processInput());

                // update sprites in scene
                drones.forEach(drone -> drone.updateUI());
            }

        };
        gameLoop.start();

    }

    private void createPlayers() {

        // drone input
        Input input = new Input(scene, subscriber);
        this.subscriber.addHandler(StateMessage.class, input);
        try {
            this.subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {

        }

        // register input listeners
        input.addListeners();

        // create drone
        BasicDrone drone = new BasicDrone(playfieldLayer, input);

        // register drone
        drones.add(drone);

    }
    public static void main(String[] args) {
        launch(args);
    }

}
