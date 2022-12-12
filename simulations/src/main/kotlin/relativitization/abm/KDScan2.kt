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
                numPlayer = 120,
                speedOfLight = 200.0,
                preferentialPower = 2.0,
                randomRandomNum = 20,
                randomPreferentialNum = 20,
                randomHomophilyNum = 20,
                transitiveRandomNum = 20,
                transitivePreferentialNum = 20,
                transitiveHomophilyNum = 20,
            )
        )
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan2.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}