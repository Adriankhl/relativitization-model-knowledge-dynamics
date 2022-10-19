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
        numStep = 1000,
        randomSeed = 100L,
        numPlayer = 50,
        speedOfLight = 1.0,
    )

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/flocking.csv")
}

internal fun knowledgeDynamicsSingleRun(
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
            "maxInitialCapability" to 30
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
            otherDoubleMap = mutableMapOf(),
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

        universe.pureAIStep()
    }

    return dfList.concat()
}