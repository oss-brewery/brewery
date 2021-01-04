package com.pipiobjo.brewery.services.model;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetTemperature;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InpotTemperature;
import lombok.Data;

@Data
public class SelfCheckResult {
    InpotTemperature inpotTemperature;
    FlameTemperature flameTemperature;
    ControlCabinetTemperature controlCabinetTemperature;
}
