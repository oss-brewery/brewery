package com.pipiobjo.brewery.adapters.io;

import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetAdapter;
import com.pipiobjo.brewery.adapters.controlcabinet.ControlCabinetTemperature;
import com.pipiobjo.brewery.adapters.flametemp.FlameTempAdapter;
import com.pipiobjo.brewery.adapters.flametemp.FlameTemperature;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperatureAdapter;
import com.pipiobjo.brewery.adapters.inpot.InPotTemperature;
import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import io.quarkus.runtime.configuration.ProfileManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Data
@Slf4j
@ApplicationScoped
public class ReadInputState {

    @Inject
    InPotTemperatureAdapter inPotTemperatureAdapter;
    @Inject
    FlameTempAdapter flameTempAdapter;
    @Inject
    ControlCabinetAdapter controlCabinetAdapter;
    @Inject
    SPIExtensionBoardAdapter extensionBoard;

    @Inject
    BreweryHardwareSimulation breweryHardwareSimulation;

    @PostConstruct
    void init() {
        log.info("Input adapter is starting");
    }

    public void checkConfiguration(){
        inPotTemperatureAdapter.checkConfiguration();
        controlCabinetAdapter.checkConfiguration();
    }

    public InPotTemperature getInPotTemperatures(){
        return inPotTemperatureAdapter.getTemperatures();
    }

    public FlameTemperature getFlameTemp(){
        return flameTempAdapter.getFlameTemp();
    }

    public ControlCabinetTemperature getControlCabinetTemp(){
        return controlCabinetAdapter.getTemperatures();
    }

    public boolean isFlameControlButtonPushed(){
        return extensionBoard.isFlameControlButtonPushed();
    }

    public boolean isFlameOn(){
        return extensionBoard.isFlameOn();
    }

    public DebugInputs getDebugInputs(){
        if (ProfileManager.getActiveProfile().equals("mockDevices")){
            DebugInputs result = new DebugInputs();
            result.setTimestamp(OffsetDateTime.now());
            result.setDebugInput1(Optional.of(breweryHardwareSimulation.getDebugValue1()));
            result.setDebugInput2(Optional.of(breweryHardwareSimulation.getDebugValue2()));
            return result;
        } else {
            DebugInputs result = new DebugInputs();
            result.setTimestamp(OffsetDateTime.now());
            result.setDebugInput1(Optional.of(BigDecimal.ZERO));
            result.setDebugInput2(Optional.of(BigDecimal.ZERO));
            return result;
        }
    }

}
