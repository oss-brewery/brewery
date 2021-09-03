package com.pipiobjo.brewery.adapters.io;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Data
public class DebugInputs {
    OffsetDateTime timestamp;
    Optional<BigDecimal> debugInput1;
    Optional<BigDecimal> debugInput2;
}
