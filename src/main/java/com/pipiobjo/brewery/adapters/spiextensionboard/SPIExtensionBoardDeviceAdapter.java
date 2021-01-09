package com.pipiobjo.brewery.adapters.spiextensionboard;

import com.diozero.util.SleepUtil;
import com.pipiobjo.brewery.sensors.MCP23S17;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * https://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 */
@Slf4j
public class SPIExtensionBoardDeviceAdapter implements SPIExtensionBoardAdapter {

    SPIExtensionBoardAdapterConfigProperties config;

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

    public SPIExtensionBoardDeviceAdapter(SPIExtensionBoardAdapterConfigProperties config) {
        this.config = config;
        if (device == null) {
            log.info("init extension board device");
            device = new MCP23S17(config.getControllerId(), config.getChipselect(), config.getFreq());

            // enable HAEN for enable addressing pins
            log.info("use hardware adresses");
            device.setRegister(MCP23S17.IOEXP_W, MCP23S17.IOCON, (byte) 0b00001000);

            log.info("define output pins");
            Map<SPIExtensionBoardAdapterConfigProperties.GPIOMCP, PortPinConfigProperties> gpiomcpMap = config.getGPIOMCPMap();
            AtomicReference<Byte> tempregister = new AtomicReference<>((byte) 0);
            List<PortPinConfigProperties> mcp1portA = gpiomcpMap.values().stream()
                    .filter(portPin -> portPin.getMcpNumber().get() == 1)
                    .filter(portPin -> portPin.getPort().get() == 'A')
                    .collect(Collectors.toList());

            List<PortPinConfigProperties> mcp1portB = gpiomcpMap.values().stream()
                    .filter(portPin -> portPin.getMcpNumber().get() == 1)
                    .filter(portPin -> portPin.getPort().get() == 'B')
                    .collect(Collectors.toList());

            List<PortPinConfigProperties> mcp2portA = gpiomcpMap.values().stream()
                    .filter(portPin -> portPin.getMcpNumber().get() == 2)
                    .filter(portPin -> portPin.getPort().get() == 'A')
                    .collect(Collectors.toList());

            List<PortPinConfigProperties> mcp2portB = gpiomcpMap.values().stream()
                    .filter(portPin -> portPin.getMcpNumber().get() == 2)
                    .filter(portPin -> portPin.getPort().get() == 'B')
                    .collect(Collectors.toList());


            tempregister.set((byte) 0xFF); // all as input --> default
            mcp1portA.forEach(portPin -> {
                tempregister.set(device.setBitinByte(tempregister.get(), false, portPin.getPin().get()));  // set as output
            });
            device.setRegister(MCP23S17.IOEXP_W_1, MCP23S17.IODIRA, tempregister.get());

            tempregister.set((byte) 0xFF); // all as input --> default
            mcp1portB.forEach(portPin -> {
                tempregister.set(device.setBitinByte(tempregister.get(), false, portPin.getPin().get()));  // set as output
            });
            device.setRegister(MCP23S17.IOEXP_W_1, MCP23S17.IODIRB, tempregister.get());

            tempregister.set((byte) 0xFF); // all as input --> default
            mcp2portA.forEach(portPin -> {
                tempregister.set(device.setBitinByte(tempregister.get(), false, portPin.getPin().get()));  // set as output
            });
            device.setRegister(MCP23S17.IOEXP_W_2, MCP23S17.IODIRA, tempregister.get());

            tempregister.set((byte) 0xFF); // all as input --> default
            mcp2portB.forEach(portPin -> {
                tempregister.set(device.setBitinByte(tempregister.get(), false, portPin.getPin().get()));  // set as output
            });
            device.setRegister(MCP23S17.IOEXP_W_2, MCP23S17.IODIRB, tempregister.get());

            motorPositionInc = 0L;
            motorPositionIncMin = 0L;
            motorPositionIncMax = MOTOR_INC_PER_REV * 49 / 10;

            flameIsOn = false;
        }

    }


    @Override
    public boolean isFlameControlButtonPushed() {
        byte register = device.getRegister(MCP23S17.IOEXP_R_1, MCP23S17.GPIOB);
        if (device.getBitinByte(register, 2)) { // get 3 bit -> gpio_b_2
            log.info("button pushed");
            flameIsOn = false;
            return true;
        } else {
            return false;
        }

    }


    @Override
    public void beepOn() {
        beep(50);
    }

