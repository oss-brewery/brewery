package com.pipiobjo.brewery.services.collector;

import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class PiCalculator {

    public long calculate(Long iterator, BigDecimal targetTemp, BigDecimal flameTemp) {
        return 0L;
    }
}
