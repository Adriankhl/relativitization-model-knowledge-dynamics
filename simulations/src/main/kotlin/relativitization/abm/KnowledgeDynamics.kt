package relativitization.abm

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AllCommandAvailability
import relativitization.universe.data.components.abmKnowledgeDynamicsData
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
        maxInitialCapability = 30,
        innovationHypothesisSize = 3,
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
    maxInitialCapability: Int,
    innovationHypothesisSize: Int,
    cooperationLength: Int,
    numPreSelectedFirm: Int,
    radicalThreshold: Int,
    incrementalThreshold: Int,
    maxCapability: Int,
    maxAbility: Int,
    maxExpertise: Int,
    numProduct: Int,
    maxProductQuality: Int,
    maxReward: Int,
    forgetProbability: Double,
    radicalInnovationProbability: Double,
    incrementalInnovationProbability: Double,
): DataFrame<*> {
    val dfList: MutableList<DataFrame<*>> = mutableListOf()

    val generateSetting = GenerateSettings(
        generateMethod = ABMKnowledgeDynamicsGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 0,
        otherIntMap = mutableMapOf(
            "maxInitialCapability" to maxInitialCapability,
            "innovationHypothesisSize" to innovationHypothesisSize,
        ),
        otherDoubleMap = mutableMapOf(),
        otherStringMap = mutableMapOf(),
        universeSettings = MutableUniverseSettings(
            universeName = "Knowledge Dynamics",
            commandCollectionName = AllCommandAvailability.name(),
            mechanismCollectionName = ABMKnowledgeDynamicsMechanismLists.name(),
            globalMechanismCollectionName = EmptyGlobalMechanismList.name(),
            speedOfLight = speedOfLight,
            xDim = 10,
            yDim = 10,
            zDim = 10,
            randomSeed = randomSeed,
            otherIntMap = mutableMapOf(
                "cooperationLength" to cooperationLength,
                "numPreSelectedFirm" to numPreSelectedFirm,
                "radicalThreshold" to radicalThreshold,
                "incrementalThreshold" to incrementalThreshold,
                "maxCapability" to maxCapability,
                "maxAbility" to maxAbility,
                "maxExpertise" to maxExpertise,
                "numProduct" to numProduct,
                "maxProductQuality" to maxProductQuality,
                "maxReward" to maxReward,
            ),
            otherDoubleMap = mutableMapOf(
                "forgetProbability" to forgetProbability,
                "radicalInnovationProbability" to radicalInnovationProbability,
                "incrementalInnovationProbability" to incrementalInnovationProbability,
            ),
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..numStep) {
        val currentPlayerDataList: List<PlayerData> = universe.getCurrentPlayerDataList()

        val productQualityMean: Double = currentPlayerDataList.sumOf {
            it.playerInternalData.abmKnowledgeDynamicsData().productQuality
        } / currentPlayerDataList.size

        dfList.add(
            dataFrameOf(
                "randomSeed" to listOf(randomSeed),
                "turn" to listOf(turn),
                "speedOfLight" to listOf(speedOfLight),
                "productQualityMean" to listOf(productQualityMean),
            )
        )

        if (printStep) {
            println(
                "Turn: $turn. " +
                        "Product quality mean: $productQualityMean"
            )
        }

        universe.pureAIStep()
    }

    return dfList.concat()
}