    @Override
    public void beepOff() {
        //TODO
    }

    @Override
    public void beepForMilliSeconds(long milliseconds) {
        beep(milliseconds / periodLenght);
    }

    private void beep(long periods) {
        for (long i = 0; i < periods; i++) {
            // single step
            setRegisterOutput(config.getBeep(), true);
            SleepUtil.sleepMillis(periodLenght / 2);
            setRegisterOutput(config.getBeep(), false);
            SleepUtil.sleepMillis(periodLenght / 2);
        }
    }

    @Override
    public void motorControl(long deltaPosition) {
        boolean dir = deltaPosition >= 0L;

        log.info("start motor");
        // enable drive
        setRegisterOutput(config.getMotor1En(), false);

        // set cycle direction
        setRegisterOutput(config.getMotor1Dir(), dir);

        // step control
        long periodLenghtMotor = 100; // ms
        for (int i = 0; i < deltaPosition; i++) {
            // single step
            // set pull high
            setRegisterOutput(config.getMotor1Pul(), true);
            SleepUtil.sleepMillis(periodLenghtMotor / 2);
            // set pull low
            setRegisterOutput(config.getMotor1Pul(), false);
            SleepUtil.sleepMillis(periodLenghtMotor / 2);
        }

        // optional disable STO - motor is  not locked
        setRegisterOutput(config.getMotor1En(), true);

        log.info("end motor");
    }

//    private void blinkLED() {
//        // write output - switch LED on
//        log.info("write values");
//        setRegisterOutput(config.getLed1(), true);
//
//        // switch LED off
//        SleepUtil.sleepSeconds(5);
//        setRegisterOutput(config.getLed1(), false);
//
//        // switch LED on again
//        SleepUtil.sleepSeconds(5);
//        setRegisterOutput(config.getLed1(), true);
//        SleepUtil.sleepSeconds(5);
//    }
//
//    private void toggle230VRelais() {
//        turn230VRelaisOn();
//        SleepUtil.sleepSeconds(5);
//        turn230VRelaisOff();
//    }

    @Override
    public void turn230VRelaisOn() {
        // write relais 230V on, switch to 0 -> negative switch logic
        setRegisterOutput(config.getGfa230VRelais(), false);
        flameIsOn = true;

    }

    @Override
    public void turn230VRelaisOff() {
        // write relais 230V off, switch to 1 -> negative switch logik
        setRegisterOutput(config.getGfa230VRelais(), true);
    }

    @Override
    public void close() {
        log.info("The application is stopping... {}", device);
        if (device != null) {
            log.info("The application is stopping... closing extension board");
            device.close();
        } else {
            log.info("The application is stopping... closing extension board ... but its not there");
        }
    }

    @Override
    public boolean isFlameOn() {
        return flameIsOn;
    }

    private void setRegisterOutput(PortPinConfigProperties element, boolean value) {
        byte opCodeRead;
        byte opCodeWrite;
        if (element.getMcpNumber().get() == 1) {
            opCodeRead = MCP23S17.IOEXP_R_1;
            opCodeWrite = MCP23S17.IOEXP_W_1;
        } else if (element.getMcpNumber().get() == 2) {
            opCodeRead = MCP23S17.IOEXP_R_2;
            opCodeWrite = MCP23S17.IOEXP_W_2;
        } else {
            log.error("undefined MCP!");
            opCodeRead = MCP23S17.IOEXP_R;    // TODO default --> how to manage?
            opCodeWrite = MCP23S17.IOEXP_W;
        }

        byte registerRead;
        byte registerWrite;
        if (element.getPort().get() == 'A') {
            registerRead = MCP23S17.GPIOA;
            registerWrite = MCP23S17.OLATA;
        } else if (element.getPort().get() == 'B') {
            registerRead = MCP23S17.GPIOB;
            registerWrite = MCP23S17.OLATB;
        } else {
            log.error("undefined port!");
            registerRead = MCP23S17.GPIOA;  // TODO default --> how to manage?
            registerWrite = MCP23S17.OLATA;
        }

        byte tempData = device.getRegister(opCodeRead, registerRead);
        tempData = device.setBitinByte(tempData, value, element.getPin().get()); // TODO How to Inject private?
        device.setRegister(opCodeWrite, registerWrite, tempData);
    }


    public void turnOnFlameControl() {
        //TODO
    }

    public void turnOffFlameControl() {
        //TODO
    }



}
