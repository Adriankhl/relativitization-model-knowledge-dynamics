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

    val cooperationLengthList: List<Int> = listOf(
        2,
        5,
        10,
    )

    val preferentialPowerList: List<Double> = listOf(
        1.0,
        2.0,
        3.0,
        4.0,
        5.0,
    )

    val homophilyPowerList: List<Double> = listOf(
        1.0,
        2.0,
        3.0,
        4.0,
        5.0,
    )

    val preSelectionStrategyList: List<PreSelectionStrategy> =
        PreSelectionStrategy.values().toList()

    val selectionStrategyList: List<SelectionStrategy> = SelectionStrategy.values().toList()

    val numPreSelectedFirm = 30

    for (cooperationLength in cooperationLengthList) {
        for (preSelectionStrategy in preSelectionStrategyList) {
            for (selectionStrategy in selectionStrategyList) {
                when (selectionStrategy) {
                    SelectionStrategy.RANDOM -> {
                        println("CooperationLength: $cooperationLength. PreSelection: $preSelectionStrategy. Selection: $selectionStrategy")
                        dfList.add(
                            knowledgeDynamicsSingleRun(
                                mechanismCollectionName = ABMKnowledgeDynamicsTestMechanismLists.name(),
                                printStep = false,
                                numStep = 10000,
                                randomSeed = 100L,
                                xDim = 1,
                                yDim = 1,
                                zDim = 1,
                                numPlayer = 100,
                                speedOfLight = 200.0,
                                sameLocation = 1,
                                maxInitialCapability = 30,
                                innovationHypothesisSize = 3,
                                preferentialPower = 1.0,
                                homophilyPower = 1.0,
                                randomRandomNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM) {
                                    100
                                } else {
                                    0
                                },
                                randomPreferentialNum = 0,
                                randomHomophilyNum = 0,
                                transitiveRandomNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE) {
                                    100
                                } else {
                                    0
                                },
                                transitivePreferentialNum = 0,
                                transitiveHomophilyNum = 0,
                                sequentialRun = 1,
                                cooperationLength = cooperationLength,
                                numPreSelectedFirm = numPreSelectedFirm,
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

                    SelectionStrategy.PREFERENTIAL -> {
                        for (preferentialPower in preferentialPowerList) {
                            println("CooperationLength: $cooperationLength. PreSelection: $preSelectionStrategy. Selection: $selectionStrategy. Preferential power: $preferentialPower. ")
                            dfList.add(
                                knowledgeDynamicsSingleRun(
                                    mechanismCollectionName = ABMKnowledgeDynamicsTestMechanismLists.name(),
                                    printStep = false,
                                    numStep = 10000,
                                    randomSeed = 100L,
                                    xDim = 1,
                                    yDim = 1,
                                    zDim = 1,
                                    numPlayer = 100,
                                    speedOfLight = 200.0,
                                    sameLocation = 1,
                                    maxInitialCapability = 30,
                                    innovationHypothesisSize = 3,
                                    preferentialPower = preferentialPower,
                                    homophilyPower = 1.0,
                                    randomRandomNum = 0,
                                    randomPreferentialNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM) {
                                        100
                                    } else {
                                        0
                                    },
                                    randomHomophilyNum = 0,
                                    transitiveRandomNum = 0,
                                    transitivePreferentialNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE) {
                                        100
                                    } else {
                                        0
                                    },
                                    transitiveHomophilyNum = 0,
                                    sequentialRun = 1,
                                    cooperationLength = cooperationLength,
                                    numPreSelectedFirm = numPreSelectedFirm,
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

                    SelectionStrategy.HOMOPHILY -> {
                        for (homophilyPower in homophilyPowerList) {
                            println("CooperationLength: $cooperationLength. PreSelection: $preSelectionStrategy. Selection: $selectionStrategy. Homophily power: $homophilyPower. ")
                            dfList.add(
                                knowledgeDynamicsSingleRun(
                                    mechanismCollectionName = ABMKnowledgeDynamicsTestMechanismLists.name(),
                                    printStep = false,
                                    numStep = 10000,
                                    randomSeed = 100L,
                                    xDim = 1,
                                    yDim = 1,
                                    zDim = 1,
                                    numPlayer = 100,
                                    speedOfLight = 200.0,
                                    sameLocation = 1,
                                    maxInitialCapability = 30,
                                    innovationHypothesisSize = 3,
                                    preferentialPower = 1.0,
                                    homophilyPower = homophilyPower,
                                    randomRandomNum = 0,
                                    randomPreferentialNum = 0,
                                    randomHomophilyNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM) {
                                        100
                                    } else {
                                        0
                                    },
                                    transitiveRandomNum = 0,
                                    transitivePreferentialNum = 0,
                                    transitiveHomophilyNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE) {
                                        100
                                    } else {
                                        0
                                    },
                                    sequentialRun = 1,
                                    cooperationLength = cooperationLength,
                                    numPreSelectedFirm = numPreSelectedFirm,
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
            }
        }
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScanTest5.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}