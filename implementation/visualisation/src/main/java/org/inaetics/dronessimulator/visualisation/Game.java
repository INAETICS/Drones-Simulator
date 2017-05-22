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
import org.inaetics.dronessimulator.common.D3PoolCoordinate;
import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;
import org.inaetics.dronessimulator.visualisation.controls.NodeGestures;
import org.inaetics.dronessimulator.visualisation.controls.PannableCanvas;
import org.inaetics.dronessimulator.visualisation.controls.SceneGestures;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Game extends Application implements MessageHandler {
    private volatile RabbitSubscriber subscriber;

    private PannableCanvas canvas;

    public Game() {
    }

    private Map<String, Drone> drones = new HashMap<>();
    private Map<String, Bullet> bullets = new HashMap<>();

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
//                    System.out.println("Average: " + durationAverageMs);
//                    System.out.println("FPS: " + fps);
                    i = 0;
                }

                // update sprites in scene
                drones.forEach((id, drone) -> drone.updateUI());
                bullets.forEach((id, bullet) -> bullet.updateUI());
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

            if (!stateMessage.getIdentifier().isPresent()) {
                return;
            }
            switch (stateMessage.getType()) {
                case DRONE:
                    createOrUpdateDrone(stateMessage);
                    break;
                case BULLET:
                    createOrUpdateBullet(stateMessage);
                    break;
                default:
                    System.out.println("Unknown type");
            }
        } else if (message instanceof KillMessage) {
            KillMessage killMessage = (KillMessage) message;

            if (!killMessage.getIdentifier().isPresent()) {
                return;
            }
            drones.get(killMessage.getIdentifier().get()).delete();
            drones.remove(killMessage.getIdentifier().get());
        } else {
            Logger.getLogger(this.getClass()).info("Received non-state msg: " + message);
        }
    }

    /**
     * Creates a new drone and returns it
     *
     * @param id String - Identifier of the new drone
     * @return drone Drone - The newly created drone
     */
    private Drone createPlayer(String id) {
        // create drone
        BasicDrone drone = new BasicDrone(canvas);

        drone.setPosition(new D3Vector(500, 400, 0));
        drone.setDirection(new D3PoolCoordinate(0, 0, 0));

        // register drone
        drones.put(id, drone);

        return drone;
    }

    /**
     * Creates a new bullet and returns it
     *
     * @param id String - Identifier of the new bullet
     * @return bullet Bullet - The newly created bullet
     */
    private Bullet createBullet(String id) {
        System.out.println("Create bullet");
        // create drone
        Bullet bullet = new Bullet(canvas);

        bullet.setPosition(new D3Vector(0, 0, 0));
        bullet.setDirection(new D3PoolCoordinate(0, 0, 0));

        // register drone
        bullets.put(id, bullet);

        return bullet;
    }

    private void createOrUpdateDrone(StateMessage stateMessage) {
        Drone currentDrone = drones.computeIfAbsent(stateMessage.getIdentifier().get(), k -> createPlayer(stateMessage.getIdentifier().get()));

        if (stateMessage.getPosition().isPresent()) {
            currentDrone.setPosition(stateMessage.getPosition().get());
        }

        if (stateMessage.getDirection().isPresent()) {
            currentDrone.setDirection(stateMessage.getDirection().get());
        }
    }

    private void createOrUpdateBullet(StateMessage stateMessage) {
        Bullet currentBullet = bullets.computeIfAbsent(stateMessage.getIdentifier().get(), k -> createBullet(stateMessage.getIdentifier().get()));

        if (stateMessage.getPosition().isPresent()) {
            currentBullet.setPosition(stateMessage.getPosition().get());
        }

        if (stateMessage.getDirection().isPresent()) {
            currentBullet.setDirection(stateMessage.getDirection().get());
        }
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
        this.subscriber.addHandler(StateMessage.class, this);
        this.subscriber.addHandler(KillMessage.class, this);
        try {
            this.subscriber.addTopic(MessageTopic.STATEUPDATES);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupInterface(Stage primaryStage) {
        Group group = new Group();

        primaryStage.setTitle("Drone simulator");
        primaryStage.setResizable(false);

        // create canvas
        canvas = new PannableCanvas(Settings.CANVAS_WIDTH, Settings.CANVAS_HEIGHT);

        canvas.setId("pane");

        canvas.setTranslateX(0);
        canvas.setTranslateY(0);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures = new NodeGestures(canvas);

        Circle circle1 = new Circle(300, 300, 50);
        circle1.setStroke(Color.ORANGE);
        circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
        circle1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        circle1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Rectangle rect1 = new Rectangle(100, 100);
        rect1.setTranslateX(450);
        rect1.setTranslateY(450);
        rect1.setStroke(Color.BLUE);
        rect1.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
        rect1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rect1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        canvas.getChildren().addAll(circle1, rect1);

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
