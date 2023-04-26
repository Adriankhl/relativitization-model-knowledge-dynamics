package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import java.io.FileWriter

fun main() {
    val randomSeedList: List<Long> = (200L..210L).toList()

    val distancePowerList: List<Double> = listOf(
        -10.0,
        0.0,
        10.0,
    )

    val fileName = "./data/KDScan4.csv"
    File("data").mkdirs()
    File(fileName).delete()

    for (randomSeed in randomSeedList) {
        for (distancePower in distancePowerList) {
            println("Random seed: $randomSeed. Distance power: $distancePower")
            val df = knowledgeDynamicsSingleRun(
                randomSeed = randomSeed,
                numStep = 2000,
                numPlayer = 100,
                speedOfLight = 0.5,
                maxOutCooperator = 1,
                cooperationLength = 1,
                distancePowerMin = distancePower,
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
}
