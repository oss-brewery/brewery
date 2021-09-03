package com.pipiobjo.brewery.services.model;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetTemperature;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InpotTemperature;
import lombok.Data;

@Data
public class CollectionResult {
    boolean flameControlButtonPushed;
    boolean flameOn;
    FlameTemperature flameTemperature;
    InpotTemperature inpotTemperature;
    ControlCabinetTemperature controlCabinetTemperature;
}
