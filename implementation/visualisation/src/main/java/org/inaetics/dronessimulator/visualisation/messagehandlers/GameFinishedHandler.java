package org.inaetics.dronessimulator.visualisation.messagehandlers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.common.protocol.GameFinishedMessage;
import org.inaetics.dronessimulator.pubsub.api.MessageHandler;

public class GameFinishedHandler implements MessageHandler<GameFinishedMessage> {
    private static final Logger logger = Logger.getLogger(GameFinishedHandler.class);
    private final Window mainWindow;

    public GameFinishedHandler(Window mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void handleMessage(GameFinishedMessage gameFinishedMessage) {
        // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("The game finished");
            if (gameFinishedMessage.getWinner() == null) {
                //There is no winner defined. This means that there was a draw.
                alert.setContentText("The game is finished and ended in a draw");

            } else {
                alert.setContentText("The game is finished and was won by: " + gameFinishedMessage.getWinner());
            }
            alert.initStyle(StageStyle.UTILITY);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            alert.setHeaderText(null);
            alert.setResizable(true);

            alert.show();
        });
    }
}
