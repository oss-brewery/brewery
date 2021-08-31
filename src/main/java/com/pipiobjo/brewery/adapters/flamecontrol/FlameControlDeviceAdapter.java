package com.pipiobjo.brewery.adapters.flamecontrol;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardDeviceAdapter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.math.BigDecimal;

@Slf4j
public class FlameControlDeviceAdapter implements FlameControlAdapter {

    @Inject
    SPIExtensionBoardDeviceAdapter spiExtensionBoard;

    public FlameControlDeviceAdapter() {
    }

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
    public void increaseFlameByOneStep() {
        log.info("decrease temperature");
    }

    @Override
    public void decreaseFlameByOneStep() {
        log.info("decrease flame temperature");
    }

    @Override
    public void setPointTempCelsius(BigDecimal setPoint){
        log.info("setPoint is {}°C", setPoint.toString());
    }

    @Override
    public boolean isFlameOn() {
        log.info("Check if flame is 'ON' ");
        return spiExtensionBoard.isFlameOn();

    }
}
