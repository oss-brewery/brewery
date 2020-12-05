#!/bin/bash

#
# Installation Script to setup the brewery in "production" mode
#
# Considering stuff like:
# * non root permissions for devices
# * execute as a specific user
#
# heavily under construction, for the moment just some snippets



BREWERY_USER="brewery"
BREWERY_GROUP="brewery"




createUserAndGroup()
{
  id -u somename &>/dev/null || useradd $BREWERY_USER
  groupadd -f $BREWERY_GROUP
}


createUDEVRule()
{
  RULE_FILE_NAME="99-brewery.rules"
  RULE_FOLDER="/etc/udev/rules.d"
  RULE_FILE="$RULE_FOLDER/$RULE_FILE_NAME"

  # backup rule file if it already exists
  if [ -f $RULE_FILE ]; then
      echo "Udev rule file already exists $RULE_FILE"
      mv $RULE_FILE $RULE_FILE_$(date +%m-%d-%y).bak
  fi

  echo "SUBSYSTEM==\"gpio*\", PROGRAM=\"/bin/sh -c 'chown -R root:$BREWERY_GROUP /sys/class/gpio && chmod -R 770 /sys/class/gpio; chown -R root:$BREWERY_GROUP /sys/devices/virtual/gpio && chmod -R 770 /sys/devices/virtual/gpio'\"" > $RULE_FILE

}






###
### MAIN
###


echo "Executing script as $(whoami)"
[ "$UID" -eq 0 ] || exec sudo "$0" "$@"


createUserAndGroup

createUDEVRule


