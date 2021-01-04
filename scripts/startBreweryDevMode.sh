#!/bin/bash
set -x

# define constants
SCRIPTPATH="$(
  cd "$(dirname "$0")" >/dev/null 2>&1
  pwd -P
)"
ORIGIN_PWD="$PWD"
JAVA_HOME32=$SCRIPTPATH/../../java/jdk-11.0.9.1+1
JAVA_BIN32=$JAVA_HOME32/bin/java

JAVA_HOME64=$SCRIPTPATH/../../java/graalvm-ce-java11-20.2.0
JAVA_BIN64=$JAVA_HOME64/bin/java
#BREWERY_USER=brewery
BREWERY_USER=pi

# define functions

prepareArch32SpecificVariables() {
  JAVA_BIN=$JAVA_BIN32
  export JAVA_HOME=$JAVA_HOME32

}

prepareArch64SpecificVariables() {
  JAVA_BIN=$JAVA_BIN64
  export JAVA_HOME=$JAVA_HOME64
}

#
# MAIN
#

# switch to root
[ "$UID" -eq 0 ] || exec sudo "$0" "$@"

SYSTEM_ARCH=$(uname -m)
USE64BIT=undefined

if [ $SYSTEM_ARCH = "armv7l" ]; then
  USE64BIT=false
  prepareArch32SpecificVariables
elif [ $SYSTEM_ARCH = "aarch64" ]; then
  USE64BIT=true
  prepareArch64SpecificVariables
  exit
else
  echo "Unsupported OS $SYSTEM_ARCH"
  exit -1
fi

echo "USE64BIT: $USE64BIT"
echo "JAVA_BIN=$JAVA_BIN"
echo "SCRIPTPATH=$SCRIPTPATH"

BREWERY_BACKEND_HOME="$SCRIPTPATH/.."
BREWERY_PID_FILE=$BREWERY_BACKEND_HOME/pid

echo "BREWERY_BACKEND_HOME=$BREWERY_BACKEND_HOME"

cd $BREWERY_BACKEND_HOME
./gradlew clean build

#export QUARKUS_LAUNCH_DEVMODE=true
#java -jar backend-0.0.1-SNAPSHOT-runner.jar

# allow everybody to write to the dev file directory, we are in development mode so its okay ;-)
chmod -R o+rw $BREWERY_BACKEND_HOME

#JAVA_BIN=/home/ubuntu/graalvm/graalvm-ce-java11-20.2.0/bin/java
#JAVA_BIN=/usr/lib/jvm/java-11-openjdk-arm64/bin/java
EXECUTABLE_JAR_PATH=$BREWERY_BACKEND_HOME/build/quarkus-app/quarkus-run.jar
#BREWERY_START_CMD="QUARKUS_LAUNCH_DEVMODE=true $JAVA_BIN -jar $EXECUTABLE_JAR_PATH 2>&1 & </dev/null"
JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
BREWERY_START_CMD="QUARKUS_LAUNCH_DEVMODE=true $JAVA_BIN $JAVA_OPTS -jar $EXECUTABLE_JAR_PATH"

#Execute as defined user
#sudo -u $BREWERY_USER bash -c "$BREWERY_START_CMD"

#Execute as defined user + returning pid
#sudo -u $$BREWERY_USER bash -c "nohup $PROGRAM_CMD >> $PROGRAM_LOG 2>&1 & </dev/null; echo "'$!'

#echo "BREWERY_PID=$BREWERY_PID"
su -c "$BREWERY_START_CMD "
#BREWERY_PID=$!
#echo "$BREWERY_PID" > $BREWERY_BACKEND_HOME/pid

# switch back to original pwd
cd $ORIGIN_PWD
