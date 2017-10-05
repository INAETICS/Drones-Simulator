package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameFinishedEvent;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;

import java.util.Collections;
import java.util.List;

public abstract class AbstractGameFinishedRule extends Rule {
    @Override
    public void configRule() {
        //no config
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        if (msg instanceof CurrentStateEvent) {
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;
            if (gameIsFinished(currentStateEvent.getCurrentState())) {
                return Collections.singletonList(new GameFinishedEvent(getWinner()));
            }
        }
        return Collections.singletonList(msg);


    }

    protected abstract String getWinner();

    protected abstract boolean gameIsFinished(List<GameEntity> currentState);
}
