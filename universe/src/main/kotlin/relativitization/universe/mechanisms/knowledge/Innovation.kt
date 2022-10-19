package relativitization.universe.mechanisms.knowledge

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

object Innovation : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val radicalThreshold: Int = universeSettings.otherIntMap.getOrElse(
            "radicalThreshold"
        ) {
            logger.error("Missing radicalThreshold")
            6
        }

        val incrementalThreshold: Int = universeSettings.otherIntMap.getOrElse(
            "incrementalThreshold"
        ) {
            logger.error("Missing incrementalThreshold")
            8
        }

        val maxExpertise: Int = universeSettings.otherIntMap.getOrElse(
            "maxExpertise"
        ) {
            logger.error("Missing maxExpertise")
            20
        }

        // Learning by doing
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().innovationHypothesis
            .forEach {
                if (it.expertise < maxExpertise) {
                    it.expertise += 1
                }
            }

        return listOf()
    }
}