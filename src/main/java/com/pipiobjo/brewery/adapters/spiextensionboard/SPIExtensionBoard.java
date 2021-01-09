package com.pipiobjo.brewery.adapters.spiextensionboard;

import com.diozero.util.SleepUtil;
import com.pipiobjo.brewery.sensors.MCP23S17;
import io.quarkus.runtime.ShutdownEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * https://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 */
@Slf4j
@ApplicationScoped
@Data
public class SPIExtensionBoard {

    @Inject
    SPIExtensionBoard1AdapterConfig config;

    //modes cpha oder cpol
    private MCP23S17 device = null;

    // motor
    private long motorPositionInc;
    private long motorPositionIncMin;  // software endpoint for the stepper motor
    private long motorPositionIncMax;  // software endpoint for the stepper motor
    private static final long MOTOR_INC_PER_REV = 200;  // set on driver via dip-switch

    // beep
    private long periodLenght = 10; // ms

    // temp
    private long targetTemp;
    private boolean flameIsOn;

    public SPIExtensionBoard(){
        if (device == null) {
            log.info("init extension board device");
            device = new MCP23S17(config.getControllerId(), config.getChipselect(), config.getFreq());




            // enable HAEN for enable addressing pins
            log.info("use hardware adresses");
            device.setRegister(MCP23S17.IOEXP_W, MCP23S17.IOCON, (byte) 0b00001000);

            // Konfiguration: Port B, Pin 0 als output
            // 0xFE = 0b11111110
            // Konfiguration: Port B, Pin 7,5,3,1,0 als output
            // 0xFC = 0b01010100
            log.info("define output pins");
            byte tempregister = (byte) 0xFF; // all as input --> default

            tempregister = device.setBitinByte(tempregister, false, config.getLEDPort());  // set as output



            tempregister = device.setBitinByte(tempregister, false, 1);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 3);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 5);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 7);  // set as output
            device.setRegister(MCP23S17.IOEXP_W_1, MCP23S17.IODIRB, tempregister);

            tempregister = (byte) 0xFF; // all as input --> default
            tempregister = device.setBitinByte(tempregister, false, 0);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 1);  // set as output
            device.setRegister(MCP23S17.IOEXP_W_2, MCP23S17.IODIRB, tempregister);

            motorPositionInc = 0L;
            motorPositionIncMin = 0L;
            motorPositionIncMax = MOTOR_INC_PER_REV*49/10;

            targetTemp = 600;
            flameIsOn = false;
        }

    }


    // TODO migrate to collector service
    public boolean isFlameControlButtonPushed() {
        byte register = device.getRegister(MCP23S17.IOEXP_R_1, MCP23S17.GPIOB);
        if (device.getBitinByte(register, 2)) { // get 3 bit -> gpio_b_2
            log.info("button pushed");
            return true;
        } else {
            return false;
        }

    }


    public void spi() {

        beep(50);
        motorControl(10);
        blinkLED();
        toggle230VRelais();


    }

    private void beep(long periods) {
        byte register;
        for (long i = 0; i < periods; i++) {
            // single step
            setRegisterOutput(config.beep,true);
            SleepUtil.sleepMillis(periodLenght / 2);
            setRegisterOutput(config.beep,false);
            SleepUtil.sleepMillis(periodLenght / 2);
        }
    }

    public void beepOn() {
        beep(50);
    }

    public void beepOff() {
        //TODO
    }

    public void beepForMilliSeconds (long milliseconds) {
        beep(milliseconds/periodLenght);
    }

    public void motorControl(long deltaPosition) {
        boolean dir = deltaPosition>= 0L;

        log.info("start motor");
        // enable drive
        setRegisterOutput(config.motor1En, false);

        // set cycle direction
        setRegisterOutput(config.motor1Dir, dir);

        // step control
        long periodLenghtMotor = 100; // ms
        for (int i = 0; i < deltaPosition; i++) {
            // single step
            // set pull high
            setRegisterOutput(config.motor1Pul, true);
            SleepUtil.sleepMillis(periodLenghtMotor / 2);
            // set pull low
            setRegisterOutput(config.motor1Pul, false);
            SleepUtil.sleepMillis(periodLenghtMotor / 2);
        }

        // optional disable STO - motor is  not locked
        setRegisterOutput(config.motor1En, true);

        log.info("end motor");
    }

    private void blinkLED() {
        // write output - switch LED on
        log.info("write values");
        setRegisterOutput(config.led1, true);

        // switch LED off
        SleepUtil.sleepSeconds(5);
        setRegisterOutput(config.led1, false);

        // switch LED on again
        SleepUtil.sleepSeconds(5);
        setRegisterOutput(config.led1, true);
        SleepUtil.sleepSeconds(5);
    }

    private void toggle230VRelais() {
        turn230VRelaisOn();
        SleepUtil.sleepSeconds(5);
        turn230VRelaisOff();
    }

    public void turn230VRelaisOn() {
        // write relais 230V on, switch to 0 -> negative switch logik
        setRegisterOutput(config.gfa230VRelais, false);

    }

    public void turn230VRelaisOff() {
        // write relais 230V off, switch to 1 -> negative switch logik
        setRegisterOutput(config.gfa230VRelais, true);
    }

    private void setRegisterOutput(PortPin element, boolean value) {
        byte opCodeRead;
        byte opCodeWrite;
        if (element.getMcpNumber()==1){
            opCodeRead=MCP23S17.IOEXP_R_1;
            opCodeWrite=MCP23S17.IOEXP_W_1;
        }else if(element.getMcpNumber()==2){
            opCodeRead=MCP23S17.IOEXP_R_2;
            opCodeWrite=MCP23S17.IOEXP_W_2;
        }else {
            log.error("undefined MCP!");
            opCodeRead=MCP23S17.IOEXP_R;    // TODO default --> how to manage?
            opCodeWrite=MCP23S17.IOEXP_W;
        }

        byte registerRead;
        byte registerWrite;
        if (element.getPort()=='A') {
            registerRead = MCP23S17.GPIOA;
            registerWrite = MCP23S17.OLATA;
        }else if (element.getPort()=='B'){
            registerRead = MCP23S17.GPIOB;
            registerWrite = MCP23S17.OLATB;
        }else {
            log.error("undefined port!");
            registerRead = MCP23S17.GPIOA;  // TODO default --> how to manage?
            registerWrite = MCP23S17.OLATA;
        }

        byte tempData = device.getRegister(opCodeRead, registerRead);
        tempData = device.setBitinByte(tempData, value, element.getPin()); // TODO How to Inject private?
        device.setRegister(opCodeWrite, registerWrite, tempData);
    }


    public void setTargetTempAdd(long deltaTargetTemp) {
        targetTemp+= deltaTargetTemp;
    }

    public void turnOnFlameControl(){
        //TODO
    }

    public void turnOffFlameControl(){
        //TODO
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping... {}", device);
        if (device != null) {
            log.info("The application is stopping... closing extension board");
            device.close();
        } else {
            log.info("The application is stopping... closing extension board ... but its not there");
        }
    }

}
