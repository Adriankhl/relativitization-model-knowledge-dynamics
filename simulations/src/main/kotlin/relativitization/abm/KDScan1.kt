package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.data.components.PreSelectionStrategy
import relativitization.universe.data.components.SelectionStrategy
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val preSelectionStrategyList: List<PreSelectionStrategy> =
        PreSelectionStrategy.values().toList()

    val selectionStrategyList: List<SelectionStrategy> = SelectionStrategy.values().toList()

    for (preSelectionStrategy in preSelectionStrategyList) {
        for (selectionStrategy in selectionStrategyList) {
            println("PreSelection: $preSelectionStrategy. Selection: $selectionStrategy")
            dfList.add(
                knowledgeDynamicsSingleRun(
                    printStep = false,
                    numStep = 1000,
                    randomSeed = 100L,
                    numPlayer = 100,
                    speedOfLight = 200.0,
                    maxInitialCapability = 30,
                    innovationHypothesisSize = 3,
                    preSelectionTransitiveNum = when (preSelectionStrategy) {
                        PreSelectionStrategy.TRANSITIVE -> 100
                        else -> 0
                    },
                    selectionPreferentialNum = when (selectionStrategy) {
                        SelectionStrategy.PREFERENTIAL -> 100
                        else -> 0
                    },
                    selectionHomophilyNum = when (selectionStrategy) {
                        SelectionStrategy.HOMOPHILY -> 100
                        else -> 0
                    },
                    cooperationLength = 5,
                    numPreSelectedFirm = 5,
                    radicalThreshold = 6,
                    incrementalThreshold = 8,
                    maxCapability = 100,
                    maxAbility = 10,
                    maxExpertise = 20,
                    numProduct = 20,
                    maxProductQuality = 50,
                    maxReward = 10,
                    forgetProbability = 0.05,
                    radicalInnovationProbability = 0.4,
                    incrementalInnovationProbability = 0.1,
                )

            )
        }
    }


    val df = dfList.concat()

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/KDScan1.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}