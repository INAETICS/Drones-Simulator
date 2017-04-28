package org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors;


import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.physicsenginewrapper.physicsenginemessage.PhysicsEngineMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.message.RuleMessage;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.rules.CollisionRules;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.rules.SendMessages;
import org.inaetics.dronessimulator.physicsenginewrapper.ruleprocessors.rules.UpdateState;
import org.inaetics.dronessimulator.physicsenginewrapper.state.GameStateManager;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class RuleProcessors extends Thread {
    private final LinkedBlockingQueue<PhysicsEngineMessage> incomingMessages;
    private final GameStateManager stateManager;
    private final Publisher publisher;

    private final UpdateState updateState;
    private final CollisionRules collisionRules;
    private final SendMessages sendMessages;

    public RuleProcessors(LinkedBlockingQueue<PhysicsEngineMessage> incomingMessages, GameStateManager stateManager, Publisher publisher) {
        this.incomingMessages = incomingMessages;
        this.stateManager = stateManager;
        this.publisher = publisher;

        this.updateState = new UpdateState();
        this.collisionRules = new CollisionRules();
        this.sendMessages = new SendMessages(publisher);
    }

    public void run() {
        while(!this.isInterrupted()) {
            PhysicsEngineMessage msg = null;
            try {
                msg = incomingMessages.take();
                System.out.println("RECEIVED MESSAGE FOR RULEPROCESSORS " + msg);
            } catch (InterruptedException e) {
                Logger.getLogger(RuleProcessors.class).error("Interrupted while waiting for incoming message");
                this.interrupt();
            }

            if(msg != null) {
                List<RuleMessage> results = new ArrayList<>();

                 this.updateState.process(this.stateManager, msg, results);
                 this.collisionRules.process(this.stateManager, msg, results);
                 this.sendMessages.process(this.stateManager, msg, results);
            } else {
                Logger.getLogger(RuleProcessors.class).error("Received message on incoming queue but was null!");
            }
        }
    }

    public void quit() {
        this.interrupt();
    }
}
