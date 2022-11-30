package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val randomSeedList: List<Long> = (100L..200L).toList()

    for (randomSeed in randomSeedList) {
        println("Random seed: $randomSeed")
        dfList.add(
            knowledgeDynamicsSingleRun(
                printStep = false,
                numStep = 1000,
                randomSeed = randomSeed,
                xDim = 10,
                yDim = 10,
                zDim = 10,
                numPlayer = 120,
                speedOfLight = 200.0,
                sameLocation = 0,
                maxInitialCapability = 30,
                innovationHypothesisSize = 3,
                preferentialPower = 2.0,
                distancePower = 1.0,
                homophilyPower = 1.0,
                randomRandomNum = 20,
                randomPreferentialNum = 20,
                randomHomophilyNum = 20,
                randomDistanceNum = 0,
                transitiveRandomNum = 20,
                transitivePreferentialNum = 20,
                transitiveHomophilyNum = 20,
                transitiveDistanceNum = 0,
                sequentialRun = 0,
                cooperationLength = 5,
                numPreSelectedFirm = 5,
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


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan2.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}