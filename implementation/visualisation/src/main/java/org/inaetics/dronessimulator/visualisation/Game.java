package org.inaetics.dronessimulator.visualisation;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventControllerService;
import org.inaetics.dronessimulator.common.architecture.SimulationAction;
import org.inaetics.dronessimulator.common.architecture.SimulationState;
import org.inaetics.dronessimulator.common.protocol.GameFinishedMessage;
import org.inaetics.dronessimulator.common.protocol.KillMessage;
import org.inaetics.dronessimulator.common.protocol.MessageTopic;
import org.inaetics.dronessimulator.common.protocol.StateMessage;
import org.inaetics.dronessimulator.common.vector.D3PolarCoordinate;
import org.inaetics.dronessimulator.common.vector.D3Vector;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.NodeEvent;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.etcd.EtcdDiscovererService;
import org.inaetics.dronessimulator.visualisation.controls.PannableCanvas;
import org.inaetics.dronessimulator.visualisation.controls.SceneGestures;
import org.inaetics.dronessimulator.visualisation.messagehandlers.GameFinishedHandler;
import org.inaetics.dronessimulator.visualisation.messagehandlers.KillMessageHandler;
import org.inaetics.dronessimulator.visualisation.messagehandlers.StateMessageHandler;
import org.inaetics.dronessimulator.visualisation.uiupdates.UIUpdate;
import org.inaetics.pubsub.api.pubsub.Publisher;
import org.inaetics.pubsub.api.pubsub.Subscriber;
import org.inaetics.pubsub.impl.pubsubadmin.zeromq.ZmqPublisher;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Instances of the game class create a new javafx application. This class provides a connection with etcd and INAETICS PubSub and
 * contain all the game elements.
 */
