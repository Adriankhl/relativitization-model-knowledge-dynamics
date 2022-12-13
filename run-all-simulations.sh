#!/bin/bash

fileList=$(ls simulations/src/main/kotlin/relativitization/abm/)

for fileName in $fileList; do
  className=${fileName::-3}
  ./gradlew :simulations:run -PmainClass=relativitization.abm."$className"Kt -PprocessorCount=10 -PramPercentage=40
done
