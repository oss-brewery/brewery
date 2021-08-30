package com.pipiobjo.brewery.adapters.spiextensionboard;

import com.pipiobjo.brewery.services.simulation.BreweryHardwareSimulation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SPIExtensionBoardMockAdapter implements SPIExtensionBoardAdapter {

    BreweryHardwareSimulation breweryHardwareSimulation = null;

    public SPIExtensionBoardMockAdapter(BreweryHardwareSimulation breweryHardwareSimulation){
        this.breweryHardwareSimulation = breweryHardwareSimulation;
    }

    @Override
    public boolean isFlameControlButtonPushed() {
        return false;
    }

    @Override
    public void beepOn() {

    }

    @Override
    public void beepOff() {

    }

    @Override
    public void beepForMilliSeconds(long milliseconds) {

    }

    @Override
    public void motorControl(long deltaPosition) {

    }

    @Override
    public void turn230VRelaisOn() {

    }

    @Override
    public void turn230VRelaisOff() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isFlameOn() {
        return breweryHardwareSimulation.isFlameIsOn();
    }

}
