package com.pipiobjo.brewery.adapters.flametemp;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class FlameTempMockAdapter implements FlameTempAdapter{
    @Override
    public FlameTemperature getFlameTemp() {
        FlameTemperature result = new FlameTemperature();
        result.setTimestamp(OffsetDateTime.now());
        result.setTemperature(Optional.of(BigDecimal.valueOf(400)));
        return result;
    }
}
