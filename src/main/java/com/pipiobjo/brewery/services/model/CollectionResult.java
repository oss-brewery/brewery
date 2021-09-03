package com.pipiobjo.brewery.services.model;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetTemperature;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperature;
import com.pipiobjo.brewery.adapters.io.DebugInputs;
import lombok.Data;

@Data
public class CollectionResult {
    boolean flameControlButtonPushed;
    boolean flameOn;
    FlameTemperature flameTemperature;
    InPotTemperature inpotTemperature;
    ControlCabinetTemperature controlCabinetTemperature;
    DebugInputs debugInputs;
}
