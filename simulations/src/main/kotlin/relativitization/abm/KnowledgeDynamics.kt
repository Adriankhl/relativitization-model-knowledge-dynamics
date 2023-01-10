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
        numPlayer = 100,
        speedOfLight = 200.0,
        sameLocation = 1,
        randomPreferentialNum = 100,
        cooperationLength = 1,
        numPreSelectedFirm = 99,
    )

    println(df.describe())

    File("data").mkdirs()
    df.writeCSV("./data/knowledge-dynamics.csv", CSVFormat.DEFAULT.withDelimiter('|'))
}

internal fun knowledgeDynamicsSingleRun(
    mechanismCollectionName: String = ABMKnowledgeDynamicsMechanismLists.name(),
    printStep: Boolean = false,
    randomSeed: Long = 100L,
    xDim: Int = 10,
    yDim: Int = 10,
    zDim: Int = 10,
    numStep: Int = 1000,
    numPlayer: Int = 0,
    speedOfLight: Double = 1.0,
    sameLocation: Int = 0,
    maxInitialCapability: Int = 30,
    innovationHypothesisSize: Int = 3,
    preferentialPower: Double = 1.0,
    homophilyPower: Double = 1.0,
    distancePowerMin: Double = 1.0,
    distancePowerMax: Double = 1.0,
    distancePowerGroup: Int = 1,
    speedLimitMin: Double = 1.0,
    speedLimitMax: Double = 1.0,
    speedLimitGroup: Int = 1,
    switchLocationCoolDown: Int = Int.MAX_VALUE,
    randomRandomNum: Int = 0,
    randomPreferentialNum: Int = 0,
    randomHomophilyNum: Int = 0,
    randomDistanceNum: Int = 0,
    transitiveRandomNum: Int = 0,
    transitivePreferentialNum: Int = 0,
    transitiveHomophilyNum: Int = 0,
    transitiveDistanceNum: Int = 0,
    sequentialRun: Int = 0,
    maxOutCooperator: Int = Int.MAX_VALUE,
    cooperationLength: Int = 5,
    numPreSelectedFirm: Int = 5,
    radicalThreshold: Int = 6,
    incrementalThreshold: Int = 8,
    maxCapability: Int = 100,
    maxAbility: Int = 10,
    maxExpertise: Int = 20,
    numProduct: Int = 20,
    maxProductQuality: Int = 50,
    maxReward: Int = 10,
    forgetProbability: Double = 0.05,
    radicalInnovationProbability: Double = 0.4,
    incrementalInnovationProbability: Double = 0.1,
): DataFrame<*> {
    // This map will be converted to dataframe
    val dfMap: MutableMap<String, MutableList<Any>> = mutableMapOf()

    val generateSetting = GenerateSettings(
        generateMethod = ABMKnowledgeDynamicsGenerate.name(),
        numPlayer = numPlayer,
        numHumanPlayer = 0,
        otherIntMap = mutableMapOf(
            "sameLocation" to sameLocation,
            "distancePowerGroup" to distancePowerGroup,
            "speedLimitGroup" to speedLimitGroup,
            "maxInitialCapability" to maxInitialCapability,
            "innovationHypothesisSize" to innovationHypothesisSize,
            "randomRandomNum" to randomRandomNum,
            "randomPreferentialNum" to randomPreferentialNum,
            "randomHomophilyNum" to randomHomophilyNum,
            "randomDistanceNum" to randomDistanceNum,
            "transitiveRandomNum" to transitiveRandomNum,
            "transitivePreferentialNum" to transitivePreferentialNum,
            "transitiveHomophilyNum" to transitiveHomophilyNum,
            "transitiveDistanceNum" to transitiveDistanceNum,
        ),
        otherDoubleMap = mutableMapOf(
            "distancePowerMin" to distancePowerMin,
            "distancePowerMax" to distancePowerMax,
            "speedLimitMin" to speedLimitMin,
            "speedLimitMax" to speedLimitMax,
        ),
        otherStringMap = mutableMapOf(),
        universeSettings = MutableUniverseSettings(
            universeName = "Knowledge Dynamics",
            commandCollectionName = AllCommandAvailability.name(),
            mechanismCollectionName = mechanismCollectionName,
            globalMechanismCollectionName = EmptyGlobalMechanismList.name(),
            speedOfLight = speedOfLight,
            xDim = xDim,
            yDim = yDim,
            zDim = zDim,
            randomSeed = randomSeed,
            otherIntMap = mutableMapOf(
                "sequentialRun" to sequentialRun,
                "cooperationLength" to cooperationLength,
                "maxOutCooperator" to maxOutCooperator,
                "numPreSelectedFirm" to numPreSelectedFirm,
                "radicalThreshold" to radicalThreshold,
                "incrementalThreshold" to incrementalThreshold,
                "maxCapability" to maxCapability,
                "maxAbility" to maxAbility,
                "maxExpertise" to maxExpertise,
                "numProduct" to numProduct,
                "maxProductQuality" to maxProductQuality,
                "maxReward" to maxReward,
                "switchLocationCoolDown" to switchLocationCoolDown,
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
                    "playerId" to currentPlayerData.playerId,
                    "x" to currentPlayerData.double4D.x,
                    "y" to currentPlayerData.double4D.y,
                    "z" to currentPlayerData.double4D.z,
                    "preferentialPower" to preferentialPower,
                    "homophilyPower" to homophilyPower,
                    "distancePower" to currentKnowledgeDynamicsData.distancePower,
                    "speedLimit" to currentKnowledgeDynamicsData.speedLimit,
                    "restMass" to currentKnowledgeDynamicsData.restMass,
                    "cooperationLength" to cooperationLength,
                    "preSelectionStrategy" to currentKnowledgeDynamicsData.preSelectionStrategy,
                    "selectionStrategy" to currentKnowledgeDynamicsData.selectionStrategy,
                    "productId" to currentKnowledgeDynamicsData.productId,
                    "capabilityFactor" to currentKnowledgeDynamicsData.capabilityFactor,
                    "abilityFactor" to currentKnowledgeDynamicsData.abilityFactor,
                    "expertiseFactor" to currentKnowledgeDynamicsData.expertiseFactor,
                    "productQuality" to currentKnowledgeDynamicsData.productQuality,
                    "latestReward" to currentKnowledgeDynamicsData.latestReward,
                    "totalReward" to currentKnowledgeDynamicsData.totalReward,
                    "cooperationIn" to currentKnowledgeDynamicsData.cooperationInMap.keys,
                    "cooperationOut" to currentKnowledgeDynamicsData.cooperationOutMap.keys,
                    "numSelfRadicalInnovation" to currentKnowledgeDynamicsData.numSelfRadicalInnovation,
                    "numCooperationRadicalInnovation" to currentKnowledgeDynamicsData.numCooperationRadicalInnovation,
                    "numSelfIncrementalInnovation" to currentKnowledgeDynamicsData.numSelfIncrementalInnovation,
                    "numCooperationIncrementalInnovation" to currentKnowledgeDynamicsData.numCooperationIncrementalInnovation,
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
