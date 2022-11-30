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
        0.01,
        0.02,
        0.08,
        0.32,
        0.64,
        1.0,
    )

    for (randomSeed in randomSeedList) {
        for (distancePower in distancePowerList) {
            println("Random seed: $randomSeed. Distance power: $distancePower")
            dfList.add(
                knowledgeDynamicsSingleRun(
                    printStep = false,
                    numStep = 1000,
                    randomSeed = randomSeed,
                    xDim = 10,
                    yDim = 10,
                    zDim = 10,
                    numPlayer = 120,
                    speedOfLight = 0.1,
                    sameLocation = 0,
                    maxInitialCapability = 30,
                    innovationHypothesisSize = 3,
                    preferentialPower = 2.0,
                    homophilyPower = 1.0,
                    distancePower = distancePower,
                    randomRandomNum = 0,
                    randomPreferentialNum = 0,
                    randomHomophilyNum = 0,
                    randomDistanceNum = 120,
                    transitiveRandomNum = 0,
                    transitivePreferentialNum = 0,
                    transitiveHomophilyNum = 0,
                    transitiveDistanceNum = 0,
                    sequentialRun = 0,
                    cooperationLength = 5,
                    numPreSelectedFirm = 30,
                    radicalThreshold = 6,
                    incrementalThreshold = 8,
                    maxCapability = 100,
                    maxAbility = 10,
                    maxExpertise = 20,
                    numProduct = 20,
                    maxProductQuality = 50,
                    maxReward = 10,
                    forgetProbability = 0.05,
                    radicalInnovationProbability = 0.4,
                    incrementalInnovationProbability = 0.1,
                )
            )
        }
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan4.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}
