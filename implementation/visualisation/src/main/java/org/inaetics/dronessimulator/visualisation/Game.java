package org.inaetics.dronessimulator.visualisation;

import com.rabbitmq.client.ConnectionFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;
import org.inaetics.dronessimulator.visualisation.messagehandlers.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Game extends Application {
    private volatile RabbitSubscriber subscriber;

    private KillMessageHandler killMessageHandler;
    private StateMessageHandler stateMessageHandler;

    public Game() {
    }

    private Pane playfieldLayer;

    private final ConcurrentMap<String, Drone> drones = new ConcurrentHashMap<>();

    private int i = 0;
    private long lastLog = -1;

    /**
     * Main entry point for a JavaFX application
     *
     * @param primaryStage - the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        setupRabbit();
        setupInterface(primaryStage);

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

    private void setupRabbit() {
        if (this.subscriber == null) {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setUsername("yourUser");
            connectionFactory.setPassword("yourPass");
            // We can connect to localhost, since the visualization does not run within Docker
            this.subscriber = new RabbitSubscriber(connectionFactory, "visualisation", new JavaSerializer());

            try {
                this.subscriber.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.stateMessageHandler = new StateMessageHandler(this.playfieldLayer, this.drones);
        this.killMessageHandler = new KillMessageHandler(this.drones);

        this.subscriber.addHandler(CollisionMessage.class, new CollisionMessageHandler());
        this.subscriber.addHandler(DamageMessage.class, new DamageMessageHandler());
        this.subscriber.addHandler(FireBulletMessage.class, new FireBulletMessageHandler());
        this.subscriber.addHandler(KillMessage.class, this.killMessageHandler);
        this.subscriber.addHandler(StateMessage.class, this.stateMessageHandler);

        try {
            this.subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            e.printStackTrace();
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

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
