package org.inaetics.dronessimulator.pubsub.rabbitmq.common;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.instances.RabbitInstance;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class RabbitConnectionInfo {
    private static final Logger logger = Logger.getLogger(RabbitConnectionInfo.class);
    private int ttl = 5 * 60; //5 min
    private final LocalDateTime creationTime = LocalDateTime.now();
    private final String username;
    private final String password;
    private final String uri;

    public RabbitConnectionInfo(String username, String password, String uri) {
        this.username = username;
        this.password = password;
        this.uri = uri;
    }

    public static Logger getLogger() {
        return logger;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUri() {
        return uri;
    }


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
     * TODO use TTL from ETCD itself as well
     */
    public boolean isValid() {
        return LocalDateTime.now().isBefore(creationTime.plusSeconds(ttl));
    }

    public static RabbitConnectionInfo createInstance(Discoverer m_discovery) {
        Map<String, String> rabbitData = m_discovery.getNode(new RabbitInstance()).getValues();
        logger.debug("Create new RabbitConnectionInfo instance from {}", rabbitData);
        return new RabbitConnectionInfo(rabbitData.get("username"), rabbitData.get("password"), rabbitData.get("uri"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RabbitConnectionInfo)) return false;
        RabbitConnectionInfo that = (RabbitConnectionInfo) o;
        return ttl == that.ttl &&
                Objects.equals(creationTime, that.creationTime) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ttl, creationTime, username, password, uri);
    }

    @Override
    public String toString() {
        return "RabbitConnectionInfo{" +
                "ttl=" + ttl +
                ", creationTime=" + creationTime +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }

    public class ConnectionInfoExpiredException extends Exception {}
}
