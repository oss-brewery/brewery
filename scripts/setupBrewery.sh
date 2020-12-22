#!/bin/bash

set -x

# define constants

SCRIPTPATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
BREWERY_USER="ubuntu"
BREWERY_GROUP="ubuntu"
SYSTEM_ARCH=`uname -m`
USE64BIT=undefined


JAVA_HOME_BASE_PATH=$SCRIPTPATH/../../java


#TODO use github api to get latest release
JDK32_URL="https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jre_arm_linux_hotspot_11.0.9.1_1.tar.gz"
JDK32_CHECKSUM_URL="https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jre_arm_linux_hotspot_11.0.9.1_1.tar.gz.sha256.txt"
JDK32_FILENAME="$(basename -- $JDK32_URL)"

#TODO use github api to get latest release
GRAALVM_URL="https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-20.3.0/graalvm-ce-java11-darwin-amd64-20.3.0.tar.gz"
GRAALVM_CHECKSUM_URL="https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-20.3.0/graalvm-ce-java11-darwin-amd64-20.3.0.tar.gz.sha256"
GRAALVM_FILENAME="$(basename -- $GRAALVM_URL)"


# define functions

prepareArc32JAVA()
{
  if [ ! -d "$JAVA_HOME_BASE_PATH" ]; then
    mkdir -p $JAVA_HOME_BASE_PATH

  fi

  cd $JAVA_HOME_BASE_PATH
  if [ ! -d "$JAVA_HOME_BASE_PATH/$JDK32_FILENAME" ]; then

    if [ ! -f "$JAVA_HOME_BASE_PATH/OpenJDK11U-jre_arm_linux_hotspot_11.0.9.1_1.tar.gz" ]; then
        wget $JDK32_URL

    fi

    CHECKSUM_OK=$(wget -O- -q -T 1 -t 1 $JDK32_CHECKSUM_URL | sha256sum -c | grep -c OK)

    if [ "$CHECKSUM_OK" != "1" ]; then
        echo "Java checksum is invalid";
        exit -1;
    fi

    tar xzf OpenJDK11U-jre_arm_linux_hotspot_11.0.9.1_1.tar.gz

    #switch owner
    chown -R $BREWERY_USER:$BREWERY_GROUP $JAVA_HOME_BASE_PATH

  fi

}


prepareArc64JAVA()
{
  if [ ! -d "$JAVA_HOME_BASE_PATH" ]; then
    mkdir -p $JAVA_HOME_BASE_PATH

  fi

  cd $JAVA_HOME_BASE_PATH
  if [ ! -d "$JAVA_HOME_BASE_PATH/graalvm" ]; then

    if [ ! -f "$JAVA_HOME_BASE_PATH/$GRAALVM_FILENAME" ]; then
        wget $GRAALVM_URL

    fi

    CHECKSUM_OK=$(wget -O- -q -T 1 -t 1 $GRAALVM_CHECKSUM_URL | sha256sum -c | grep -c OK)

    if [ "$CHECKSUM_OK" != "1" ]; then
        echo "Java checksum is invalid";
        exit -1;
    fi

    tar xzf graalvm-ce-java11-darwin-amd64-20.3.0.tar.gz

    #switch owner
    chown -R $BREWERY_USER:$BREWERY_GROUP $JAVA_HOME_BASE_PATH

  fi
}



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


#createUserAndGroup
#createUDEVRule





if      [ $SYSTEM_ARCH = "armv7l" ]; then
        USE64BIT=false
        prepareArc32JAVA
        #prepareArc64JAVA
elif    [ $SYSTEM_ARCH = "aarch64" ]; then
        USE64BIT=true
        prepareArc64JAVA

else
        echo "Unsupported OS $SYSTEM_ARCH"
        exit -1
fi





