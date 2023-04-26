package relativitization.knowledge

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import java.io.FileWriter

fun main() {
    val randomSeedList: List<Long> = (100L..150L).toList()

    val fileName = "./data/KDScan2.csv"
    File("data").mkdirs()
    File(fileName).delete()

    for (randomSeed in randomSeedList) {
        println("Random seed: $randomSeed")
        val df = knowledgeDynamicsSingleRun(
            numStep = 1000,
            randomSeed = randomSeed,
            numPlayer = 100,
            speedOfLight = 200.0,
            preferentialPower = 2.0,
            randomRandomNum = 17,
            randomPreferentialNum = 17,
            randomHomophilyNum = 17,
            transitiveRandomNum = 17,
            transitivePreferentialNum = 16,
            transitiveHomophilyNum = 16,
        )

        val format = CSVFormat.DEFAULT.withDelimiter('|')
            .withSkipHeaderRecord(File(fileName).exists())
        df.writeCSV(FileWriter(File(fileName), true), format)
    }
}
