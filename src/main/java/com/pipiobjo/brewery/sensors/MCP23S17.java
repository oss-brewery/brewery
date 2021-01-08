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
    public byte getRegister(byte deviceOpcode, byte register, byte spiMCPData) throws RuntimeIOException {
        byte[] write = new byte[]{deviceOpcode,register,spiMCPData};
        byte[] bytes = device.writeAndRead(write);
        return bytes[2];


    }

}
