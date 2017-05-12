package org.inaetics.dronessimulator.visualisation;

import com.rabbitmq.client.ConnectionFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends Application implements MessageHandler {
    private volatile RabbitSubscriber subscriber;


    public Game() {
    }

    private Pane playfieldLayer;

    private Map<String, BasicDrone> drones = new HashMap<>();

    private Scene scene;

    private int i = 0;
    private long lastLog = -1;

    @Override
    public void start(Stage primaryStage) {

        setupRabbit();
        setupInterface(primaryStage);


        createPlayer();

        lastLog = System.currentTimeMillis();

        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                i++;
                if (i == 100) {
                    long current = System.currentTimeMillis();
                    float durationAverageMs = ((float) (current - lastLog)) / 100f;
                    float fps = 1000f / durationAverageMs;


                    lastLog = current;
                    System.out.println("Average: " + durationAverageMs);
                    System.out.println("FPS: " + fps);
                    i = 0;
                }

                // player input
                drones.forEach((id, drone) -> drone.processInput());

                // update sprites in scene
                drones.forEach((id, drone) -> drone.updateUI());
            }

        };
        gameLoop.start();
    }

    /**
     * Message handler for the pubsub
     * Changes the position and direction based on the stateMessage
     * @param message The received message.
     */
    public synchronized void handleMessage(Message message) {

        if(message instanceof StateMessage) {
            StateMessage stateMessage = (StateMessage) message;
            if (drones.get(message.getId()))

            if (stateMessage.getPosition().isPresent()) {

                this.position = stateMessage.getPosition().get();
                //System.out.println("New position: " + this.position);
            }
            if (stateMessage.getDirection().isPresent()) {
                this.direction = stateMessage.getDirection().get();
                //System.out.println("New direction: " + this.direction);
            }
        } else {
            Logger.getLogger(Input.class).info("Received non-state msg: " + message);
        }
    }

    private void createPlayer() {
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

    public void setupRabbit() {
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
    }

    public void setupInterface(Stage primaryStage) {
        StackPane root = new StackPane();

        // create layers
        playfieldLayer = new Pane();
        root.getChildren().add(playfieldLayer);
        root.setId("pane");

        scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
        scene.getStylesheets().addAll(this.getClass().getResource("/style.css").toExternalForm());

        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setLayoutX(750);
        btn.setLayoutY(50);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createPlayer();
            }
        });

        root.getChildren().add(btn);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
