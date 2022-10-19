package relativitization.universe.generate.abm

import relativitization.universe.ai.EmptyAI
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.UniverseState
import relativitization.universe.data.components.MutableABMKnowledgeDynamicsData
import relativitization.universe.data.components.MutableKnowledgeGene
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.maths.grid.Grids.create4DGrid
import relativitization.universe.utils.RelativitizationLogManager
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

        val maxAbility: Int = universeSettings.otherIntMap.getOrElse(
            "maxAbility"
        ) {
            logger.error("Missing maxAbility")
            10
        }

        for (i in 1..settings.numPlayer) {
            val playerId: Int = universeState.getNewPlayerId()

            val mutablePlayerData = MutablePlayerData(playerId)

            mutablePlayerData.playerInternalData.playerDataComponentMap.put(
                MutableABMKnowledgeDynamicsData()
            )

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