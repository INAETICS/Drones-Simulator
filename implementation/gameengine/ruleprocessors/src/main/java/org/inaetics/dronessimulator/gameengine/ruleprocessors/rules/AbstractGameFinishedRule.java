package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameFinishedEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractGameFinishedRule extends Rule {
    private static final Logger logger = Logger.getLogger(AbstractGameFinishedRule.class);
    private AtomicBoolean gameFinishedEventWasSend = new AtomicBoolean(false);

    @Override
    public void configRule() {
        logger.debug("Set gameFinishedEventWasSend to false so a new game finished event can be send if this game ends");
        gameFinishedEventWasSend.set(false);
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        if (msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;
            if (gameIsFinished(currentStateEvent.getCurrentState()) && !gameFinishedEventWasSend.get()) {
                gameFinishedEventWasSend.set(true);
                return Collections.singletonList(new GameFinishedEvent(getWinner()));
            }
        }
        return Collections.singletonList(msg);
    }

    protected abstract String getWinner();

    protected abstract boolean gameIsFinished(List<GameEntity> currentState);
}
