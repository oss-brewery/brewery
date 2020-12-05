#!/bin/bash

echo "updating git repo"
cd /home/ubuntu/brewery/brewery-application/libs/src/diozero
#git pull
cd /home/ubuntu/brewery/brewery-application/libs/src/diozero/system-utils-native/src/main/c
rm /home/ubuntu/brewery/brewery-application/libs/src/diozero/system-utils-native/src/main/c/libdiozero-system-utils.so
make
