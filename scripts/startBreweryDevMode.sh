#!/bin/bash
set -x
# switch to root
[ "$UID" -eq 0 ] || exec sudo "$0" "$@"


BREWERY_USER=brewery
BREWERY_BACKEND_HOME="/home/ubuntu/brewery/brewery-application/backend/"
BREWERY_PID_FILE=$BREWERY_BACKEND_HOME/pid

#export QUARKUS_LAUNCH_DEVMODE=true
#java -jar backend-0.0.1-SNAPSHOT-runner.jar

# allow everybody to write to the dev file directory, we are in development mode so its okay ;-)
chmod -R o+rw $BREWERY_BACKEND_HOME

#JAVA_BIN=/home/ubuntu/graalvm/graalvm-ce-java11-20.2.0/bin/java
JAVA_BIN=/usr/lib/jvm/java-11-openjdk-arm64/bin/java
BREWERY_START_CMD="QUARKUS_LAUNCH_DEVMODE=true $JAVA_BIN -jar $BREWERY_BACKEND_HOME/quarkus-run.jar"



#sudo -u $$BREWERY_USER bash -c "nohup $PROGRAM_CMD >> $PROGRAM_LOG 2>&1 & </dev/null; echo "'$!'
BREWERY_PID=$(sudo -u $BREWERY_USER bash -c "$BREWERY_START_CMD & </dev/null; echo "'$!')
echo "BREWERY_PID=$BREWERY_PID"
#su -c "$BREWERY_START_CMD " -s /bin/sh $BREWERY_USER
#BREWERY_PID=$!

echo "$BREWERY_PID" > $BREWERY_BACKEND_HOME/pid


