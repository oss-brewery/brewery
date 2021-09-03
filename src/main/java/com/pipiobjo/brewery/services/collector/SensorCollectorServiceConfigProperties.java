package com.pipiobjo.brewery.services.collector;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;
import javax.validation.constraints.Positive;

@Data
@ConfigProperties(prefix = "brewery.collector")
public class SensorCollectorServiceConfigProperties {

    @Positive() private long baseCollectionIntervallInMS;
    @Positive() private long inputCollectionIntervallInMS;
    @Positive() private long temperatureCollectionIntervallInMS;
    @Positive() private long persistenceIntervallInMS;
    @Positive() private long uiUpdateIntervallInMS;
    @Positive() private long calculationIntervallInMS;
    @Positive() private long outputWritingIntervallInMS;

}
