package relativitization.knowledge

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.knowledge.data.components.PreSelectionStrategy
import relativitization.universe.knowledge.data.components.SelectionStrategy
import java.io.File
import java.io.FileWriter

fun main() {
    val preSelectionStrategyList: List<PreSelectionStrategy> = listOf(
        PreSelectionStrategy.RANDOM,
        PreSelectionStrategy.TRANSITIVE,
    )

    val selectionStrategyList: List<SelectionStrategy> = listOf(
        SelectionStrategy.RANDOM,
        SelectionStrategy.PREFERENTIAL,
        SelectionStrategy.HOMOPHILY,
    )


    val fileName = "./data/KDScan1.csv"
    File("data").mkdirs()
    File(fileName).delete()


    for (preSelectionStrategy in preSelectionStrategyList) {
        for (selectionStrategy in selectionStrategyList) {
            println("PreSelection: $preSelectionStrategy. Selection: $selectionStrategy")
            val df = knowledgeDynamicsSingleRun(
                numPlayer = 100,
                speedOfLight = 200.0,
                preferentialPower = 2.0,
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
                randomDistanceNum = 0,
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
                incrementalInnovationProbability = 0.1,
            )

            val format = CSVFormat.DEFAULT.withDelimiter('|')
                .withSkipHeaderRecord(File(fileName).exists())
            df.writeCSV(FileWriter(File(fileName), true), format)
        }
    }
}