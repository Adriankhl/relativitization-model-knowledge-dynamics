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

    val preSelectionStrategyList: List<PreSelectionStrategy> = listOf(
        PreSelectionStrategy.RANDOM,
        PreSelectionStrategy.TRANSITIVE,
    )

    val selectionStrategyList: List<SelectionStrategy> = listOf(
        SelectionStrategy.RANDOM,
        SelectionStrategy.PREFERENTIAL,
        SelectionStrategy.HOMOPHILY,
    )

    val numPreSelectedFirm = 99

    for (cooperationLength in cooperationLengthList) {
        for (preSelectionStrategy in preSelectionStrategyList) {
            for (selectionStrategy in selectionStrategyList) {
                when (selectionStrategy) {
                    SelectionStrategy.RANDOM -> {
                        println("CooperationLength: $cooperationLength. PreSelection: $preSelectionStrategy. Selection: $selectionStrategy")
                        dfList.add(
                            knowledgeDynamicsSingleRun(
                                mechanismCollectionName = ABMKnowledgeDynamicsTestMechanismLists.name(),
                                numStep = 10000,
                                xDim = 1,
                                yDim = 1,
                                zDim = 1,
                                numPlayer = 100,
                                speedOfLight = 200.0,
                                sameLocation = 1,
                                randomRandomNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM) {
                                    100
                                } else {
                                    0
                                },
                                transitiveRandomNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE) {
                                    100
                                } else {
                                    0
                                },
                                sequentialRun = 1,
                                cooperationLength = cooperationLength,
                                numPreSelectedFirm = numPreSelectedFirm,
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
                                    xDim = 1,
                                    yDim = 1,
                                    zDim = 1,
                                    numPlayer = 100,
                                    speedOfLight = 200.0,
                                    sameLocation = 1,
                                    preferentialPower = preferentialPower,
                                    randomPreferentialNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM) {
                                        100
                                    } else {
                                        0
                                    },
                                    transitivePreferentialNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE) {
                                        100
                                    } else {
                                        0
                                    },
                                    sequentialRun = 1,
                                    cooperationLength = cooperationLength,
                                    numPreSelectedFirm = numPreSelectedFirm,
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
                                    numStep = 10000,
                                    xDim = 1,
                                    yDim = 1,
                                    zDim = 1,
                                    numPlayer = 100,
                                    speedOfLight = 200.0,
                                    sameLocation = 1,
                                    homophilyPower = homophilyPower,
                                    randomHomophilyNum = if (preSelectionStrategy == PreSelectionStrategy.RANDOM) {
                                        100
                                    } else {
                                        0
                                    },
                                    transitiveHomophilyNum = if (preSelectionStrategy == PreSelectionStrategy.TRANSITIVE) {
                                        100
                                    } else {
                                        0
                                    },
                                    sequentialRun = 1,
                                    cooperationLength = cooperationLength,
                                    numPreSelectedFirm = numPreSelectedFirm,
                                )
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }


    val df = dfList.concat()

    File("data").mkdirs()
    df.writeCSV("./data/KDScanTest4.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}