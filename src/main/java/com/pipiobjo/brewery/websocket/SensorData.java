package com.pipiobjo.brewery.websocket;

import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.CollectionResult;
import com.pipiobjo.brewery.websocket.json.JSONTextDecoder;
import com.pipiobjo.brewery.websocket.json.JSONTextEncoder;
import com.pipiobjo.brewery.websocket.model.WebsocketMessage;
import io.quarkus.vertx.ConsumeEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/brewery/sensors/",
        decoders = {JSONTextDecoder.class},
        encoders = {JSONTextEncoder.class})
@ApplicationScoped
public class SensorData {
    Map<String, Session> sessions = new ConcurrentHashMap<>();
    Jsonb jsonb = JsonbBuilder.create();

    @ConsumeEvent(value = SensorCollectorService.PUBLISH_TO_UI_EVENT_NAME, blocking = true)
    public void updateUIEvent(CollectionResult event) {
        log.info("receiving collection result: {}", event);
        WebsocketMessage msg = new WebsocketMessage();
        msg.setMessageType(WebsocketMessage.MESSAGE_TYPES.UPDATING_SENSOR_DATA);
        msg.setBody(jsonb.toJson(event));
        broadcast(msg);
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("Session: {} contains errors:", session.getId(), throwable);
        sessions.remove(session.getId());
    }

    @OnMessage
    public void onMessage(Session session, JsonObject message) {

        log.info("Receiving msg: {}", message);
    }

    private void broadcast(WebsocketMessage obj) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(jsonb.toJson(obj), result -> {
                if (result.getException() != null) {
                    log.error("Unable to send obj: " + result.getException());
                }
            });
        });
    }
}
