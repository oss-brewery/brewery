package com.pipiobjo.brewery.websocket;

import com.pipiobjo.brewery.services.collector.SensorCollectorService;
import com.pipiobjo.brewery.services.model.CollectionResult;
import io.quarkus.vertx.ConsumeEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint("/brewery/sensors/")
@ApplicationScoped
public class SensorData {
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @ConsumeEvent(value = SensorCollectorService.PUBLISH_TO_UI_EVENT_NAME, blocking = true)
    public void updateUIEvent(CollectionResult event) {
        log.info("receiving collection result: {}", event);
        broadcast(event);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
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
    public void onMessage(Session session, String message) {
        log.info("Receiving msg: {}", message);
    }

    private void broadcast(Object obj) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(obj, result -> {
                if (result.getException() != null) {
                    log.error("Unable to send obj: " + result.getException());
                }
            });
        });
    }
}
