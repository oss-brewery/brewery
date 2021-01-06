package com.pipiobjo.brewery.adapters.flametemp;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
public class FlameTempMockAdapter implements FlameTempAdapter{
    @Override
    public FlameTemperature getFlameTemp() {
        FlameTemperature result = new FlameTemperature();
        result.setTimestamp(OffsetDateTime.now());
        result.setTemperature(Optional.of(BigDecimal.valueOf(400)));
        return result;
    }

    @Override
    public void close() {
        log.info("close");
    }
}
