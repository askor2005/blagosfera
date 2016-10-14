package ru.askor.blagosfera.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.EnumSet;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    @Autowired
    private SessionRepository sessionRepository;

    @Value("${rabbitmq.stomp.client.username}")
    private String clientLogin;

    @Value("${rabbitmq.stomp.client.password}")
    private String clientPasscode;

    @Value("${rabbitmq.stomp.system.username}")
    private String systemLogin;

    @Value("${rabbitmq.stomp.system.password}")
    private String systemPasscode;

    @Value("${rabbitmq.stomp.host}")
    private String relayHost;

    @Value("${rabbitmq.stomp.port}")
    private int relayPort;

    @Value("${rabbitmq.stomp.virtualhost}")
    private String virtualHost;

    @Value("${rabbitmq.stomp.heartbeat.send.interval}")
    private int heartbeatSendInterval;

    @Value("${rabbitmq.stomp.heartbeat.receive.interval}")
    private int heartbeatReceiveInterval;

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp_endpoint").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/websocket");

        registry.enableStompBrokerRelay("/queue/", "/topic/")
                .setClientLogin(clientLogin).setClientPasscode(clientPasscode)
                .setSystemLogin(systemLogin).setSystemPasscode(systemPasscode)
                .setRelayHost(relayHost).setRelayPort(relayPort).setVirtualHost(virtualHost)
                .setSystemHeartbeatSendInterval(heartbeatSendInterval)
                .setSystemHeartbeatReceiveInterval(heartbeatReceiveInterval);
    }

    @Override
    @Bean
    public SessionRepositoryMessageInterceptor sessionRepositoryInterceptor() {
        SessionRepositoryMessageInterceptor bean = new SessionRepositoryMessageInterceptor(sessionRepository);
        bean.setMatchingMessageTypes(EnumSet.of(SimpMessageType.CONNECT, SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE, SimpMessageType.UNSUBSCRIBE, SimpMessageType.HEARTBEAT));
        return bean;
    }
}