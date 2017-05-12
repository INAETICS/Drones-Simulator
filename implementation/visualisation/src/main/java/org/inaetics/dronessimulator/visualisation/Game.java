package org.inaetics.dronessimulator.visualisation;

import com.rabbitmq.client.ConnectionFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game extends Application {
    private volatile RabbitSubscriber subscriber;


    public Game() {
    }

    private Pane playfieldLayer;

    private List<BasicDrone> drones = new ArrayList<>();

    private Scene scene;

    private int i = 0;
    private long lastLog = -1;

    @Override
    public void start(Stage primaryStage) {
        if(this.subscriber == null) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            // We can connect to localhost, since the visualization does not run within Docker
            this.subscriber = new RabbitSubscriber(connectionFactory, "visualisation", new JavaSerializer());

            try {
                this.subscriber.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Group root = new Group();

        // create layers
        playfieldLayer = new Pane();
        root.getChildren().add(playfieldLayer);

        scene = new Scene( root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();


        createPlayers();

        lastLog = System.currentTimeMillis();

        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                i++;
                if(i == 100) {
                    long current = System.currentTimeMillis();
                    float durationAverageMs = ((float) (current - lastLog)) / 100f;
                    float fps = 1000f / durationAverageMs;


                    lastLog = current;
                    System.out.println("Average: " + durationAverageMs);
                    System.out.println("FPS: " + fps);
                    i = 0;
                }

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
            e.printStackTrace();
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
