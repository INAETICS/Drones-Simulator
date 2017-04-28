package org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.rules;


import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.PhysicsEngineMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.message.RuleMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.state.GameStateManager;

import java.util.List;

public abstract class Processor {
    public abstract void process(GameStateManager stateManager, PhysicsEngineMessage msg, List<RuleMessage> results);
}
