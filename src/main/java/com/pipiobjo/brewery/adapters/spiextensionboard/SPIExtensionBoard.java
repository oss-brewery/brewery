package com.pipiobjo.brewery.adapters.spiextensionboard;

import com.diozero.util.SleepUtil;
import com.pipiobjo.brewery.sensors.MCP23S17;
import io.quarkus.runtime.ShutdownEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * https://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 */
@Slf4j
@ApplicationScoped
public class SPIExtensionBoard {

    // Const for MCP23S17
    // Slaveadresse des MCP23S17
    static byte IOEXPw = 0b01000000;  // Opcode write
    static byte IOEXPr = 0b01000001;  // Opcode read

    // first MCP23S17 with Adrr. 0bxxxx001x
    static byte IOEXPw_1 = 0b01000010;  // Opcode write
    static byte IOEXPr_1 = 0b01000011;  // Opcode read

    // second MCP23S17 with Adrr. 0bxxxx010x
    static byte IOEXPw_2 = 0b01000100;  // Opcode write
    static byte IOEXPr_2 = 0b01000101;  // Opcode read

    // Adressen der Register
    static byte IODIRA = 0x00;   // Port A direction
    static byte IODIRB = 0x01;   // Port B direction
    static byte GPIOA = 0x12;   // Port A input
    static byte GPIOB = 0x13;    // Port B input
    static byte OLATA = 0x14;    // Port A output
    static byte OLATB = 0x15;    // Port B output
    static byte IOCON = 0x0A;    // Port A configuration
    //static byte IOCON = 0x0A;    // Port B configuration


    //chiselect == cs // ss -> 0 / 1
    static int chipselect = 0;
    static int freq = 12_500_000 - 1000;

    //modes cpha oder cpol
    boolean lsbFirst = false; // leastSignifactBit kommt am ende
    private MCP23S17 device = null;


    public SPIExtensionBoard(){
        if (device == null) {
            log.info("init extension board device");
            device = new MCP23S17(0, chipselect, freq);

            // enable HAEN for enable addressing pins
            log.info("use hardware adresses");
            device.setRegister(IOEXPw, IOCON, (byte) 0b00001000);

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
            device.setRegister(IOEXPw_1, IODIRB, tempregister);

            tempregister = (byte) 0xFF; // all as input --> default
            tempregister = device.setBitinByte(tempregister, false, 0);  // set as output
            tempregister = device.setBitinByte(tempregister, false, 1);  // set as output
            device.setRegister(IOEXPw_2, IODIRB, tempregister);
        }

    }


    // TODO migrate to collector service
    public boolean isFlameControlButtonPushed() {
        byte register = device.getRegister(IOEXPr_1, GPIOB, (byte) 0x00);
        if (device.getBitinByte(register, 2)) { // get 3 bit -> gpio_b_2
            log.info("button pushed");
            return true;
        } else {
            return false;
        }

    }


    public void spi() {

        beep();
        motorControl();
        blinkLED();
        toggle230VRelais();


    }

    private void beep() {

        byte register = device.getRegister(IOEXPw_2, OLATB, (byte) 0x00);
        long periodLenght = 10; // ms
        for (int i = 0; i < 50; i++) {
            // single step
            // set pull high
            register = device.getRegister(IOEXPw_2, OLATB, (byte) 0x00);
            register = device.setBitinByte(register, true, 1);
            device.setRegister(IOEXPw_2, OLATB, register);
            SleepUtil.sleepMillis(periodLenght / 2);
            // set pull low
            register = device.getRegister(IOEXPw_2, OLATB, (byte) 0x00);
            register = device.setBitinByte(register, false, 1);
            device.setRegister(IOEXPw_2, OLATB, register);
            SleepUtil.sleepMillis(periodLenght / 2);
        }
    }

    private void motorControl() {
        log.info("start motor");
        // motor controller
        // enable driver
        byte register = device.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, false, 7);
        device.setRegister(IOEXPw_1, OLATB, register);

        // set cycle direction
        // open - right

        register = device.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, false, 5);
        device.setRegister(IOEXPw_1, OLATB, register);

        // close - left
//        register = extensionBoard.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
//        register = extensionBoard.setBitinByte(register,true,5);
//        extensionBoard.setRegister(IOEXPw_1, OLATB, register);


        // step control
        long periodLenght = 100; // ms
        for (int i = 0; i < 50; i++) {
            // single step
            // set pull high
            register = device.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
            register = device.setBitinByte(register, true, 3);
            device.setRegister(IOEXPw_1, OLATB, register);
            SleepUtil.sleepMillis(periodLenght / 2);
            // set pull low
            register = device.setBitinByte(register, false, 3);
            device.setRegister(IOEXPw_1, OLATB, register);
            SleepUtil.sleepMillis(periodLenght / 2);
        }

        // optional disable STO - motor is  not locked
        register = device.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, true, 7);
        device.setRegister(IOEXPw_1, OLATB, register);


        log.info("end motor");
    }

    private void blinkLED() {
        // write output - switch LED on
        log.info("write values");
        byte register = device.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, true, 0);
        device.setRegister(IOEXPw_1, OLATB, register);

        // switch LED off
        SleepUtil.sleepSeconds(5);
        register = device.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, false, 0);
        device.setRegister(IOEXPw_1, OLATB, register);

        // switch LED on again
        SleepUtil.sleepSeconds(5);
        register = device.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = device.setBitinByte(register, true, 0);
        device.setRegister(IOEXPw_1, OLATB, register);
        SleepUtil.sleepSeconds(5);
    }

    private void toggle230VRelais() {
        byte register;// write relais 230V on, switch to 0 -> negative switch logik
        register = device.getRegister(IOEXPr_1, GPIOB, (byte) 0x00);
        register = device.setBitinByte(register, false, 1);
        device.setRegister(IOEXPw_1, OLATB, register);

        SleepUtil.sleepSeconds(5);
        // write relais 230V off, switch to 1
        register = device.getRegister(IOEXPr_1, GPIOB, (byte) 0x00);
        register = device.setBitinByte(register, true, 1);
        device.setRegister(IOEXPw_1, OLATB, register);
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
