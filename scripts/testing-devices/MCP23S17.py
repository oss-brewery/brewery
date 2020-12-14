#!/usr/bin/python
import RPi.GPIO as GPIO
import time
import sys, spidev

# pip install spidev
# Slaveadresse des MCP23S17
# IOEXP = 0x20
SPIBUS = 0     # SPI0
SPIDEVICE = 0  # CE0
IOEXPw = 0b01000000  # Opcode write
IOEXPr = 0b01000001  # Opcode read

# first MCP23S17 with Adrr. 0bxxxx001x
IOEXPw_1 = 0b01000010  # Opcode write
IOEXPr_1 = 0b01000011  # Opcode read

# second MCP23S17 with Adrr. 0bxxxx010x
IOEXPw_2 = 0b01000100  # Opcode write
IOEXPr_2 = 0b01000101  # Opcode read

# Adressen der Register
IODIRA = 0x00   # Port A direction
IODIRB = 0x01   # Port B direction
GPIOA = 0x12    # Port A input
GPIOB = 0x13    # Port B input
OLATA = 0x14    # Port A output
OLATB = 0x15    # Port B output
IOCON = 0x0A    # Port A configuration
IOCON = 0x0A    # Port B configuration


#SPI BASESETTINGS
spi = spidev.SpiDev() # create spi object
spi.open(0, 0) # open spi port 0, device (CS) 0

Raspi_freq = int(250e6)
CLKDIV = 20 #  max speed --> CLKDIV = 6

spi.max_speed_hz=(int(Raspi_freq/CLKDIV))
spi.mode = 0b00
spi.lsbfirst = False;

# Konfiguration: Port A, Pin 0 als output
# 0xFE = 0b11111110
#spi.xfer([IOEXPw, IOCON, 0b00000000]) # disable HAEN for enable addressing pins
spi.xfer([IOEXPw, IOCON, 0b00001000]) # enable HAEN for enable addressing pins

# Konfiguration: Port A, Pin 0 als output
# 0xFE = 0b11111110
spi.xfer([IOEXPw_1, IODIRB, 0xFE])
spi.xfer([IOEXPw_2, IODIRB, 0xFE])

PWM_freq = 150.0 #

try:
    while(True):

        for dc in range(0, 101, 2):
            dc = dc/100.0
            for i in range(0,3,1):
                 # set pin high
                portb = spi.xfer([IOEXPr_1, OLATB, 0])[2]
                portb |= 0x01    # Veroderung mit 0x01
                spi.xfer([IOEXPw_1, OLATB, portb])

                portb = spi.xfer([IOEXPr_2, OLATB, 0])[2]
                portb &= 0xFE    # neg last bit
                spi.xfer([IOEXPw_2, OLATB, portb])

                time.sleep(dc/PWM_freq)

                # set pin low
                portb = spi.xfer([IOEXPr_1, OLATB, 0])[2]
                portb &= 0xFE    # neg last bit
                spi.xfer([IOEXPw_1, OLATB, portb])

                portb = spi.xfer([IOEXPr_2, OLATB, 0])[2]
                portb |= 0x01    # neg last bit
                spi.xfer([IOEXPw_2, OLATB, portb])

                time.sleep((1.0-dc)/PWM_freq)

        for dc in range(100, -1, -2):
            dc = dc/100.0
            for i in range(0,3,1):
                # set pin high
                portb = spi.xfer([IOEXPr_1, OLATB, 0])[2]
                portb |= 0x01    # Veroderung mit 0x01
                spi.xfer([IOEXPw_1, OLATB, portb])

                portb = spi.xfer([IOEXPr_2, OLATB, 0])[2]
                portb &= 0xFE    # neg last bit
                spi.xfer([IOEXPw_2, OLATB, portb])

                time.sleep(dc/PWM_freq)

                # set pin low
                portb = spi.xfer([IOEXPr_1, OLATB, 0])[2]
                portb &= 0xFE    # neg last bit
                spi.xfer([IOEXPw_1, OLATB, portb])

                portb = spi.xfer([IOEXPr_2, OLATB, 0])[2]
                portb |= 0x01    # neg last bit
                spi.xfer([IOEXPw_2, OLATB, portb])

                time.sleep((1.0-dc)/PWM_freq)


            #

        #time.sleep(0.006)
except KeyboardInterrupt:
    #spi.close()
    print("Bye!")

#def pwm_fun():
    # empty


