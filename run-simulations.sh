#!/bin/bash

fileList=(
  "KDScan3.kt"
  "KDScan4.kt"
  "KDScan5.kt"
  "KDScan6.kt"
)

for fileName in ${fileList[@]}; do
  className=${fileName::-3}
  ./gradlew :simulations:run -PmainClass=relativitization.abm."$className"Kt -PprocessorCount=${1:-4} -PramPercentage=${2:-10}
done
