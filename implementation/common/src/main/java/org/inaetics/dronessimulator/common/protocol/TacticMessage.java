package org.inaetics.dronessimulator.common.protocol;

import org.inaetics.dronessimulator.pubsub.api.Message;

import java.util.HashMap;

/**
 * Message that contains String key, value pairs
 */
public class TacticMessage extends HashMap<String, String> implements Message {

}
