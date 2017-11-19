#!/bin/bash

echo Compiling
/home/fe14/as14622/linux/dist/maven/bin/mvn package

echo Assembling
#/home/fe14/as14622/linux/dist/maven/bin/mvn exec:java@asm -DasmIn=prog/test1.aca -DasmOut=prog/test1.hex
java -cp target/ACASim-0.0.1-SNAPSHOT.jar simulator.core.ACAAssembler prog/test1.aca prog/test1.hex

echo Running simulator
#/home/fe14/as14622/linux/dist/maven/bin/mvn exec:java@sim-nogui -DprogramIn=prog/test1.hex 
java -jar target/ACASim-0.0.1-SNAPSHOT.jar prog/test1.hex nogui

