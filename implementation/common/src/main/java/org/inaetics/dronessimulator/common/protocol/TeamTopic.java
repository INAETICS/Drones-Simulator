package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.pubsub.api.Topic;

import java.util.Objects;

public class TeamTopic implements Topic {

    public TeamTopic(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TeamTopic{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamTopic)) return false;
        TeamTopic teamTopic = (TeamTopic) o;
        return Objects.equals(name, teamTopic.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
