package com.pipiobjo.brewery.adapters.flamecontrol;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardDeviceAdapter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class FlameControlAdapterDevice implements FlameControlAdapter {

    @Inject
    SPIExtensionBoardDeviceAdapter spiExtensionBoard;

    @Override
    public void turnOff() {
        spiExtensionBoard.turnOffFlameControl();
        log.info("turned Off");
    }

    @Override
    public void turnOn() {
        spiExtensionBoard.turnOnFlameControl();
        log.info("turned On");
    }

    @Override
    public void flameOneStepHigher() {
        spiExtensionBoard.setTargetTempAdd(+50L);
        log.info("new target temperature ist = {}", spiExtensionBoard.getTargetTemp());
    }

    @Override
    public void flameOneStepLower() {
        spiExtensionBoard.setTargetTempAdd(-50L);
        log.info("new target temperature ist = {}", spiExtensionBoard.getTargetTemp());
    }

    @Override
    public boolean isFlameOn() {
        log.info("Check if flame is 'ON' ");
        return spiExtensionBoard.isFlameIsOn();

    }
}
