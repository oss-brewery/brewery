package com.pipiobjo.brewery.adapters.alarm;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class AlarmDeviceAdapter implements AlarmAdapter{

    @Inject
    SPIExtensionBoardAdapter spiExtensionBoard;

    @Override
    public void turnAlarmOn() {
        log.info("try to turn beep on");
        spiExtensionBoard.beepOn();
        log.info("turned beep on");
    }

    @Override
    public void turnAlarmOff() {
        log.info("try to turn beep off");
        spiExtensionBoard.beepOff();
        log.info("turned beep off");
    }

    @Override
    public void turnAlarmOnForMilliseconds(long milliseconds) {
        spiExtensionBoard.beepForMilliSeconds(milliseconds);
        log.info("beeped for {} milli seconds", milliseconds);
    }
}
