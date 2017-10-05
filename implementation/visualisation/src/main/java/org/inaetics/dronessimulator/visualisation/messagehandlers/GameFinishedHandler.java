package org.inaetics.dronessimulator.visualisation.messagehandlers;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.inaetics.dronessimulator.common.protocol.GameFinishedMessage;
import org.inaetics.dronessimulator.pubsub.api.Message;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameFinishedHandler implements MessageHandler {
    private static final AtomicBoolean popupIsQueued = new AtomicBoolean(false);
    private final Window mainWindow;

    public GameFinishedHandler(Window mainWindow) {
        this.mainWindow = mainWindow;
        clear();
    }

    public void clear() {
        popupIsQueued.set(false);
    }

    @Override
    public void handleMessage(Message message) {
        GameFinishedMessage gameFinishedMessage = (GameFinishedMessage) message;
        if (!popupIsQueued.get()) {
            // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
            Platform.runLater(
                    () -> {
                        final Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(mainWindow);
                        VBox dialogVbox = new VBox(20);
                        dialogVbox.getChildren().add(new Text("The game is finished and was won by: " + gameFinishedMessage.getWinner()));
                        Scene dialogScene = new Scene(dialogVbox, 300, 200);
                        dialog.setScene(dialogScene);
                        dialog.show();
                    });
            popupIsQueued.set(true);
        }
    }
}
