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
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Game extends Application implements MessageHandler {
    private volatile RabbitSubscriber subscriber;

    public Game() {
    }

    private Pane playfieldLayer;

    private Map<String, Drone> drones = new HashMap<>();

    private int i = 0;
    private long lastLog = -1;

    /**
     * Main entry point for a JavaFX application
     * @param primaryStage - the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {

        setupRabbit();
        setupInterface(primaryStage);


        // For testing
        // todo: REMOVE!!!!
        createPlayer("createRandomDrone");

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

                // update sprites in scene
                drones.forEach((id, drone) -> drone.updateUI());
            }

        };
        gameLoop.start();
    }

    /**
     * Message handler for the pubsub
     * Changes the position and direction based on the stateMessage
     *
     * @param message The received message.
     */
    public synchronized void handleMessage(Message message) {

        if (message instanceof StateMessage) {
            StateMessage stateMessage = (StateMessage) message;
            Drone currentDrone;

            if (!stateMessage.getIdentifier().isPresent()) {
                return;
            }
            currentDrone = drones.getOrDefault(stateMessage.getIdentifier().get(), createPlayer(stateMessage.getIdentifier().get()));

            if (stateMessage.getPosition().isPresent()) {
                currentDrone.setPosition(stateMessage.getPosition().get());
            }

            if (stateMessage.getDirection().isPresent()) {
                currentDrone.setDirection(stateMessage.getDirection().get());
            }
        } else {
            Logger.getLogger(this.getClass()).info("Received non-state msg: " + message);
        }
    }

    /**
     * Creates a new drone and returns it
     * @param id String - Identifier of the new drone
     * @return drone Drone - The newly created drone
     */
    private Drone createPlayer(String id) {
        this.subscriber.addHandler(StateMessage.class, this);
        try {
            this.subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create drone
        BasicDrone drone = new BasicDrone(playfieldLayer);

        drone.setPosition(new D3Vector(0, 0, 0));
        drone.setDirection(new D3PoolCoordinate(0, 0, 0));

        // register drone
        drones.put(id, drone);

        return drone;
    }

    private void setupRabbit() {
        if (this.subscriber == null) {
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

    private void setupInterface(Stage primaryStage) {
        StackPane root = new StackPane();

        // create layers
        playfieldLayer = new Pane();
        root.getChildren().add(playfieldLayer);
        root.setId("pane");

        Scene scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
        scene.getStylesheets().addAll(this.getClass().getResource("/style.css").toExternalForm());


        //todo: Remove button
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setLayoutX(750);
        btn.setLayoutY(50);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createPlayer("someRandomString");
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
