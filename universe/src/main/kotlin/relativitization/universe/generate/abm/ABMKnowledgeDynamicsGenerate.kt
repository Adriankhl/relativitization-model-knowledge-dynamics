package relativitization.universe.generate.abm

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.components.MutableABMKnowledgeDynamicsData
import relativitization.universe.data.components.MutableKnowledgeGene
import relativitization.universe.data.components.PreSelectionStrategy
import relativitization.universe.data.components.SelectionStrategy
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.floor
import kotlin.random.Random

object ABMKnowledgeDynamicsGenerate : ABMGenerateUniverseMethod() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun generate(settings: GenerateSettings, random: Random): UniverseData {
        val universeSettings: UniverseSettings = DataSerializer.copy(settings.universeSettings)

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

        val maxInitialCapability: Int = settings.otherIntMap.getOrElse(
            "maxInitialCapability"
        ) {
            logger.error("Missing maxCapability")
            30
        }

        val innovationHypothesisSize: Int = settings.otherIntMap.getOrElse(
            "innovationHypothesisSize"
        ) {
            logger.error("Missing innovationHypothesisSize")
            3
        }

        val preSelectionTransitiveNum: Int = settings.otherIntMap.getOrElse(
            "preSelectionTransitiveNum"
        ) {
            logger.error("Missing preSelectionTransitiveNum")
            0
        }

        if (preSelectionTransitiveNum > settings.numPlayer) {
            logger.error("Wrong preSelection num")
        }

        val selectionPreferentialNum: Int = settings.otherIntMap.getOrElse(
            "selectionPreferentialNum"
        ) {
            logger.error("Missing selectionPreferentialNum")
            0
        }

        val selectionHomophilyNum: Int = settings.otherIntMap.getOrElse(
            "selectionHomophilyNum"
        ) {
            logger.error("Missing selectionHomophilyNum")
            0
        }

        if (selectionPreferentialNum + selectionHomophilyNum > settings.numPlayer) {
            logger.error("Wrong selection num")
        }

        val maxAbility: Int = universeSettings.otherIntMap.getOrElse(
            "maxAbility"
        ) {
            logger.error("Missing maxAbility")
            10
        }

        val preSelectionStrategyList: List<PreSelectionStrategy> = (1..settings.numPlayer).map {
            when {
                it <= preSelectionTransitiveNum -> PreSelectionStrategy.TRANSITIVE
                else -> PreSelectionStrategy.RANDOM
            }
        }.shuffled(random)

        val selectionStrategyList: List<SelectionStrategy> = (1..settings.numPlayer).map {
            when {
                it <= selectionPreferentialNum -> SelectionStrategy.PREFERENTIAL
                it in (selectionPreferentialNum + 1)..selectionPreferentialNum + selectionHomophilyNum -> SelectionStrategy.HOMOPHILY
                else -> SelectionStrategy.RANDOM
            }
        }.shuffled(random)

        for (i in 1..settings.numPlayer) {
            val playerId: Int = universeState.getNewPlayerId()

            val mutablePlayerData = MutablePlayerData(playerId)

            mutablePlayerData.playerInternalData.playerDataComponentMap.put(
                MutableABMKnowledgeDynamicsData()
            )

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

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().preSelectionStrategy =
                preSelectionStrategyList[i - 1]

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().selectionStrategy =
                selectionStrategyList[i - 1]

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
            universeGlobalData = UniverseGlobalData()
        )
    }
}