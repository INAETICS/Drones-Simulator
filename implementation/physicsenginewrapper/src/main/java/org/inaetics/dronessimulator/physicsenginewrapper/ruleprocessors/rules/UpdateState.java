package org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.rules;

import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.CurrentStateMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.PhysicsEngineMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.message.RuleMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.state.GameStateManager;

import java.util.List;

public class UpdateState extends Processor {
    @Override
    public void process(GameStateManager stateManager, PhysicsEngineMessage msg, List<RuleMessage> results) {
        if(msg instanceof CurrentStateMessage) {
            stateManager.updateState(((CurrentStateMessage) msg).getCurrentState());
        }
    }
}
