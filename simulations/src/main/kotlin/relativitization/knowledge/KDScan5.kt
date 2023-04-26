package relativitization.knowledge

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import java.io.FileWriter

fun main() {
    val randomSeedList: List<Long> = (200L..250L).toList()

    val fileName = "./data/KDScan5.csv"
    File("data").mkdirs()
    File(fileName).delete()

    for (randomSeed in randomSeedList) {
        println("Random seed: $randomSeed")
        val df = knowledgeDynamicsSingleRun(
            randomSeed = randomSeed,
            numStep = 2000,
            numPlayer = 100,
            speedOfLight = 0.5,
            maxOutCooperator = 1,
            cooperationLength = 1,
            distancePowerMin = 0.0,
            distancePowerMax = 10.0,
            distancePowerGroup = 3,
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
