package relativitization.universe.generate.abm

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.MutableUniverseData4D
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.UniverseState
import relativitization.universe.core.data.global.MutableUniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethod
import relativitization.universe.core.maths.grid.Grids.create4DGrid
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.data.components.MutableABMKnowledgeDynamicsData
import relativitization.universe.data.components.MutableKnowledgeGene
import relativitization.universe.data.components.PreSelectionStrategy
import relativitization.universe.data.components.SelectionStrategy
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import kotlin.math.floor
import kotlin.random.Random

object ABMKnowledgeDynamicsGenerate : GenerateUniverseMethod() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun generate(
        generateSettings: GenerateSettings,
        random: Random
    ): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(
            generateSettings.universeSettings
        )

        val data = MutableUniverseData4D(
            create4DGrid(
                universeSettings.tDim,
                universeSettings.xDim,
                universeSettings.yDim,
                universeSettings.zDim
            ) { _, _, _, _ -> mutableMapOf() }
        )

        val universeState = UniverseState(
            currentTime = universeSettings.tDim - 1,
            maxPlayerId = 0,
        )

        val sameLocation: Int = generateSettings.getOtherIntOrDefault(
            "sameLocation",
            0
        )

        val maxInitialCapability: Int = generateSettings.getOtherIntOrDefault(
            "maxInitialCapability",
            30
        )

        val innovationHypothesisSize: Int = generateSettings.getOtherIntOrDefault(
            "innovationHypothesisSize",
            3
        )

        val maxAbility: Int = universeSettings.getOtherIntOrDefault(
            "maxAbility",
            10
        )

        val randomRandomNum: Int = generateSettings.getOtherIntOrDefault(
            "randomRandomNum",
            0
        )

        val randomPreferentialNum: Int = generateSettings.getOtherIntOrDefault(
            "randomPreferentialNum",
            0
        )

        val randomHomophilyNum: Int = generateSettings.getOtherIntOrDefault(
            "randomHomophilyNum",
            0
        )

        val randomDistanceNum: Int = generateSettings.getOtherIntOrDefault(
            "randomDistanceNum",
            0
        )

        val transitiveRandomNum: Int = generateSettings.getOtherIntOrDefault(
            "transitiveRandomNum",
            0
        )

        val transitivePreferentialNum: Int = generateSettings.getOtherIntOrDefault(
            "transitivePreferentialNum",
            0
        )

        val transitiveHomophilyNum: Int = generateSettings.getOtherIntOrDefault(
            "transitiveHomophilyNum",
            0
        )

        val transitiveDistanceNum: Int = generateSettings.getOtherIntOrDefault(
            "transitiveDistanceNum",
            0
        )

        val totalStrategyNum: Int = randomRandomNum + randomPreferentialNum + randomHomophilyNum +
                randomDistanceNum + transitiveRandomNum + transitivePreferentialNum +
                transitiveHomophilyNum + transitiveDistanceNum

        if (totalStrategyNum != generateSettings.numPlayer) {
            logger.error("Wrong strategy num")
        }

        val strategyPairList: List<Pair<PreSelectionStrategy, SelectionStrategy>> =
            (1..generateSettings.numPlayer).map {
                when (it) {
                    in 1..randomRandomNum ->
                        Pair(PreSelectionStrategy.RANDOM, SelectionStrategy.RANDOM)

                    in (randomRandomNum + 1)..(randomRandomNum + randomPreferentialNum) ->
                        Pair(PreSelectionStrategy.RANDOM, SelectionStrategy.PREFERENTIAL)

                    in (randomRandomNum + randomPreferentialNum + 1)..(randomRandomNum + randomPreferentialNum + randomHomophilyNum) ->
                        Pair(PreSelectionStrategy.RANDOM, SelectionStrategy.HOMOPHILY)

                    in (randomRandomNum + randomPreferentialNum + randomHomophilyNum + 1)..(randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum) ->
                        Pair(PreSelectionStrategy.RANDOM, SelectionStrategy.DISTANCE)

                    in (randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + 1)..(randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + transitiveRandomNum) ->
                        Pair(PreSelectionStrategy.TRANSITIVE, SelectionStrategy.RANDOM)

                    in (randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + transitiveRandomNum + 1)..(randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + transitiveRandomNum + transitivePreferentialNum) ->
                        Pair(PreSelectionStrategy.TRANSITIVE, SelectionStrategy.PREFERENTIAL)

                    in (randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + transitiveRandomNum + transitivePreferentialNum + 1)..(randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + transitiveRandomNum + transitivePreferentialNum + transitiveHomophilyNum) ->
                        Pair(PreSelectionStrategy.TRANSITIVE, SelectionStrategy.HOMOPHILY)

                    in (randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + transitiveRandomNum + transitivePreferentialNum + 1)..(randomRandomNum + randomPreferentialNum + randomHomophilyNum + randomDistanceNum + transitiveRandomNum + transitivePreferentialNum + transitiveHomophilyNum + transitiveDistanceNum) ->
                        Pair(PreSelectionStrategy.TRANSITIVE, SelectionStrategy.DISTANCE)

                    else -> Pair(PreSelectionStrategy.RANDOM, SelectionStrategy.RANDOM)
                }
            }.shuffled(random)

        val distancePowerMin: Double = generateSettings.getOtherDoubleOrDefault(
            "distancePowerMin",
            0.0
        )

        val distancePowerMax: Double = generateSettings.getOtherDoubleOrDefault(
            "distancePowerMax",
            0.0
        )

        val distancePowerGroup: Int = generateSettings.getOtherIntOrDefault(
            "distancePowerGroup",
            1
        )

        val distancePowerNumPerGroup: Int =
            (generateSettings.numPlayer - 1) / distancePowerGroup + 1

        val distancePowerGroupDiff: Double = if (distancePowerGroup > 1) {
            (distancePowerMax - distancePowerMin) / (distancePowerGroup - 1)
        } else {
            0.0
        }

        val distancePowerList: List<Double> = (0 until generateSettings.numPlayer).map {
            distancePowerMin + distancePowerGroupDiff * (it / distancePowerNumPerGroup)
        }.shuffled(random)

        val speedLimitMin: Double = generateSettings.getOtherDoubleOrDefault(
            "speedLimitMin",
            0.0
        )

        val speedLimitMax: Double = generateSettings.getOtherDoubleOrDefault(
            "speedLimitMax",
            0.0
        )

        val speedLimitGroup: Int = generateSettings.getOtherIntOrDefault(
            "speedLimitGroup",
            1
        )

        val speedLimitNumPerGroup: Int = (generateSettings.numPlayer - 1) / speedLimitGroup + 1

        val speedLimitGroupDiff: Double = if (speedLimitGroup > 1) {
            (speedLimitMax - speedLimitMin) / (speedLimitGroup - 1)
        } else {
            0.0
        }

        val speedLimitList: List<Double> = (0 until generateSettings.numPlayer).map {
            speedLimitMin + speedLimitGroupDiff * (it / speedLimitNumPerGroup)
        }.shuffled(random)

        for (i in 1..generateSettings.numPlayer) {
            val playerId: Int = universeState.getNewPlayerId()

            val mutablePlayerData = MutablePlayerData(playerId)

            mutablePlayerData.playerInternalData.playerDataComponentMap.put(
                MutableABMKnowledgeDynamicsData()
            )

            if (sameLocation == 0) {
                // Random location, avoid too close to the boundary by adding a 0.1 width margin
                mutablePlayerData.double4D.x = random.nextDouble(
                    0.1,
                    universeSettings.xDim.toDouble() - 0.1
                )
                mutablePlayerData.double4D.y = random.nextDouble(
                    0.1,
                    universeSettings.yDim.toDouble() - 0.1
                )
                mutablePlayerData.double4D.z = random.nextDouble(
                    0.1,
                    universeSettings.zDim.toDouble() - 0.1
                )
                mutablePlayerData.int4D.x = floor(mutablePlayerData.double4D.x).toInt()
                mutablePlayerData.int4D.y = floor(mutablePlayerData.double4D.y).toInt()
                mutablePlayerData.int4D.z = floor(mutablePlayerData.double4D.z).toInt()
            } else {
                mutablePlayerData.int4D.x = 0
                mutablePlayerData.int4D.y = 0
                mutablePlayerData.int4D.z = 0

                mutablePlayerData.double4D.x = 0.2
                mutablePlayerData.double4D.y = 0.2
                mutablePlayerData.double4D.z = 0.2
            }

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().preSelectionStrategy =
                strategyPairList[i - 1].first

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().selectionStrategy =
                strategyPairList[i - 1].second

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().distancePower =
                distancePowerList[i - 1]

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().speedLimit =
                speedLimitList[i - 1]

            repeat(innovationHypothesisSize) {
                mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                    .innovationHypothesis.add(
                        MutableKnowledgeGene(
                            capability = random.nextInt(0, maxInitialCapability + 1),
                            ability = random.nextInt(0, maxAbility + 1),
                            expertise = 0
                        )
                    )
            }

            data.addPlayerDataToLatestDuration(
                mutablePlayerData = mutablePlayerData,
                currentTime = universeState.getCurrentTime(),
                duration = universeSettings.tDim - 1,
                edgeLength = universeSettings.groupEdgeLength,
            )
        }

        return UniverseData(
            universeData4D = DataSerializer.copy(data),
            universeSettings = universeSettings,
            universeState = universeState,
            commandMap = mutableMapOf(),
            universeGlobalData = DataSerializer.copy(MutableUniverseGlobalData())
        )
    }
}