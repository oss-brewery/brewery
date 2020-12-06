#!/usr/bin/python
import RPi.GPIO as GPIO
import time
#import smbus
import sys, spidev

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
spi.open(SPIBUS, SPIDEVICE) # open spi port 0, device (CS) 0

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
# all set as output
spi.xfer([IOEXPw_1, IODIRA, 0x00])
spi.xfer([IOEXPw_1, IODIRB, 0x00])
spi.xfer([IOEXPw_2, IODIRA, 0x00])
spi.xfer([IOEXPw_2, IODIRB, 0x00])

PWM_freq = 150.0 #

try:
    i = 0
    elapsed = 0.0;
    while(i<10000):
        # first MCP
        t = time.time()

        # Read from Port A
        porttemp = spi.xfer([IOEXPr_1, OLATA, 0])[2]
        porttemp ^= 0xFF    # Toggle all bits
        # Write to Port A
        spi.xfer([IOEXPw_1, OLATA, porttemp])

        # Read from Port B
        porttemp = spi.xfer([IOEXPr_1, OLATB, 0])[2]
        porttemp ^= 0xFF    # Toggle all bits
        # Write to Port B
        spi.xfer([IOEXPw_1, OLATB, porttemp])



        # first MCP
        # Read from Port A
        porttemp = spi.xfer([IOEXPr_2, OLATA, 0])[2]
        porttemp ^= 0xFF    # Toggle all bits
        # Write to Port A
        spi.xfer([IOEXPw_2, OLATA, porttemp])

        # Read from Port B
        porttemp = spi.xfer([IOEXPr_2, OLATB, 0])[2]
        porttemp ^= 0xFF    # Toggle all bits
        # Write to Port B
        spi.xfer([IOEXPw_2, OLATB, porttemp])

        elapsed = elapsed + time.time() - t
        i= i+1


    print(elapsed/10000)
    print("Bye!")

    #time.sleep(0.006)
except KeyboardInterrupt:
    #spi.close()
    print("Bye!")

#def pwm_fun():
# empty