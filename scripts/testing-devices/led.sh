#!/bin/bash


# Common path for all GPIO access
BASE_GPIO_PATH=/sys/class/gpio
ON="1"
OFF="0"

LED_PIN=24


# Utility function to export a pin if not already exported
exportPin()
{
  if [ ! -e $BASE_GPIO_PATH/gpio$1 ]; then
    echo "$1" > $BASE_GPIO_PATH/export
  fi
}


# Utility function to set a pin as an output
setOutput()
{
  echo "out" > $BASE_GPIO_PATH/gpio$1/direction
}


# Utility function to change state of a light
setLightState()
{
  echo $2 > $BASE_GPIO_PATH/gpio$1/value
}


exportPin $LED_PIN
setOutput $LED_PIN

setLightState $LED_PIN $ON

sleep 1

setLightState $LED_PIN $OFF

sleep 1

setLightState $LED_PIN $ON

sleep 1

echo "switchting off"

setLightState $LED_PIN $OFF

# Zunächst teilen wir dem System unseren zu exportierenden GPIO (z.B. 17) mit. 
#echo "LED_PIN" > /sys/class/gpio/export
#echo "LED_PIN" > /sys/class/gpio/unexport

# direction in / out 
#echo "out" > /sys/class/gpio/gpio$LED_PIN/direction

# value 

#echo "1" > /sys/class/gpio/gpio$LED_PIN/value
#echo "0" > /sys/class/gpio/gpio$LED_PIN/value


