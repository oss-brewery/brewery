package com.pipiobjo.brewery.adapters.spiextensionboard;

import com.diozero.util.SleepUtil;
import com.pipiobjo.brewery.sensors.MCP23S17;
import io.quarkus.runtime.ShutdownEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * https://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 */
@Slf4j
@ApplicationScoped
@Data
public class SPIExtensionBoard {

    // Const for MCP23S17
    // Slaveadresse des MCP23S17
    private static final byte IOEXP_W = 0b01000000;  // Opcode write
    //private static final byte IOEXP_R = 0b01000001;  // Opcode read

    // first MCP23S17 with Adrr. 0bxxxx001x
    private static final byte IOEXP_W_1 = 0b01000010;  // Opcode write
    private static final byte IOEXP_R_1 = 0b01000011;  // Opcode read

    // second MCP23S17 with Adrr. 0bxxxx010x
    private static final byte IOEXP_W_2 = 0b01000100;  // Opcode write
    private static final byte IOEXP_R_2 = 0b01000101;  // Opcode read

    // Adressen der Register
    //private static final byte IODIRA = 0x00;   // Port A direction
    private static final byte IODIRB = 0x01;   // Port B direction
    //private static final byte GPIOA = 0x12;    // Port A input
    private static final byte GPIOB = 0x13;    // Port B input
    //private static final byte OLATA = 0x14;    // Port A output
    private static final byte OLATB = 0x15;    // Port B output
    private static final byte IOCON = 0x0A;    // Port A configuration
    //static byte IOCON = 0x0A;    // Port B configuration


    //chiselect == cs // ss -> 0 / 1
    static int chipselect = 0;
    static int freq = 12_500_000 - 1000;  // why -1000?

    //modes cpha oder cpol
    boolean lsbFirst = false; // leastSignifactBit kommt am ende
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
            device = new MCP23S17(0, chipselect, freq);

            // enable HAEN for enable addressing pins
            log.info("use hardware adresses");
            device.setRegister(IOEXP_W, IOCON, (byte) 0b00001000);

            // Konfiguration: Port B, Pin 0 als output
            // 0xFE = 0b11111110
            // Konfiguration: Port B, Pin 7,5,3,1,0 als output
            // 0xFC = 0b01010100
            log.info("define output pins");
            byte tempregister = (byte) 0xFF; // all as input --> default
            tempregister = device.setBitinByte(tempregister, false, 0);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 1);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 3);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 5);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 7);  // set as output
            device.setRegister(IOEXP_W_1, IODIRB, tempregister);

            tempregister = (byte) 0xFF; // all as input --> default
            tempregister = device.setBitinByte(tempregister, false, 0);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 1);  // set as output
            device.setRegister(IOEXP_W_2, IODIRB, tempregister);

            motorPositionInc = 0L;
            motorPositionIncMin = 0L;
            motorPositionIncMax = MOTOR_INC_PER_REV*49/10;

            targetTemp = 600;
            flameIsOn = false;
        }

    }


    // TODO migrate to collector service
    public boolean isFlameControlButtonPushed() {
        byte register = device.getRegister(IOEXP_R_1, GPIOB, (byte) 0x00);
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
            // set pull high
            register = device.getRegister(IOEXP_R_2, OLATB, (byte) 0x00);
            register = device.setBitinByte(register, true, 1);
            device.setRegister(IOEXP_W_2, OLATB, register);
            SleepUtil.sleepMillis(periodLenght / 2);
            // set pull low
            register = device.getRegister(IOEXP_R_2, OLATB, (byte) 0x00);
            register = device.setBitinByte(register, false, 1);
            device.setRegister(IOEXP_W_2, OLATB, register);
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
        // motor controller
        // enable driver
        byte register = device.getRegister(IOEXP_R_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, false, 7);
        device.setRegister(IOEXP_W_1, OLATB, register);

        // set cycle direction
        // open - right
        register = device.getRegister(IOEXP_R_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, dir, 5);
        device.setRegister(IOEXP_W_1, OLATB, register);

        // step control
        long periodLenghtMotor = 100; // ms
        for (int i = 0; i < deltaPosition; i++) {
            // single step
            // set pull high
            register = device.getRegister(IOEXP_R_1, OLATB, (byte) 0x00);
            register = device.setBitinByte(register, true, 3);
            device.setRegister(IOEXP_W_1, OLATB, register);
            SleepUtil.sleepMillis(periodLenghtMotor / 2);
            // set pull low
            register = device.setBitinByte(register, false, 3);
            device.setRegister(IOEXP_W_1, OLATB, register);
            SleepUtil.sleepMillis(periodLenghtMotor / 2);
        }

        // optional disable STO - motor is  not locked
        register = device.getRegister(IOEXP_R_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, true, 7);
        device.setRegister(IOEXP_W_1, OLATB, register);


        log.info("end motor");
    }

    private void blinkLED() {
        // write output - switch LED on
        log.info("write values");
        byte register = device.getRegister(IOEXP_R_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, true, 0);
        device.setRegister(IOEXP_W_1, OLATB, register);

        // switch LED off
        SleepUtil.sleepSeconds(5);
        register = device.getRegister(IOEXP_R_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, false, 0);
        device.setRegister(IOEXP_W_1, OLATB, register);

        // switch LED on again
        SleepUtil.sleepSeconds(5);
        register = device.getRegister(IOEXP_R_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, true, 0);
        device.setRegister(IOEXP_W_1, OLATB, register);
        SleepUtil.sleepSeconds(5);
    }

    private void toggle230VRelais() {
        turn230VRelaisOn();
        SleepUtil.sleepSeconds(5);
        turn230VRelaisOff();
    }

    public void turn230VRelaisOn() {
        byte register;// write relais 230V on, switch to 0 -> negative switch logik
        register = device.getRegister(IOEXP_R_1, GPIOB, (byte) 0x00);
        register = device.setBitinByte(register, false, 1);
        device.setRegister(IOEXP_W_1, OLATB, register);

    }

    public void turn230VRelaisOff() {
        byte register;// write relais 230V on, switch to 0 -> negative switch logik
        // write relais 230V off, switch to 1
        register = device.getRegister(IOEXP_R_1, GPIOB, (byte) 0x00);
        register = device.setBitinByte(register, true, 1);
        device.setRegister(IOEXP_W_1, OLATB, register);
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
