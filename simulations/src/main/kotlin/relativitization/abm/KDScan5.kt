package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val randomSeedList: List<Long> = (200L..250L).toList()

    for (randomSeed in randomSeedList) {
        println("Random seed: $randomSeed")
        dfList.add(
            knowledgeDynamicsSingleRun(
                randomSeed = randomSeed,
                numStep = 2000,
                numPlayer = 100,
                speedOfLight = 0.5,
                maxOutCooperator = 1,
                cooperationLength = 1,
                distancePowerMin = -10.0,
                distancePowerMax = 10.0,
                distancePowerGroup = 5,
                randomDistanceNum = 100,
                numPreSelectedFirm = 99,
                radicalInnovationProbability = 0.1,
                incrementalInnovationProbability = 0.02,
                forgetProbability = 0.5,
            )
        )
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan5.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}
