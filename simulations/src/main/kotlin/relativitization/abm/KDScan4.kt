package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val randomSeedList: List<Long> = (100L..110L).toList()

    val distancePowerList: List<Double> = listOf(
        0.0,
        5.0,
        200.0,
    )

    for (randomSeed in randomSeedList) {
        for (distancePower in distancePowerList) {
            println("Random seed: $randomSeed. Distance power: $distancePower")
            dfList.add(
                knowledgeDynamicsSingleRun(
                    printStep = false,
                    randomSeed = randomSeed,
                    numPlayer = 120,
                    speedOfLight = 1.0,
                    maxInitialCapability = 30,
                    maxOutCooperator = 1,
                    cooperationLength = 1,
                    distancePowerMin = distancePower,
                    randomDistanceNum = 120,
                    numPreSelectedFirm = 30,
                    radicalInnovationProbability = 0.2,
                    incrementalInnovationProbability = 0.05,
                )
            )
        }
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan4.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}
