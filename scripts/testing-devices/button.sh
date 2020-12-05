

#!/bin/bash


# Common path for all GPIO access
BASE_GPIO_PATH=/sys/class/gpio
BUTTON_PIN=23

# Pin als Eingang definieren
echo "$BUTTON_PIN" > $BASE_GPIO_PATH/unexport
echo "$BUTTON_PIN" > $BASE_GPIO_PATH/export
echo "in" > $BASE_GPIO_PATH/gpio$BUTTON_PIN/direction


# Den Zustand des Eingangs lesen

previous=$(cat $BASE_GPIO_PATH/gpio$BUTTON_PIN/value)

# Endlose Schleife
while true
do
# Den Zustand des Eingangs lesen
pin=$(cat /sys/class/gpio/gpio$BUTTON_PIN/value)

# Wenn der Eingang von 0 auf 1 gewechselt hat
if [ $pin -gt $previous ]
then
# Das Programm starten
echo "Taster wurde betätigt am $(date)"

# status led ansteuern
./led.sh

else
# Definition des timeout, um CPU-Load zu Optimieren
sleep 0.05
fi

# variabel wird zum neuen Durchlauf auf den Wert „previous“ gesetzt
previous=$pin
done
