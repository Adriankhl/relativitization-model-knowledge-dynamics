package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val randomSeedList: List<Long> = (100L..150L).toList()

    for (randomSeed in randomSeedList) {
        println("Random seed: $randomSeed")
        dfList.add(
            knowledgeDynamicsSingleRun(
                randomSeed = randomSeed,
                numPlayer = 120,
                speedOfLight = 1.0,
                maxOutCooperator = 1,
                cooperationLength = 1,
                distancePowerMin = -10.0,
                distancePowerMax = 10.0,
                distancePowerGroup = 5,
                randomDistanceNum = 120,
                numPreSelectedFirm = 30,
                radicalInnovationProbability = 0.2,
                incrementalInnovationProbability = 0.05,
            )
        )
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan5.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}
