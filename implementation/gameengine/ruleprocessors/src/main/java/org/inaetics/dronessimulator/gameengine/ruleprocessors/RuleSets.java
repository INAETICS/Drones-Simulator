package org.inaetics.dronessimulator.gameengine.ruleprocessors;


import org.inaetics.dronessimulator.common.GameMode;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.*;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.CollisionRule;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.DeathmatchGameFinished;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.KillEntitiesRule;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.teamplay.TeamplayGameFinished;
import org.inaetics.pubsub.api.pubsub.Publisher;

import java.util.LinkedList;
import java.util.List;

public class RuleSets {
    private RuleSets() {
        throw new IllegalStateException("Utility class");
    }
    public static List<Rule> getRulesForGameMode(GameMode gameMode, Publisher publisher, IdentifierMapper idMapper) {
        List<Rule> result = new LinkedList<>();
        //General rules that are applicable in any game mode
        result.add(new KillOutOfBounds());
        result.add(new CollisionRule());
        result.add(new KillEntitiesRule());
        result.add(new RemoveStrayBullets());
        result.add(new RemoveStaleStateData());

        //Game mode specific rules
        switch (gameMode) {
            case DEATHMATCH:
                break;
            case TEAMPLAY:
                break;
        }

        //This must always be the last
        result.add(new SendMessages(publisher, idMapper));
        return result;
    }

    public static List<Rule> getIntervalRulesForGameMode(GameMode gameMode, Publisher publisher, IdentifierMapper
            idMapper) {
        List<Rule> result = new LinkedList<>();
        //General rules that are applicable in any game mode
        // None (yet)

        //Game mode specific rules
        switch (gameMode) {
            case DEATHMATCH:
                result.add(new DeathmatchGameFinished(idMapper));
                break;
            case TEAMPLAY:
                result.add(new TeamplayGameFinished(idMapper));
                break;
        }

        //This must always be the last
        result.add(new SendMessages(publisher, idMapper));
        return result;
    }
}
