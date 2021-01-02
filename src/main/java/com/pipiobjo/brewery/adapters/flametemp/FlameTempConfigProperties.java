package com.pipiobjo.brewery.adapters.flametemp;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@Data
@ConfigProperties(prefix = "brewery.flametemp")
public class FlameTempConfigProperties {
    private int chipselect = 1;
    private int freq = 1000000;
    private int controller = 0;
}
