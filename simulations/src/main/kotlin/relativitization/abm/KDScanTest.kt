package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.data.components.PreSelectionStrategy
import relativitization.universe.data.components.SelectionStrategy
import relativitization.universe.mechanisms.ABMKnowledgeDynamicsTestMechanismLists
import java.io.File

fun main() {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val preferentialPowerList: List<Double> = listOf(
        1.0,
        2.0,
        5.0,
        10.0
    )

    val preSelectionStrategyList: List<PreSelectionStrategy> =
        PreSelectionStrategy.values().toList()

    val selectionStrategyList: List<SelectionStrategy> = SelectionStrategy.values().toList()

    for (preferentialPower in preferentialPowerList) {
        for (preSelectionStrategy in preSelectionStrategyList) {
            for (selectionStrategy in selectionStrategyList) {
                println("PreferentialPower: $preferentialPower. PreSelection: $preSelectionStrategy. Selection: $selectionStrategy")
                dfList.add(
                    knowledgeDynamicsSingleRun(
                        mechanismCollectionName = ABMKnowledgeDynamicsTestMechanismLists.name(),
                        printStep = false,
                        numStep = 1000,
                        randomSeed = 100L,
                        numPlayer = 100,
                        speedOfLight = 200.0,
                        sameLocation = 1,
                        maxInitialCapability = 30,
                        innovationHypothesisSize = 3,
                        preferentialPower = preferentialPower,
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
                        cooperationLength = 1,
                        numPreSelectedFirm = 99,
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
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScanTest.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}