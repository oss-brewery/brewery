package com.pipiobjo.brewery.adapters.monotorcontrol;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import io.quarkus.arc.DefaultBean;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
@DefaultBean
public class MotorControlDeviceAdapter implements MotorControlAdapter{

    @Inject
    SPIExtensionBoardAdapter spiExtensionBoard;

    @Override
    public long getMaxPosition() {
        // see also "getCurrentPosition"
        return 0;
    }

    @Override
    public long getMinPosition() {
        // see also "getCurrentPosition"
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        // TODO software persistence required because stepper has no position sensor
        // TODO implement a reference drive at Startup for having correct position.
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
