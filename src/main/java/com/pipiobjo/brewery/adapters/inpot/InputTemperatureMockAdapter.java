package com.pipiobjo.brewery.adapters.inpot;

import io.quarkus.arc.profile.IfBuildProfile;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class InputTemperatureMockAdapter implements InPotTemperatureAdapter{

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void checkConfiguration() {
        log.info("fake configuration");
    }

    @Override
    public InpotTemperature getTemparatures() {
        int c = counter.getAndIncrement();
        InpotTemperature result = new InpotTemperature();
        result.setTimestamp(OffsetDateTime.now());

        result.setBottom(Optional.of(BigDecimal.valueOf(12+c)));
        result.setMiddle(Optional.of(BigDecimal.valueOf(11+c)));
        result.setTop(Optional.of(BigDecimal.valueOf(10+c)));

        return result;
    }
}
