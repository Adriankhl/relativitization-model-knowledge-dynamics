package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
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
                    xDim = 10,
                    yDim = 10,
                    zDim = 10,
                    numPlayer = 100,
                    speedOfLight = 200.0,
                    sameLocation = 0,
                    maxInitialCapability = 30,
                    innovationHypothesisSize = 3,
                    preferentialPower = 2.0,
                    homophilyPower = 1.0,
                    randomRandomNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM && selectionStrategy == SelectionStrategy.RANDOM) {
                        100
                    } else {
                        0
                    },
                    randomPreferentialNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM && selectionStrategy == SelectionStrategy.PREFERENTIAL) {
                        100
                    } else {
                        0
                    },
                    randomHomophilyNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM && selectionStrategy == SelectionStrategy.HOMOPHILY) {
                        100
                    } else {
                        0
                    },
                    transitiveRandomNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE && selectionStrategy == SelectionStrategy.RANDOM) {
                        100
                    } else {
                        0
                    },
                    transitivePreferentialNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE && selectionStrategy == SelectionStrategy.PREFERENTIAL) {
                        100
                    } else {
                        0
                    },
                    transitiveHomophilyNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE && selectionStrategy == SelectionStrategy.HOMOPHILY) {
                        100
                    } else {
                        0
                    },
                    sequentialRun = 0,
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

    File("data").mkdirs()
    df.writeCSV("./data/KDScan1.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}