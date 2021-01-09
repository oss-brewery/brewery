package com.pipiobjo.brewery.websocket;

import com.pipiobjo.brewery.rest.BreweryBasicITestProfile;
import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.CollectionResult;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Tag("integration")
@TestProfile(BreweryBasicITestProfile.class)
@QuarkusTest
public class SensorDataTest {
    private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

    @TestHTTPResource("/brewery/sensors/")
    URI uri;

    @Inject
    SensorCollectorService sensorCollectorService;

    @Test
    public void testWebsocketChat() throws Exception {
        sensorCollectorService.startCollecting();

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            assertThat(MESSAGES.poll(2, TimeUnit.SECONDS)).contains("CONNECT");
            session.getAsyncRemote().sendText("hello world");
        }
        sensorCollectorService.stopCollecting();
    }

    @ClientEndpoint
    public static class Client {

        @OnOpen
        public void open(Session session) {
            MESSAGES.add("CONNECT");
            // Send a message to indicate that we are ready,
            // as the message handler may not be registered immediately after this callback.
            session.getAsyncRemote().sendText("_ready_");
        }

        @OnMessage
        void message(String msg) {
            log.info("received message={}", msg);
            MESSAGES.add(msg);
        }

    }


}