package org.inaetics.dronessimulator.visualisation;

import com.rabbitmq.client.ConnectionFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;
import org.inaetics.dronessimulator.visualisation.messagehandlers.*;
import org.inaetics.dronessimulator.visualisation.controls.NodeGestures;
import org.inaetics.dronessimulator.visualisation.controls.PannableCanvas;
import org.inaetics.dronessimulator.visualisation.controls.SceneGestures;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Game extends Application {
    private volatile RabbitSubscriber subscriber;
    private static final Logger logger = Logger.getLogger(Game.class);

    private final ConcurrentMap<String, BaseEntity> entities = new ConcurrentHashMap<>();

    private PannableCanvas canvas;

    private final BlockingQueue<UIUpdate> uiUpdates;

    public Game() {
        this.uiUpdates = new LinkedBlockingQueue<>();
    }

    private int i = 0;
    private long lastLog = -1;

    /**
     * Main entry point for a JavaFX application
     *
     * @param primaryStage - the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        setupInterface(primaryStage);
        setupRabbit();
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

                    logger.info("Average: " + durationAverageMs);
                    logger.info("FPS: " + fps);
                    i = 0;
                }

                while(!uiUpdates.isEmpty()) {
                    try {
                        UIUpdate uiUpdate = uiUpdates.take();
                        uiUpdate.execute(canvas);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // update sprites in scene
                entities.forEach((id, entity) -> entity.updateUI());
            }

        };
        gameLoop.start();
    }

    /**
     * Sets up the connection to the message broker and subscribes to the necessary channels and sets the required handlers
     */
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

        this.subscriber.addHandler(CollisionMessage.class, new CollisionMessageHandler());
        this.subscriber.addHandler(DamageMessage.class, new DamageMessageHandler());
        this.subscriber.addHandler(FireBulletMessage.class, new FireBulletMessageHandler());
        this.subscriber.addHandler(KillMessage.class, new KillMessageHandler(this.entities));
        this.subscriber.addHandler(StateMessage.class, new StateMessageHandler(uiUpdates, this.canvas, this.entities));

        try {
            this.subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the canvas for scrolling and panning.
     *
     * @param primaryStage - Stage as given by the start method
     */
    private void setupInterface(Stage primaryStage) {
        Group group = new Group();

        primaryStage.setTitle("Drone simulator");
        primaryStage.setResizable(false);

        // create canvas
        canvas = new PannableCanvas(Settings.CANVAS_WIDTH, Settings.CANVAS_HEIGHT);
        canvas.setId("pane");
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);
        group.getChildren().add(canvas);

        double width = Settings.SCENE_WIDTH > Settings.CANVAS_WIDTH ? Settings.CANVAS_WIDTH : Settings.SCENE_WIDTH;
        double height = Settings.SCENE_HEIGHT > Settings.CANVAS_HEIGHT ? Settings.CANVAS_HEIGHT : Settings.SCENE_HEIGHT;

        // create scene which can be dragged and zoomed
        Scene scene = new Scene(group, width, height);
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        scene.getStylesheets().addAll(this.getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.addGrid();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
