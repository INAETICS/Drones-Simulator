package org.inaetics.dronessimulator.pubsub.rabbitmq.common;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;
import org.inaetics.dronessimulator.discovery.api.instances.RabbitInstance;
import org.osgi.service.cm.ConfigurationException;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class RabbitConnectionInfo {
    private static final Logger logger = Logger.getLogger(RabbitConnectionInfo.class);
    private int ttl = 5 * 60; //5 min
    private final LocalDateTime creationTime = LocalDateTime.now();
    private final String username;
    private final String password;
    private final String uri;

    public ConnectionFactory createConnectionFactory() throws ConnectionInfoExpiredException {
        if (!isValid()){
            throw new ConnectionInfoExpiredException();
        }
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        try {
            logger.debug("Received configuration, RabbitMQ URI is {}", uri);
            factory.setUri(uri);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("Invalid URI found in configuration", e);
        }
        return factory;
    }

    /**
     * Check if the TTL has expired
     */
    public boolean isValid() {
        return LocalDateTime.now().isBefore(creationTime.plusSeconds(ttl));
    }

    public static RabbitConnectionInfo createInstance(Discoverer m_discovery) {
        Map<String, String> rabbitData = m_discovery.getNode(new RabbitInstance()).getValues();
        logger.debug("Create new RabbitConnectionInfo instance from {}", rabbitData);
        return new RabbitConnectionInfo(rabbitData.get("username"), rabbitData.get("password"), rabbitData.get("uri"));
    }

    class ConnectionInfoExpiredException extends Exception {}
}
