package com.pipiobjo.brewery.websocket;

import com.pipiobjo.brewery.rest.BreweryBasicITestProfile;
import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.CollectionResult;
import com.pipiobjo.brewery.websocket.model.WebsocketMessage;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
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
public class SensorDataITest {
    private static final LinkedBlockingDeque<WebsocketMessage> MESSAGES = new LinkedBlockingDeque<>();
    Jsonb jsonb = JsonbBuilder.create();
    @TestHTTPResource("/brewery/sensors/")
    URI uri;

    @Inject
    SensorCollectorService sensorCollectorService;

    @Test
    public void testWebsocketChat() throws Exception {
        sensorCollectorService.startCollecting();

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            WebsocketMessage msg = MESSAGES.poll(2, TimeUnit.SECONDS);
            assertThat(msg).isNotNull();
            WebsocketMessage.MESSAGE_TYPES messageType = msg.getMessageType();
            assertThat(messageType).isNotNull();
            assertThat(messageType).isEqualTo(WebsocketMessage.MESSAGE_TYPES.UPDATING_SENSOR_DATA);
            String resultJson = msg.getBody();
            assertThat(resultJson).isNotNull();
            CollectionResult body = jsonb.fromJson(resultJson, CollectionResult.class);
            assertThat(body).isNotNull();

//            session.getAsyncRemote().sendText("hello world");
        }
        sensorCollectorService.stopCollecting();
    }

    @ClientEndpoint
    public static class Client {
    Jsonb jsonb = JsonbBuilder.create();

        @OnOpen
        public void open(Session session) {
            log.info("session opened {}", session);
        }

        @OnMessage
        void message(String msg) {
            log.info("received message={}", msg);
            MESSAGES.add(jsonb.fromJson(msg, WebsocketMessage.class));
        }

    }


}