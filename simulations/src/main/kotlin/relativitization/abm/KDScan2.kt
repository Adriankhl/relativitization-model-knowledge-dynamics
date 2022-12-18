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
        )
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan2.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}
