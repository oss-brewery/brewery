package com.pipiobjo.brewery.sensors;

import com.diozero.api.SensorInterface;
import com.diozero.api.SpiClockMode;
import com.diozero.api.SpiDevice;
import com.diozero.util.RuntimeIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MCP23S17 Version 1.0
 * https://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 */
public class MCP23S17  implements SensorInterface {
    // Const for MCP23S17
    // Slaveadresse des MCP23S17
    public static final byte IOEXP_W = 0b01000000;  // Opcode write
    public static final byte IOEXP_R = 0b01000001;  // Opcode read

    // first MCP23S17 with Adrr. 0bxxxx001x
    public static final byte IOEXP_W_1 = 0b01000010;  // Opcode write
    public static final byte IOEXP_R_1 = 0b01000011;  // Opcode read

    // second MCP23S17 with Adrr. 0bxxxx010x
    public static final byte IOEXP_W_2 = 0b01000100;  // Opcode write
    public static final byte IOEXP_R_2 = 0b01000101;  // Opcode read

    // Adressen der Register
    public static final byte IODIRA = 0x00;   // Port A direction
    public static final byte IODIRB = 0x01;   // Port B direction
    public static final byte GPIOA = 0x12;    // Port A input
    public static final byte GPIOB = 0x13;    // Port B input
    public static final byte OLATA = 0x14;    // Port A output
    public static final byte OLATB = 0x15;    // Port B output
    public static final byte IOCON = 0x0A;    // Port A configuration
    //public byte IOCON = 0x0A;    // Port B configuration



    private static final Logger log = LoggerFactory.getLogger(MCP23S17.class);
    private static final int MAX_SPI_CLOCK_FREQUENCY = 12_500_000;

    // datasheet says always at the end
    private static final boolean LSB_FIRST = false;

    private SpiDevice device;

    /**
     *
     * @param controller
     * @param chipSelect
     * @param frequence
     */
    public MCP23S17(int controller, int chipSelect, int frequence) {
        if(frequence > MAX_SPI_CLOCK_FREQUENCY){
            throw new UnsupportedOperationException("the given frequency is higher than supported max:" + MAX_SPI_CLOCK_FREQUENCY);
        }
        try{
        device = new SpiDevice(controller, chipSelect, frequence, SpiClockMode.MODE_0, LSB_FIRST);
        }catch (Exception e){
            log.error("Error while init device ", e);
            throw e;
        }
    }


    @Override
    public void close() {
        if (device != null) {
            log.info("closing spi bus device {}", device);
            device.close();
        }
    }

    public void setRegister(byte deviceOpcode, byte register, byte spiMCPData) throws RuntimeIOException {
        byte[] write = new byte[]{deviceOpcode,register,spiMCPData};
        device.writeAndRead(write);
    }

    public byte setBitinByte(byte register, boolean value,int bitNum) throws RuntimeIOException {
        if (value) {
            // set high
            return (byte) (register&0xff | ((byte) 0x01<<bitNum));
        }
        else {
            // set low
            return (byte) (register&0xff & ~((byte) 0x01<<bitNum));
        }
    }

    public boolean getBitinByte(byte register,int bitNum) throws RuntimeIOException {
        return ((byte) (register&0xff & ((byte) 0x01<<bitNum))>0);
    }

    /**
     *
     * @param deviceOpcode
     * @param register
     * @param spiMCPData
     * @return return the third byte of the byte[]
     * @throws RuntimeIOException
     */
    public byte getRegister(byte deviceOpcode, byte register) throws RuntimeIOException {
        byte[] write = new byte[]{deviceOpcode,register,(byte) 0x00};
        byte[] bytes = device.writeAndRead(write);
        return bytes[2];


    }

}
