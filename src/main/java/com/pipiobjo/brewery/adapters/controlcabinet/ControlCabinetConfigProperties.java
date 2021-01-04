package com.pipiobjo.brewery.adapters.controlcabinet;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@Data
@ConfigProperties(prefix = "brewery.controlcabinet")
public class ControlCabinetConfigProperties {
    private String w1TempDevicePath;
    private String w1AirTempInCabinetID;
    private String w1AirTempID;
}
