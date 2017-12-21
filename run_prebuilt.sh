#!/bin/bash

echo Running simulator
java -jar sim.jar prog/${1:-bubble} ${2:-nogui}

