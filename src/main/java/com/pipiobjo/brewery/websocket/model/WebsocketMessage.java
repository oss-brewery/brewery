package com.pipiobjo.brewery.websocket.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebsocketMessage {
    public static enum MESSAGE_TYPES {
        UPDATING_SENSOR_DATA, INFORMATION_MESSAGE;
    }

    private MESSAGE_TYPES messageType;
    private String body;
}
