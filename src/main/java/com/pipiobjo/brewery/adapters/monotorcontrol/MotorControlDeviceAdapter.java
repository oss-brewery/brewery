package com.pipiobjo.brewery.adapters.monotorcontrol;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class MotorControlDeviceAdapter implements MotorControlAdapter{

    @Inject
    SPIExtensionBoardAdapter spiExtensionBoard;

    @Override
    public long getMaxPosition() {
//        long motorPositionIncMax = spiExtensionBoard.getMotorPositionIncMax();
//        log.info("getMaxPosition: {}", motorPositionIncMax);
        return 0;
    }

    @Override
    public long getMinPosition() {
//        long motorPositionIncMin = spiExtensionBoard.getMotorPositionIncMin();
//        log.info("getMinPosition: {}", motorPositionIncMin);
        return 0;
    }

    @Override
    public long getCurrentPosition() {
//        long motorPositionInc = spiExtensionBoard.getMotorPositionInc();
//        log.info("current {}", motorPositionInc);
        return 0;
    }

    @Override
    public boolean moveToPosition(long targetPosition) {
        long controlDifference = targetPosition - getCurrentPosition();
        log.info("drive steps: {}", controlDifference);
        spiExtensionBoard.motorControl(controlDifference);
        getCurrentPosition();
        return false;
    }
}
