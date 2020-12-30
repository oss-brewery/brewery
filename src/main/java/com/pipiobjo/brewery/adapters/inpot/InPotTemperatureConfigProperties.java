package com.pipiobjo.brewery.adapters.inpot;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@Data
@ConfigProperties(prefix = "brewery.inpot")
public class InPotTemperatureConfigProperties {
    private String w1TempDevicePath;
    private String w1TempBottomID;
    private String w1TempMiddleID;
    private String w1TempTopID;
}
