package com.pipiobjo.brewery.adapters;

import com.diozero.devices.PCF8574;

import javax.enterprise.context.ApplicationScoped;





@ApplicationScoped
public class ExtensionBoard_OLD {

    public ExtensionBoard_OLD(){


/**
 * i2cdetect -y 1
 *      0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
 * 00:          -- -- -- -- -- -- -- -- -- -- -- -- --
 * 10: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 20: 20 21 -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 30: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 40: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 50: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 60: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 70: -- -- -- -- -- -- -- --
 *
 * 0x20 -> 32
 *
 */

// i2cset -y 1 0x20 0x00        # aus
// i2cset -y 1 0x20 0xff        # an

        PCF8574 pcf8574 = new PCF8574(1, 0x20, 7);
    }




}
