package org.inaetics.dronessimulator.common.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CompressedProtocolMessage extends ProtocolMessage {

    public CompressedProtocolMessage() {
    }

    public CompressedProtocolMessage(List<ProtocolMessage> msgs) {
        this.msgs = msgs;
    }

    private List<ProtocolMessage> msgs = new ArrayList<>();

    public void add(ProtocolMessage msg) {
        msgs.add(msg);
    }

    public void add(List<ProtocolMessage> msgsArg) {
        msgs.addAll(msgsArg);
    }

    public void remove(ProtocolMessage msg) {
        msgs.remove(msg);
    }

    public void remove(List<ProtocolMessage> msgsArg) {
        msgs.removeAll(msgsArg);
    }

    public List<ProtocolMessage> getAll() {
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

    public Stream<ProtocolMessage> stream() {
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
