package org.inaetics.dronessimulator.gameengine.ruleprocessors;


import org.inaetics.dronessimulator.common.GameMode;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.*;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.CollisionRule;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.DeathmatchGameFinished;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.KillEntitiesRule;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.teamplay.TeamplayGameFinished;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

import java.util.LinkedList;
import java.util.List;

public class RuleSets {
    public static List<Rule> getRulesForGameMode(GameMode gameMode, Publisher publisher, IdentifierMapper idMapper) {
        List<Rule> result = new LinkedList<>();
        switch (gameMode) {
            case DEATHMATCH:
                result.add(new KillOutOfBounds());
                result.add(new CollisionRule());
                result.add(new KillEntitiesRule());
                result.add(new DeathmatchGameFinished(idMapper));
                break;
            case TEAMPLAY:
                result.add(new KillEntitiesRule());
                result.add(new CollisionRule());
                result.add(new TeamplayGameFinished(idMapper));
                break;
        }
        result.add(new RemoveStrayBullets());
        result.add(new RemoveStaleStateData());
        result.add(new SendMessages(publisher, idMapper));

        return result;
    }
}
