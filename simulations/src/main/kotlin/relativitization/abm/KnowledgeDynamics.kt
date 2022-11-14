package relativitization.abm

import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.describe
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AllCommandAvailability
import relativitization.universe.data.components.ABMKnowledgeDynamicsData
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
        sameLocation = 1,
        maxInitialCapability = 30,
        innovationHypothesisSize = 3,
        preferentialPower = 1.0,
        homophilyPower = 1.0,
        preSelectionTransitiveNum = 0,
        selectionPreferentialNum = 100,
        selectionHomophilyNum = 0,
        sequentialRun = 0,
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

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/knowledge-dynamics.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}

internal fun knowledgeDynamicsSingleRun(
    mechanismCollectionName: String = ABMKnowledgeDynamicsMechanismLists.name(),
    printStep: Boolean,
    randomSeed: Long,
    numStep: Int,
    numPlayer: Int,
    speedOfLight: Double,
    sameLocation: Int,
    maxInitialCapability: Int,
    innovationHypothesisSize: Int,
    preferentialPower: Double,
    homophilyPower: Double,
    preSelectionTransitiveNum: Int,
    selectionPreferentialNum: Int,
    selectionHomophilyNum: Int,
    sequentialRun: Int,
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
    // This map will be converted to dataframe
    val dfMap: MutableMap<String, MutableList<Any>> = mutableMapOf()

    val generateSetting = GenerateSettings(
        generateMethod = ABMKnowledgeDynamicsGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 0,
        otherIntMap = mutableMapOf(
            "sameLocation" to sameLocation,
            "maxInitialCapability" to maxInitialCapability,
            "innovationHypothesisSize" to innovationHypothesisSize,
            "preSelectionTransitiveNum" to preSelectionTransitiveNum,
            "selectionPreferentialNum" to selectionPreferentialNum,
            "selectionHomophilyNum" to selectionHomophilyNum,
        ),
        otherDoubleMap = mutableMapOf(),
        otherStringMap = mutableMapOf(),
        universeSettings = MutableUniverseSettings(
            universeName = "Knowledge Dynamics",
            commandCollectionName = AllCommandAvailability.name(),
            mechanismCollectionName = mechanismCollectionName,
            globalMechanismCollectionName = EmptyGlobalMechanismList.name(),
            speedOfLight = speedOfLight,
            xDim = 10,
            yDim = 10,
            zDim = 10,
            randomSeed = randomSeed,
            otherIntMap = mutableMapOf(
                "sequentialRun" to sequentialRun,
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
                "preferentialPower" to preferentialPower,
                "homophilyPower" to homophilyPower,
                "forgetProbability" to forgetProbability,
                "radicalInnovationProbability" to radicalInnovationProbability,
                "incrementalInnovationProbability" to incrementalInnovationProbability,
            ),
        )
    )

    val universe = Universe(GenerateUniverseMethodCollection.generate(generateSetting))

    for (turn in 1..numStep) {
        val currentPlayerDataList: List<PlayerData> = universe.getCurrentPlayerDataList()

        val shouldRecord: Boolean = if (sequentialRun == 1) {
            turn % numPlayer == 1
        } else {
            true
        }

        if (shouldRecord) {
            currentPlayerDataList.forEach { currentPlayerData ->
                val currentKnowledgeDynamicsData: ABMKnowledgeDynamicsData = currentPlayerData
                    .playerInternalData.abmKnowledgeDynamicsData()

                val outputDataMap = mapOf(
                    "randomSeed" to randomSeed,
                    "turn" to turn,
                    "speedOfLight" to speedOfLight,
                    "preferentialPower" to preferentialPower,
                    "homophilyPower" to homophilyPower,
                    "cooperationLength" to cooperationLength,
                    "playerId" to currentPlayerData.playerId,
                    "preSelectionStrategy" to currentKnowledgeDynamicsData.preSelectionStrategy,
                    "selectionStrategy" to currentKnowledgeDynamicsData.selectionStrategy,
                    "productId" to currentKnowledgeDynamicsData.productId,
                    "productQuality" to currentKnowledgeDynamicsData.productQuality,
                    "latestReward" to currentKnowledgeDynamicsData.latestReward,
                    "cooperationIn" to currentKnowledgeDynamicsData.cooperationInMap.keys,
                    "cooperationOut" to currentKnowledgeDynamicsData.cooperationOutMap.keys,
                )

                outputDataMap.forEach {
                    dfMap.getOrPut(it.key) {
                        mutableListOf()
                    }.add(it.value)
                }
            }
        }

        if (printStep) {
            val productQualityMean: Double = currentPlayerDataList.sumOf {
                it.playerInternalData.abmKnowledgeDynamicsData().productQuality
            } / currentPlayerDataList.size

            val product1Player: List<PlayerData> = currentPlayerDataList.filter {
                it.playerInternalData.abmKnowledgeDynamicsData().productId == 1
            }

            val product1QualityMean: Double = if (product1Player.isNotEmpty()) {
                product1Player.sumOf {
                    it.playerInternalData.abmKnowledgeDynamicsData().productQuality
                } / product1Player.size
            } else {
                0.0
            }

            val expertiseMean: Double = currentPlayerDataList.sumOf { playerData ->
                playerData.playerInternalData.abmKnowledgeDynamicsData().innovationHypothesis
                    .sumOf { it.expertise }.toDouble() / playerData.playerInternalData
                    .abmKnowledgeDynamicsData().innovationHypothesis.size
            } / currentPlayerDataList.size

            val latestRewardList: List<Int> = currentPlayerDataList.map {
                it.playerInternalData.abmKnowledgeDynamicsData().latestReward
            }

            val rewardThreshold = 6

            val numPoorPlayer: Int = latestRewardList.count {
                it <= rewardThreshold
            }

            val numNoCooperator: Int = currentPlayerDataList.filter {
                it.playerInternalData.abmKnowledgeDynamicsData().cooperationInMap.isEmpty()
            }.size

            println(
                "Turn: $turn. " +
                        "Product quality mean (1): $productQualityMean ($product1QualityMean). " +
                        "Expertise mean: $expertiseMean. " +
                        "Num poor: $numPoorPlayer. " +
                        "Num no cooperator: $numNoCooperator. "
            )
        }

        universe.pureAIStep()
    }

    return dfMap.toDataFrame()
}