public class Game extends Application implements Subscriber {
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    public static final String URI_FIELD = "uri";

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Game.class);

    /**
     * UI updates
     */
    private final BlockingQueue<UIUpdate> uiUpdates = new LinkedBlockingQueue<UIUpdate>();

    /**
     * All the entities in the game
     */
    //@Getter(AccessLevel.PACKAGE)
    private final ConcurrentMap<String, BaseEntity> entities = new ConcurrentHashMap<>();
    private final KillMessageHandler killMessageHandler = new KillMessageHandler(entities);
    private final GameFinishedHandler gameFinishedHandler = new GameFinishedHandler();
    private final StateMessageHandler stateMessageHandler = new StateMessageHandler(uiUpdates, entities);

    ConcurrentMap<String, BaseEntity> getEntities() {
        return entities;
    }

    /**
     * All the available entities from the discoverer
     */
    private final ConcurrentMap<String, DiscoveryNode> availableEntities = new ConcurrentHashMap<>();

    private final Instance visualisationInstance = new Instance(Type.SERVICE, org.inaetics.dronessimulator.discovery.api.discoverynode.Group.SERVICES, "visualisation", new HashMap<>());

    /*
    Publisher field should be static
    It is injected by OSGi, but JavaFX creates it's own Game instance
   */
    private static volatile Publisher publisher;

    /**
     * Discoverer for etcd
     */
    //@Getter(AccessLevel.PACKAGE)
    private EtcdDiscovererService discoverer;

    EtcdDiscovererService getDiscoverer() {
        return discoverer;
    }

    /**
     * Close event handler
     * When the window closes, the discoverer disconnect
     */
    private final EventHandler<WindowEvent> onCloseEventHandler = new EventHandler<WindowEvent>() {
        boolean isClosed = false;

        @Override
        public void handle(WindowEvent t) {
            if (!isClosed) {
                log.info("Closing the application gracefully");
                if (discoverer != null)
                    discoverer.stop();
                isClosed = true;
                Platform.exit();
            }
        }
    };
    /**
     * The pannable and zommable canvas
     */
    private PannableCanvas canvas;
    /**
     * Group for all entities
     */
    private Group root;
    private Stage primaryStage;
    /**
     * counter for the logger to output once every 100 times
     */
    private int i = 0;
    /**
     * Time is ms of the last log
     */
    private long lastLog = -1;

    /**
     * Instantiates a new game object
     */
    public Game() {
        System.out.println("Game.java Constructor called");
    }

    /*OSGi start method*/
    public void start() {
        System.out.println("Game::start()");

        //Because this is the OSGi start method, we don't want to block here.
        new Thread(
                () -> Game.launch()
        ).start(); ;
    }

    /**
     * Main entry point for a JavaFX application
     *
     * @param primaryStage - the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Game::start(Stage)");

        this.primaryStage = primaryStage;
        setupInterface();
        setupDiscovery();
        setupGameEventListener();

        lastLog = System.currentTimeMillis();
        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long current_step_started_at_ms = System.currentTimeMillis();

                i++;
                if (i == 100 && log.isDebugEnabled()) {
                    long current = System.currentTimeMillis();
                    float durationAverageMs = ((float) (current - lastLog)) / 100f;
                    float fps = 1000f / durationAverageMs;
                    lastLog = current;

                    log.debug("Average: " + durationAverageMs);
                    log.debug("FPS: " + fps);
                    i = 0;
                }

                while (!uiUpdates.isEmpty()) {
                    try {
                        UIUpdate uiUpdate = uiUpdates.take();
                        uiUpdate.execute(canvas);
                    } catch (InterruptedException e) {
                        log.fatal(e);
                        Thread.currentThread().interrupt();
                    }
                }

                // update sprites in scene
                entities.forEach((id, entity) -> entity.updateUI());

                long current_step_ended_at_ms = System.currentTimeMillis();
                long current_step_took_ms = current_step_ended_at_ms - current_step_started_at_ms;
                long diff = 10 - current_step_took_ms;

                if (diff > 0) {
                    try {
                        Thread.sleep(diff);
                    } catch (InterruptedException e) {
                        log.fatal(e);
                        Thread.currentThread().interrupt();
                    }
                }
            }

        };
        gameLoop.start();

        setupArchitectureManagementVisuals();
        setupArchitectureManagement();
       //TODO: Move message Handlers to receive()
    }

    /**
     * Creates the canvas for scrolling and panning.
     */
    private void setupInterface() {
        root = new Group();

        primaryStage.setTitle("Drone simulator");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(onCloseEventHandler);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> onCloseEventHandler.handle(null)));

        // create canvas
        canvas = new PannableCanvas(Settings.CANVAS_WIDTH, Settings.CANVAS_HEIGHT);
        canvas.setId("pane");
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);

        root.getChildren().add(canvas);

        double width = Math.max(Settings.CANVAS_WIDTH, Settings.SCENE_WIDTH);
        double height = Math.max(Settings.CANVAS_HEIGHT, Settings.SCENE_HEIGHT);

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

    /**
     * Setup discovery
     * Create new discoverer object and start
     */
    private void setupDiscovery() {
        this.discoverer = new EtcdDiscovererService();
        this.discoverer.start();
    }

    private void handleNodeEvent(NodeEvent e) {
        DiscoveryNode node = e.getNode();
        DiscoveryPath path = node.getPath();
    }

    /**
     * Sets up the connection to the message broker and subscribes to the necessary channels and sets the required handlers
     */
    private void setupGameEventListener() {
        List<NodeEventHandler<RemovedNode>> removeHandlers = new ArrayList<>();
        List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();

        addHandlers.add((AddedNode addedNodeEvent) -> {
            DiscoveryNode node = addedNodeEvent.getNode();
            DiscoveryPath path = node.getPath();

            if (path.startsWith(DiscoveryPath.group(Type.DRONE, org.inaetics.dronessimulator.discovery.api.discoverynode.Group.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();
                availableEntities.put(protocolId, node);
                createDroneIfNotExists(protocolId, node.getValue("team"));
                log.info("Added drone " + protocolId + " to visualisation");
            }
        });

        removeHandlers.add((RemovedNode removedNodeEvent) -> {
            DiscoveryNode node = removedNodeEvent.getNode();
            DiscoveryPath path = node.getPath();

            if (path.startsWith(DiscoveryPath.group(Type.DRONE, org.inaetics.dronessimulator.discovery.api.discoverynode.Group.DRONE)) && path.isConfigPath()) {
                String protocolId = node.getId();
                availableEntities.remove(protocolId);
                BaseEntity baseEntity = entities.get(protocolId);

                if (baseEntity != null) {
                    baseEntity.delete();
                    entities.remove(protocolId);
                }
                log.info("Removed drone " + protocolId + " from visualisation");
            }
        });

        this.discoverer.addHandlers(true, addHandlers, Collections.emptyList(), removeHandlers);
    }

    /**
     * Create the different buttons for the architecture management and add the desired actions to them
     */
    private void setupArchitectureManagementVisuals() {
        HBox buttons = new HBox();

        Button configButton = new Button("Config");
        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resumeButton = new Button("Resume");
        Button stopButton = new Button("Stop");

        buttons.getChildren().addAll(configButton, startButton, pauseButton, resumeButton, stopButton);

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

        configButton.setOnMouseClicked(new ArchitectureButtonEventHandler(SimulationAction.CONFIG, publisher));
        startButton.setOnMouseClicked(new ArchitectureButtonEventHandler(SimulationAction.START, publisher));
        stopButton.setOnMouseClicked(new ArchitectureButtonEventHandler(SimulationAction.STOP, publisher));
        pauseButton.setOnMouseClicked(new ArchitectureButtonEventHandler(SimulationAction.PAUSE, publisher));
        resumeButton.setOnMouseClicked(new ArchitectureButtonEventHandler(SimulationAction.RESUME, publisher));
    }

    /**
     * Responds to incoming architecture lifecycle messages
     */
    private void setupArchitectureManagement() {
        ArchitectureEventControllerService architectureEventController = new ArchitectureEventControllerService(this.discoverer);
        architectureEventController.start();

        architectureEventController.addHandler(SimulationState.INIT, SimulationAction.CONFIG, SimulationState.CONFIG,
                (SimulationState fromState, SimulationAction action, SimulationState toState) -> {
                    for (BaseEntity e : this.entities.values()) {
                        e.delete();
                    }
                    this.entities.clear();
                    //Create all drones that are currently available
                    getAvailableDrones().forEach(entry -> createDrone(entry.getKey(), entry.getValue().getValue("team")));
                }
        );
    }

    private List<Map.Entry<String, DiscoveryNode>> getAvailableDrones() {
        //TODO maybe perform a check with etcd for synchronisation
        List<Map.Entry<String, DiscoveryNode>> availableDrones = availableEntities.entrySet().stream().filter((entry) -> entry.getValue().getPath().startsWith(DiscoveryPath.group(Type.DRONE, org.inaetics.dronessimulator.discovery.api.discoverynode.Group.DRONE))).collect(Collectors.toList());
        visualisationInstance.getProperties().put("availableDrones", availableDrones.toString());
        visualisationInstance.getProperties().put("numberOfAvailableDrones", String.valueOf(availableDrones.size()));
        try {
            discoverer.updateProperties(visualisationInstance, visualisationInstance.getProperties());
        } catch (IOException e) {
            log.error(e);
        }
        return availableDrones;
    }

    private void createDroneIfNotExists(String protocolId, String teamname) {
        BaseEntity baseEntity = entities.get(protocolId);

        if (baseEntity == null) {
            createDrone(protocolId, teamname);
        }
    }

    /**
     * Creates a new drone and returns it
     *
     * @param id String - Identifier of the new drone
     */
    private void createDrone(String id, String teamname) {
        BasicDrone drone = new BasicDrone(uiUpdates);
        drone.setId(id);
        drone.setTeam(teamname);
        drone.setPosition(new D3Vector(-9999, -9999, -9999));
        drone.setDirection(new D3PolarCoordinate(-9999, -9999, -9999));
        entities.putIfAbsent(id, drone);
    }

    @Override
    public void receive(Object o, MultipartCallbacks multipartCallbacks) {
        System.out.println("Game::Received msg: " + o.toString());

        if( o instanceof KillMessage) {
            this.killMessageHandler.handleMessage((KillMessage) o);
            return;
        } else if( o instanceof GameFinishedHandler) {
            this.gameFinishedHandler.handleMessage((GameFinishedMessage) o);
            return;
        } else if( o instanceof StateMessage) {
            this.stateMessageHandler.handleMessage((StateMessage) o);
            return;
        }
        System.out.println("(Message not handled)");
    }
}
