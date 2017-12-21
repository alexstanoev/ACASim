#!/bin/bash

set -e

# The CentOS 7 lab machines apear to have mvn installed, but snowy does not seem to
# a prebuilt maven is available at /home/fe14/as14622/linux/dist/maven/bin/mvn
MAVEN=mvn

if ! type "$MAVEN" &> /dev/null; then
  MAVEN=/home/fe14/as14622/linux/dist/maven/bin/mvn
  echo mvn not found, trying to use prebuilt maven
fi

PROG=${1:-bubble}

echo Compiling
$MAVEN package

echo Assembling
#$MAVEN exec:java@asm -DasmIn=prog/test1.aca -DasmOut=prog/test1.hex
java -cp target/ACASim-0.0.1-SNAPSHOT-jar-with-dependencies.jar simulator.core.ACAAssembler prog/$PROG.aca prog/$PROG.hex

echo Running simulator
#$MAVEN exec:java@sim-nogui -DprogramIn=prog/test1.hex 
java -jar target/ACASim-0.0.1-SNAPSHOT-jar-with-dependencies.jar prog/$PROG ${2:-nogui}

