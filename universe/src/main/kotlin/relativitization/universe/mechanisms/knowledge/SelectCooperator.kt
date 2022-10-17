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

object SelectCooperator : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        val numPreSelectedFirm: Int = universeSettings.otherIntMap.getOrElse(
            "numPreSelectedFirm"
        ) {
            logger.error("Missing numPreSelectedFirm")
            5
        }

        val preSelectionStrategy: String = universeSettings.otherStringMap.getOrElse(
            "preSelectionStrategy"
        ) {
            logger.error("Missing preSelectionStrategy")
            ""
        }

        val selectionStrategy: String = universeSettings.otherStringMap.getOrElse(
            "selectionStrategy"
        ) {
            logger.error("Missing selectionStrategy")
            ""
        }

        val preSelectedSet: Set<Int> = when (preSelectionStrategy) {
            "random" -> {
                preSelectionRandom(
                    universeData3DAtPlayer = universeData3DAtPlayer,
                    numPreSelectedFirm = numPreSelectedFirm,
                    random = random,
                )
            }

            "transitive" -> {
                preSelectionTransitive(
                    mutablePlayerData = mutablePlayerData,
                    universeData3DAtPlayer = universeData3DAtPlayer,
                    numPreSelectedFirm = numPreSelectedFirm,
                    random = random,
                )
            }

            else -> {
                logger.error("Wrong preSelectionStrategy")
                setOf()
            }
        }

        return listOf()
    }

    private fun preSelectionRandom(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        numPreSelectedFirm: Int,
        random: Random
    ): Set<Int> {
        return universeData3DAtPlayer.playerDataMap.keys.asSequence().shuffled(random)
            .take(numPreSelectedFirm).toSet()
    }

    private fun preSelectionTransitive(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        numPreSelectedFirm: Int,
        random: Random,
    ): Set<Int> {
        val allIndirectSet: Set<Int> = mutablePlayerData.playerInternalData
            .abmKnowledgeDynamicsData().allConfirmedCooperator(
                thisPlayerId = universeData3DAtPlayer.id,
                universeData3DAtPlayer = universeData3DAtPlayer
            ).fold(setOf()) { acc, cooperatorId ->
                acc + universeData3DAtPlayer.get(cooperatorId).playerInternalData.abmKnowledgeDynamicsData()
                    .allConfirmedCooperator(
                        thisPlayerId = cooperatorId,
                        universeData3DAtPlayer = universeData3DAtPlayer
                    )
            }

        return if (allIndirectSet.size >= numPreSelectedFirm) {
            allIndirectSet.shuffled(random).take(numPreSelectedFirm).toSet()
        } else {
            allIndirectSet + universeData3DAtPlayer.playerDataMap.keys.asSequence()
                .shuffled(random).take(numPreSelectedFirm - allIndirectSet.size)
        }
    }
}