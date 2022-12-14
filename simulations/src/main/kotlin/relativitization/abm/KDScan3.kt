package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val randomSeedList: List<Long> = (100L..110L).toList()

    val speedOfLightList: List<Double> = listOf(
        0.1,
        1.0,
        10.0,
        200.0
    )

    for (randomSeed in randomSeedList) {
        for (speedOfLight in speedOfLightList) {
            println("Random seed: $randomSeed. Speed of light: $speedOfLight")
            dfList.add(
                knowledgeDynamicsSingleRun(
                    printStep = false,
                    randomSeed = randomSeed,
                    numPlayer = 120,
                    speedOfLight = speedOfLight,
                    preferentialPower = 2.0,
                    randomRandomNum = 120,
                )
            )
        }
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScan3.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}
