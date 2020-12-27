package com.pipiobjo.brewery.sensors;

import com.diozero.api.SensorInterface;
import com.diozero.api.SpiClockMode;
import com.diozero.api.SpiDevice;
import com.diozero.util.RuntimeIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * MCP23S17 Version 1.0
 * https://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 */
public class MCP23S17  implements SensorInterface {
    private static final Logger log = LoggerFactory.getLogger(MCP23S17.class);
    private static final int MAX_SPI_CLOCK_FREQUENCY = 12_500_000;

    // datasheet says always at the end
    private static final boolean lsbFirst = false;

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
        device = new SpiDevice(controller, chipSelect, frequence, SpiClockMode.MODE_0, lsbFirst);
        }catch (Exception e){
            log.error("Error while init device ", e);
//            device.close();
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

    public void setRegister(byte deviceOpcode, byte Register, byte spiMCP_Data) throws RuntimeIOException {
        byte[] write = new byte[]{deviceOpcode,Register,spiMCP_Data};
        byte[] bytes = device.writeAndRead(write);
        BigInteger binaryValue = new BigInteger(bytes);


    }

    /**
     *
     * @param deviceOpcode
     * @param Register
     * @param spiMCP_Data
     * @return return the third byte of the byte[]
     * @throws RuntimeIOException
     */
    public byte getRegister(byte deviceOpcode, byte Register, byte spiMCP_Data) throws RuntimeIOException {
        byte[] write = new byte[]{deviceOpcode,Register,spiMCP_Data};
        byte[] bytes = device.writeAndRead(write);
        return bytes[2];


    }




    private byte[] readData(){
        byte[] write = new byte[]{0x0000, 0x0000};
        byte[] read =device.writeAndRead(write);
        return read;
    }
}
