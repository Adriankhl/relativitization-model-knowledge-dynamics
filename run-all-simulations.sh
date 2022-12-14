#!/bin/bash

fileList=$(find ./simulations/src/main/kotlin/relativitization/abm ! -name "*Test*.kt" -type f | sed 's|^.*/||')

for fileName in $fileList; do
  className=${fileName::-3}
  ./gradlew :simulations:run -PmainClass=relativitization.abm."$className"Kt -PprocessorCount=10 -PramPercentage=50
done
