package relativitization.knowledge

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import java.io.FileWriter

fun main() {

    val randomSeedList: List<Long> = (200L..220L).toList()

    val fileName = "./data/KDScan6.csv"
    File("data").mkdirs()
    File(fileName).delete()

    for (randomSeed in randomSeedList) {
        println("Random seed: $randomSeed")
        val df = knowledgeDynamicsSingleRun(
            randomSeed = randomSeed,
            numStep = 1000,
            numPlayer = 100,
            speedOfLight = 1.0,
            maxOutCooperator = 10,
            cooperationLength = 10,
            distancePowerMin = 10.0,
            distancePowerGroup = 1,
            speedLimitMin = 0.0,
            speedLimitMax = 0.9,
            speedLimitGroup = 2,
            randomDistanceNum = 100,
            numPreSelectedFirm = 99,
            radicalInnovationProbability = 0.1,
            incrementalInnovationProbability = 0.02,
            forgetProbability = 0.5,
        )

        val format = CSVFormat.DEFAULT.withDelimiter('|')
            .withSkipHeaderRecord(File(fileName).exists())
        df.writeCSV(FileWriter(File(fileName), true), format)
    }
}