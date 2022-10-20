package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.commands.AllCommandAvailability
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.abm.ABMKnowledgeDynamicsGenerate
import relativitization.universe.global.EmptyGlobalMechanismList
import relativitization.universe.mechanisms.ABMKnowledgeDynamicsMechanismLists
import java.io.File

fun main() {
    val df = knowledgeDynamicsSingleRun(
        printStep = true,
        numStep = 1000,
        randomSeed = 100L,
        numPlayer = 100,
        speedOfLight = 200.0,
    )

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/knowledge-dynamics.csv")
}

internal fun knowledgeDynamicsSingleRun(
    printStep: Boolean,
    randomSeed: Long,
    numStep: Int,
    numPlayer: Int,
    speedOfLight: Double,
): DataFrame<*> {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val generateSetting = GenerateSettings(
        generateMethod = ABMKnowledgeDynamicsGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 0,
        otherIntMap = mutableMapOf(
            "maxInitialCapability" to 30,
            "innovationHypothesisSize" to 3,
        ),
        otherDoubleMap = mutableMapOf(),
        otherStringMap = mutableMapOf(),
        universeSettings = MutableUniverseSettings(
            universeName = "Flocking",
            commandCollectionName = AllCommandAvailability.name(),
            mechanismCollectionName = ABMKnowledgeDynamicsMechanismLists.name(),
            globalMechanismCollectionName = EmptyGlobalMechanismList.name(),
            speedOfLight = speedOfLight,
            xDim = 10,
            yDim = 10,
            zDim = 10,
            randomSeed = randomSeed,
            otherIntMap = mutableMapOf(
                "cooperationLength" to 5,
                "numPreSelectedFirm" to 5,
                "radicalThreshold" to 6,
                "incrementalThreshold" to 8,
                "maxCapability" to 100,
                "maxAbility" to 10,
                "maxExpertise" to 20,
                "numProduct" to 20,
                "maxProductQuality" to 50,
                "maxReward" to 10,
            ),
            otherDoubleMap = mutableMapOf(
                "forgetProbability" to 0.05,
                "radicalInnovationProbability" to 0.4,
                "incrementalInnovationProbability" to 0.1,
            ),
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..numStep) {
        dfList.add(
            dataFrameOf(
                "randomSeed" to listOf(randomSeed),
                "turn" to listOf(turn),
                "speedOfLight" to listOf(speedOfLight),
            )
        )

        if (printStep) {
            println("Turn: $turn. "
            )
        }

        universe.pureAIStep()
    }

    return dfList.concat()
}