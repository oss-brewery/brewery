package com.pipiobjo.brewery.adapters.spiextensionboard;

import io.quarkus.arc.profile.IfBuildProfile;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
@IfBuildProfile("mockDevices")
public class SPIExtensionBoardMockAdapter implements SPIExtensionBoardAdapter{
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
    public long getMotorPositionInc() {
        return 0;
    }

    @Override
    public long getMotorPositionIncMin() {
        return 0;
    }

    @Override
    public long getMotorPositionIncMax() {
        return 0;
    }

    @Override
    public long getPeriodLenght() {
        return 0;
    }

    @Override
    public long getTargetTemp() {
        return 0;
    }

    @Override
    public boolean isFlameIsOn() {
        return false;
    }
}
