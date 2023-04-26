#!/bin/bash

fileList=$(find ./simulations/src/main/kotlin/relativitization/knowledge ! -name "*Test*.kt" -type f | sed 's|^.*/||' | sort)

for fileName in $fileList; do
  className=${fileName::-3}
  ./gradlew :simulations:run -PmainClass=relativitization.knowledge."$className"Kt -PprocessorCount="${1:-10}" -PramPercentage="${2:-50}"
done
