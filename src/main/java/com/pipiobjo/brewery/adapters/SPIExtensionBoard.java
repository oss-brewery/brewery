package com.pipiobjo.brewery.adapters;

import com.diozero.api.GpioPullUpDown;
import com.diozero.devices.Button;
import com.diozero.util.SleepUtil;
import com.pipiobjo.brewery.sensors.MCP23S17;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.scheduler.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.concurrent.TimeUnit;

/**
 * https://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 */
@ApplicationScoped
public class SPIExtensionBoard {
    Logger log = LoggerFactory.getLogger(SPIExtensionBoard.class);

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
    private MCP23S17 extensionBoard = null;
//    private static final MCP23S17 extensionBoard = new MCP23S17(0,chipselect, freq); // working


    @PostConstruct
    void init() {
        if(extensionBoard == null){
            log.info("init extension board device");
            extensionBoard = new MCP23S17(0,chipselect, freq);

            // enable HAEN for enable addressing pins
            log.info("use hardware adresses");
            extensionBoard.setRegister(IOEXPw, IOCON, (byte)0b00001000);

            // Konfiguration: Port B, Pin 0 als output
            // 0xFE = 0b11111110
            // Konfiguration: Port B, Pin 7,5,3,1,0 als output
            // 0xFC = 0b01010100
            log.info("define output pins");
            extensionBoard.setRegister(IOEXPw_1, IODIRB, (byte)0x54);
            extensionBoard.setRegister(IOEXPw_2, IODIRB, (byte)0xFE);

        }

    }

    @Scheduled(every="10s", delayUnit = TimeUnit.MILLISECONDS)
    void increment() {
        byte registerState = extensionBoard.getRegister(IOEXPr_1, GPIOB, (byte) 0x00);
        if((registerState & 0x04) > 0){ // get 3 bit -> gpio_b_2
            log.info("button pushed");
        }else{
            log.info("button released");
        }

    }



    public void spi(){

        blinkLED();

        toggle230VRelais();


    }

    private void blinkLED() {
        // write output - switch LED on
        log.info("write values");
        byte register = extensionBoard.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = (byte) (register | (byte)0x01);
        extensionBoard.setRegister(IOEXPw_1, OLATB, register);

        // switch LED off
        SleepUtil.sleepSeconds(5);
        register = extensionBoard.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = (byte) (register & (byte)0xFE);
        extensionBoard.setRegister(IOEXPw_1, OLATB, register);

        // switch LED on again
        SleepUtil.sleepSeconds(5);
        register = extensionBoard.getRegister(IOEXPr_1, OLATB, (byte) 0x00);
        register = (byte) (register | (byte)0x01);
        extensionBoard.setRegister(IOEXPw_1, OLATB, register);
        SleepUtil.sleepSeconds(5);
    }

    private void toggle230VRelais() {
        byte register;// write relais 230V on, switch to 0 -> negative switch logik
        register = extensionBoard.getRegister(IOEXPr_1, GPIOB, (byte) 0x00);
        register = (byte) (register & (byte)0xFD);
        extensionBoard.setRegister(IOEXPw_1, OLATB, register);

        SleepUtil.sleepSeconds(5);
        // write relais 230V off, switch to 1
        register = extensionBoard.getRegister(IOEXPr_1, GPIOB, (byte) 0x00);
        register = (byte) (register | (byte)0x02);
        extensionBoard.setRegister(IOEXPw_1, OLATB, register);
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping... {}", extensionBoard);
        if(extensionBoard !=null){
            log.info("The application is stopping... closing extension board");
            extensionBoard.close();
        }else{
            log.info("The application is stopping... closing extension board ... but its not there");
        }
    }

}
