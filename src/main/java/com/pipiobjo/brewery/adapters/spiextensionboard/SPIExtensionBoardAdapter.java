package com.pipiobjo.brewery.adapters.spiextensionboard;

public interface SPIExtensionBoardAdapter {
    boolean isFlameControlButtonPushed();

    void beepOn();

    void beepOff();

    void beepForMilliSeconds(long milliseconds);

    void motorControl(long deltaPosition);

    void turn230VRelaisOn();

    void turn230VRelaisOff();

    long getMotorPositionInc();

    long getMotorPositionIncMin();

    long getMotorPositionIncMax();

    long getPeriodLenght();

    long getTargetTemp();

    boolean isFlameIsOn();
}
