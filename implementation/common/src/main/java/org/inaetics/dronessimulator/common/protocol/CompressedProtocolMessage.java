package org.inaetics.dronessimulator.common.protocol;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CompressedProtocolMessage extends ProtocolMessage {

    private List<StateMessage> msgs;

    public CompressedProtocolMessage() {
        msgs = new ArrayList<>();
    }

    public CompressedProtocolMessage(List<StateMessage> msgs) {
        this.msgs = msgs;
    }

    public void add(StateMessage msg) {
        msgs.add(msg);
    }

    public void add(List<StateMessage> msgsArg) {
        msgs.addAll(msgsArg);
    }

    public void remove(StateMessage msg) {
        msgs.remove(msg);
    }

    public void remove(List<StateMessage> msgsArg) {
        msgs.removeAll(msgsArg);
    }

    public List<StateMessage> getMsgs() {
        return msgs;
    }

    public ProtocolMessage poll() {
        if (msgs.size() != 0) {
            ProtocolMessage m = msgs.get(0);
            msgs.remove(0);
            return m;
        }
        return null;
    }

    public Stream<StateMessage> stream() {
        return msgs.stream();
    }

    @Override
    public List<MessageTopic> getTopics() {
        List<MessageTopic> res = new ArrayList<>();
        res.add(MessageTopic.STATEUPDATES);
        return res;
        //return Collections.singletonList(MessageTopic.STATEUPDATES);
    }

    @Override
    public String toString() {
        return "(CompressedProtocolMessage{" +
                String.join(",", msgs.stream().map(Object::toString).collect(Collectors.toList())) +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompressedProtocolMessage)) return false;
        CompressedProtocolMessage that = (CompressedProtocolMessage) o;
        return Objects.equals(msgs, that.msgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msgs);
    }

}
