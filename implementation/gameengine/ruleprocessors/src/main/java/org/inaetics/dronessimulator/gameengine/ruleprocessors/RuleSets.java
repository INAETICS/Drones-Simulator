package org.inaetics.dronessimulator.gameengine.ruleprocessors;


import org.inaetics.dronessimulator.common.GameMode;
import org.inaetics.dronessimulator.gameengine.identifiermapper.IdentifierMapper;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.RemoveStaleStateData;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.RemoveStrayBullets;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.Rule;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.SendMessages;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.CollisionRule;
import org.inaetics.dronessimulator.gameengine.ruleprocessors.rules.deathmatch.KillEntitiesRule;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;

public class RuleSets {
    public static Rule[] getRulesForGameMode(GameMode gameMode, Publisher publisher, IdentifierMapper idMapper) {
        Rule[] result;
        switch(gameMode) {
            case DEATHMATCH:
                result = new Rule[]
                    { new CollisionRule()
                    , new KillEntitiesRule()
                    , new RemoveStrayBullets()
                    , new RemoveStaleStateData()
                    , new SendMessages(publisher, idMapper)
                    };
                break;

            default:
                result = new Rule[]{};
                break;
        }

        return result;
    }
}
