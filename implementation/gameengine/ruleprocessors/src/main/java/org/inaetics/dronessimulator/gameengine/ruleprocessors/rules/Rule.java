package org.inaetics.dronessimulator.gameengine.ruleprocessors.rules;


import org.inaetics.dronessimulator.gameengine.common.gameevent.GameEngineEvent;

import java.util.List;

public abstract class Rule {
    public abstract void configRule();
    public abstract List<GameEngineEvent> process(GameEngineEvent msg);
}
