package com.pipiobjo.brewery.adapters.spiextensionboard;

public interface SPIExtensionBoardAdapter {
    boolean isFlameControlButtonPushed();

    void beepOn();

    void beepOff();

    void beepForMilliSeconds(long milliseconds);

    void motorControl(long deltaPosition);

    void turn230VRelaisOn();

    void turn230VRelaisOff();

    void close();

    boolean isFlameOn();

    /**
     * turn on the separate burning device.
     * The device controls the gas valve and the ignition by its own.
     */
    void turnOnAutomaticGasBurner();

    /**
     * turn off the separate burning device.
     * The device controls the gas valve by its own.
     */
    void turnOffAutomaticGasBurner();
}
