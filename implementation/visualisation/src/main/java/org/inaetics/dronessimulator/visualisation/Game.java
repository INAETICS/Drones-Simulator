package org.inaetics.dronessimulator.visualisation;

import com.rabbitmq.client.ConnectionFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.architecture.Action;
import org.inaetics.dronessimulator.common.protocol.*;
import org.inaetics.dronessimulator.pubsub.javaserializer.JavaSerializer;
import org.inaetics.dronessimulator.pubsub.rabbitmq.publisher.RabbitPublisher;
import org.inaetics.dronessimulator.pubsub.rabbitmq.subscriber.RabbitSubscriber;
import org.inaetics.dronessimulator.visualisation.controls.PannableCanvas;
import org.inaetics.dronessimulator.visualisation.controls.SceneGestures;
import org.inaetics.dronessimulator.visualisation.messagehandlers.*;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Game extends Application {
    private RabbitSubscriber subscriber;
    private RabbitPublisher publisher;

    private static final Logger logger = Logger.getLogger(Game.class);

    private final ConcurrentMap<String, BaseEntity> entities = new ConcurrentHashMap<>();

    private PannableCanvas canvas;
    private Group root;

    private final BlockingQueue<UIUpdate> uiUpdates;

    private KillMessageHandler killMessageHandler;
    private StateMessageHandler stateMessageHandler;

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
        setupArchitectureManagement();

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
            this.publisher = new RabbitPublisher(connectionFactory, new JavaSerializer());

            try {
                this.subscriber.connect();
                this.publisher.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.stateMessageHandler = new StateMessageHandler(uiUpdates, this.canvas, this.entities);
        this.killMessageHandler = new KillMessageHandler(this.entities);

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

    /**
     * Creates the canvas for scrolling and panning.
     *
     * @param primaryStage - Stage as given by the start method
     */
    private void setupInterface(Stage primaryStage) {
        root = new Group();

        primaryStage.setTitle("Drone simulator");
        primaryStage.setResizable(false);

        // create canvas
        canvas = new PannableCanvas(Settings.CANVAS_WIDTH, Settings.CANVAS_HEIGHT);
        canvas.setId("pane");
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);

        // create sample nodes which can be dragged
        // @todo: remove these before production, currently quite useful for position recognition when there are no drones
        /*
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
        */
        root.getChildren().add(canvas);

        double width = Settings.SCENE_WIDTH > Settings.CANVAS_WIDTH ? Settings.CANVAS_WIDTH : Settings.SCENE_WIDTH;
        double height = Settings.SCENE_HEIGHT > Settings.CANVAS_HEIGHT ? Settings.CANVAS_HEIGHT : Settings.SCENE_HEIGHT;

        // create scene which can be dragged and zoomed
        Scene scene = new Scene(root, width, height);
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        scene.getStylesheets().addAll(this.getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.addGrid();
    }

    private void setupArchitectureManagement() {
        HBox buttons = new HBox();

        Button startButton = new Button("Start");
        Button restartButton = new Button("Restart");
        Button stopButton = new Button("Stop");
        Button pauseButton = new Button("Pause");
        Button resumeButton = new Button("Resume");

        buttons.getChildren().addAll(startButton, restartButton, stopButton, pauseButton, resumeButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setPrefHeight(canvas.getScene().getHeight());
        borderPane.setPrefWidth(canvas.getScene().getWidth());

        Pane space = new Pane();
        space.setMinSize(1, 1);
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox container = new HBox();
        container.setPrefWidth(canvas.getScene().getWidth());

        container.getChildren().addAll(space, buttons);
        borderPane.setBottom(container);
        root.getChildren().add(borderPane);

        startButton.setOnMouseClicked(new ArchitectureButtonEventHandler(Action.START, publisher));
        restartButton.setOnMouseClicked(new ArchitectureButtonEventHandler(Action.RESTART, publisher));
        stopButton.setOnMouseClicked(new ArchitectureButtonEventHandler(Action.STOP, publisher));
        pauseButton.setOnMouseClicked(new ArchitectureButtonEventHandler(Action.PAUSE, publisher));
        resumeButton.setOnMouseClicked(new ArchitectureButtonEventHandler(Action.RESUME, publisher));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
