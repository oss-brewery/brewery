package com.pipiobjo.brewery.services.collector;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@Data
@ConfigProperties(prefix = "brewery.sensors")
public class SensorCollectorServiceConfigProperties {
    private long baseCollectionIntervallInMS;
    private long inputCollectionIntervallInMS;
    private long temperatureCollectionIntervallInMS;

}
