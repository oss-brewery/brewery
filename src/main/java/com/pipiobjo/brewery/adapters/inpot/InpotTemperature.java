package com.pipiobjo.brewery.adapters.inpot;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Data
public class InpotTemperature {

    OffsetDateTime timestamp;
    Optional<BigDecimal> bottom;
    Optional<BigDecimal> middle;
    Optional<BigDecimal> top;
}
