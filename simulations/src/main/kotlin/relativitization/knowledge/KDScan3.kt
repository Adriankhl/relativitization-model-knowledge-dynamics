package relativitization.knowledge

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import java.io.FileWriter

fun main() {
    val randomSeedList: List<Long> = (100L..110L).toList()

    val speedOfLightList: List<Double> = listOf(
        0.1,
        1.0,
        10.0,
        200.0
    )

    val fileName = "./data/KDScan3.csv"
    File("data").mkdirs()
    File(fileName).delete()

    for (randomSeed in randomSeedList) {
        for (speedOfLight in speedOfLightList) {
            println("Random seed: $randomSeed. Speed of light: $speedOfLight")
            val df = knowledgeDynamicsSingleRun(
                randomSeed = randomSeed,
                numStep = 2000,
                numPlayer = 100,
                speedOfLight = speedOfLight,
                preferentialPower = 2.0,
                randomRandomNum = 100,
            )

            val format = CSVFormat.DEFAULT.withDelimiter('|')
                .withSkipHeaderRecord(File(fileName).exists())
            df.writeCSV(FileWriter(File(fileName), true), format)
        }
    }
}
