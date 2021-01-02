package com.pipiobjo.brewery.adapters.flametemp;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Data
public class FlameTemperature {
    OffsetDateTime timestamp;
    Optional<BigDecimal> temperature;
}
