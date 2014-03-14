#!/bin/sh

mvn clean install -Dcheckstyle.skip=false -s config/settings.xml $1 $2 $3 $4
