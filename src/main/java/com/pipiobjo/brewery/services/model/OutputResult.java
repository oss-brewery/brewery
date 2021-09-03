package com.pipiobjo.brewery.services.model;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetTemperature;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperature;
import com.pipiobjo.brewery.adapters.io.DebugInputs;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OutputResult {
    long motorControlTargetPositionInc;
    BigDecimal motorControlTargetPercent;
}
