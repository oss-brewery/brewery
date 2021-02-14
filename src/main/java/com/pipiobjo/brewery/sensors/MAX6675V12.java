package com.pipiobjo.brewery.sensors;

import com.diozero.api.SpiClockMode;
import com.diozero.api.SpiDevice;
import com.diozero.devices.ThermometerInterface;
import com.diozero.api.RuntimeIOException;
import com.pipiobjo.brewery.adapters.flametemp.FlameTempDeviceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * MAX6675 Version 1.2
 * https://datasheets.maximintegrated.com/en/ds/MAX6675.pdf
 */
public class MAX6675V12 implements ThermometerInterface {
    public static final BigDecimal MAX_TEMPRATURE = BigDecimal.valueOf(1023.75);
    private static final Logger log = LoggerFactory.getLogger(FlameTempDeviceAdapter.class);
    private static final int MAX_SPI_CLOCK_FREQUENCY = 4_300_000;
    // 12 bit range of sensor and tempratur range of 1024°C -> (2^12-1)÷1023.75
    private static final BigDecimal segmentationFactor = (new BigDecimal(2).pow(12).subtract(new BigDecimal(1))).divide(MAX_TEMPRATURE,8, RoundingMode.HALF_UP);
    // datasheet says always at the end
    private static final boolean LSB_FIRST = false;

    private SpiDevice device;

    /**
     *
     * @param controller
     * @param chipSelect
     * @param frequence
     * @param mode cpha / cpol - https://en.wikipedia.org/wiki/Serial_Peripheral_Interface
     */
    public MAX6675V12(int controller, int chipSelect, int frequence, SpiClockMode mode) {
        if(frequence > MAX_SPI_CLOCK_FREQUENCY){
            throw new UnsupportedOperationException("the given frequency is higher than supported max:" + MAX_SPI_CLOCK_FREQUENCY);
        }
        try{
        device = new SpiDevice(controller, chipSelect, frequence, mode, LSB_FIRST);
        }catch (Exception e){
            log.error("Error while init temp sensor ", e);
            close();
            throw e;
        }
    }


    @Override
    public void close() {
        if (device != null) {
            log.info("closing");
            device.close();
        }
    }

    @Override
    public float getTemperature() throws RuntimeIOException {
        BigInteger binaryValue = new BigInteger(readData());

        return convertToTemprature(binaryValue);
    }

    // 672 -> 21.01
    private float convertToTemprature(BigInteger binaryValue) {
        // remove the 3 fields state, device id, thermocoupleinput
        BigDecimal cleared = new BigDecimal(binaryValue.shiftRight(3));

        // and device by segmentation factor
        BigDecimal temperature = cleared.divide(segmentationFactor, 4, RoundingMode.HALF_UP);

        float roundedTemp = temperature.setScale(2, RoundingMode.HALF_UP).floatValue(); // resolution 0.25°C
        log.debug("found temperature bigdec{} float{}", temperature, roundedTemp);
        return roundedTemp;
    }

    private byte[] readData(){
        byte[] write = new byte[]{0x0000, 0x0000};
        byte[] read =device.writeAndRead(write);
        return read;
    }
}
