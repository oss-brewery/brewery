package com.pipiobjo.brewery.adapters.monotorcontrol;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoard;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class MotorControlDeviceAdapter implements MotorControlAdapter{

    @Inject
    SPIExtensionBoard spiExtensionBoard;


    @Override
    public long getMaxPosition() {
        log.info("getMaxPosition");
        return 0;
    }

    @Override
    public long getMinPosition() {
        log.info("getMinPosition");
        return 0;
    }

    @Override
    public long getCurrentPosition() {

        log.info("current {} {}", 12, 13);
        return 0;
    }

    @Override
    public boolean moveToPosition(long targetPosition) {
        return false;
    }
}
