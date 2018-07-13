package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;

import org.inaetics.dronessimulator.common.protocol.EntityType;
import org.inaetics.dronessimulator.gameengine.common.gameevent.CurrentStateEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;
import org.inaetics.dronessimulator.gameengine.common.gameevent.GameFinishedEvent;
import org.inaetics.dronessimulator.gameengine.common.state.Drone;
import org.inaetics.dronessimulator.gameengine.common.state.GameEntity;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractGameFinishedRule extends Rule {
    protected final IdentifierMapper idMapper;
    private final AtomicBoolean gameFinishedEventWasSend = new AtomicBoolean(false);
    private final AtomicBoolean wasGameFinishedInPreviousRun = new AtomicBoolean(false);

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractGameFinishedRule.class);

    public AbstractGameFinishedRule(IdentifierMapper idMapper) {
        this.idMapper = idMapper;
    }

    @Override
    public void configRule() {
        log.debug("Set gameFinishedEventWasSend to false so a new game finished event can be send if this game ends");
        gameFinishedEventWasSend.set(false);
        wasGameFinishedInPreviousRun.set(false);
    }

    @Override
    public List<GameEngineEvent> process(GameEngineEvent msg) {
        if (msg instanceof CurrentStateEvent) {
            log.debug("PROCESS MESSAGE IN GAMEFINISHEDHANDLER");
            CurrentStateEvent currentStateEvent = (CurrentStateEvent) msg;
            if (!gameFinishedEventWasSend.get()) {
                if (gameIsFinished(currentStateEvent.getCurrentState())) {
                    if (wasGameFinishedInPreviousRun.get()) {
                        gameFinishedEventWasSend.set(true);
                        return Collections.singletonList(new GameFinishedEvent(getWinner()));
                    } else {
                        wasGameFinishedInPreviousRun.set(true);
                    }
                } else if (noDronesLeft(currentStateEvent.getCurrentState())) {
                    //This is a very unlikely case because there will probably be an order in which the drones are killed.
                    gameFinishedEventWasSend.set(true);
                    return Collections.singletonList(new GameFinishedEvent(null));
                } else {
                    log.debug("Game not finished: " + "\n" +
                            "gameFinishedEventWasSend:" + gameFinishedEventWasSend.get() + "\n" +
                            "wasGameFinishedInPreviousRun:" + wasGameFinishedInPreviousRun.get());
                }
            }
        }
        return Collections.singletonList(msg);
    }

    private boolean noDronesLeft(List<GameEntity> currentState) {
        return currentState.stream().filter(gameEntity -> EntityType.DRONE.equals(gameEntity.getType())).map(gameEntity -> ((Drone) gameEntity)).filter(drone -> drone.getHp() > 0).count() == 0;
    }

    protected abstract String getWinner();

    protected abstract boolean gameIsFinished(List<GameEntity> currentState);
}
