package com.pipiobjo.brewery.adapters.controlcabinet;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Data
public class ControlCabinetTemperature {
    OffsetDateTime timestamp;
    Optional<BigDecimal> controlCabinetAirTemp;
    Optional<BigDecimal> airTemp;
}
