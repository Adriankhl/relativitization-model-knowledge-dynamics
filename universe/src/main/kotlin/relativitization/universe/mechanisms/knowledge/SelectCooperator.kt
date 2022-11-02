package relativitization.universe.mechanisms.knowledge

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.AskCooperationCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutableCooperation
import relativitization.universe.data.components.PreSelectionStrategy
import relativitization.universe.data.components.SelectionStrategy
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.sampling.WeightedReservoir
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.abs
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

        val incrementalThreshold: Int = universeSettings.otherIntMap.getOrElse(
            "incrementalThreshold"
        ) {
            logger.error("Missing incrementalThreshold")
            8
        }

        val latestReward: Int = mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .latestReward

        return if (latestReward > incrementalThreshold) {
            listOf()
        } else {
            val preSelectionStrategy: PreSelectionStrategy = mutablePlayerData.playerInternalData
                .abmKnowledgeDynamicsData().preSelectionStrategy

            val preSelectedSet: Set<Int> = when (preSelectionStrategy) {
                PreSelectionStrategy.RANDOM -> {
                    preSelectionRandom(
                        mutablePlayerData = mutablePlayerData,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        numPreSelectedFirm = numPreSelectedFirm,
                        random = random,
                    )
                }

                PreSelectionStrategy.TRANSITIVE -> {
                    preSelectionTransitive(
                        mutablePlayerData = mutablePlayerData,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        numPreSelectedFirm = numPreSelectedFirm,
                        random = random,
                    )
                }
            }

            if (preSelectedSet.isEmpty()) {
                listOf()
            } else {
                val selectionStrategy: SelectionStrategy = mutablePlayerData.playerInternalData
                    .abmKnowledgeDynamicsData().selectionStrategy

                val selectedCooperator: Int = when (selectionStrategy) {
                    SelectionStrategy.RANDOM -> selectionRandom(
                        preSelectedSet = preSelectedSet,
                        random = random
                    )

                    SelectionStrategy.PREFERENTIAL -> selectionPreferential(
                        preSelectedSet = preSelectedSet,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        random = random,
                    )

                    SelectionStrategy.HOMOPHILY -> selectionHomophily(
                        numSelfCooperator = mutablePlayerData.playerInternalData
                            .abmKnowledgeDynamicsData().allCooperator().size,
                        preSelectedSet = preSelectedSet,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        random = random,
                    )
                }

                mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                    .cooperationOutWaitMap[selectedCooperator] = MutableCooperation(0)

                listOf(
                    AskCooperationCommand(
                        toId = selectedCooperator
                    )
                )
            }
        }
    }

    private fun preSelectionRandom(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        numPreSelectedFirm: Int,
        random: Random
    ): Set<Int> {
        val validCooperator: List<Int> = universeData3DAtPlayer.playerDataMap.keys
            .filter {
                it != mutablePlayerData.playerId && !mutablePlayerData.playerInternalData
                    .abmKnowledgeDynamicsData().outCooperator().contains(it)
            }

        return validCooperator.asSequence().shuffled(random).take(numPreSelectedFirm).toSet()
    }

    private fun preSelectionTransitive(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        numPreSelectedFirm: Int,
        random: Random,
    ): Set<Int> {
        val allIndirectSet: Set<Int> = mutablePlayerData.playerInternalData
            .abmKnowledgeDynamicsData().allCooperator()
            .fold<Int, Set<Int>>(setOf()) { acc, cooperatorId ->
                acc + universeData3DAtPlayer.get(cooperatorId).playerInternalData
                    .abmKnowledgeDynamicsData().allCooperator()
            }.filter {
                it != mutablePlayerData.playerId && !mutablePlayerData.playerInternalData
                    .abmKnowledgeDynamicsData().outCooperator().contains(it)
            }.toSet()

        return when {
            allIndirectSet.size >= numPreSelectedFirm -> {
                allIndirectSet.shuffled(random).take(numPreSelectedFirm).toSet()
            }

            else -> {
                val validCooperator: List<Int> = universeData3DAtPlayer.playerDataMap.keys
                    .filter {
                        it != mutablePlayerData.playerId && !mutablePlayerData.playerInternalData
                            .abmKnowledgeDynamicsData().outCooperator()
                            .contains(it) && !allIndirectSet.contains(it)
                    }

                allIndirectSet + validCooperator.asSequence().shuffled(random)
                    .take(numPreSelectedFirm - allIndirectSet.size)
            }
        }
    }

    private fun selectionRandom(
        preSelectedSet: Set<Int>,
        random: Random,
    ): Int {
        return preSelectedSet.asSequence().shuffled(random).first()
    }

    private fun selectionPreferential(
        preSelectedSet: Set<Int>,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): Int {
        return WeightedReservoir.aRes(
            numItem = 1,
            itemList = preSelectedSet.toList(),
            random = random,
        ) {
            universeData3DAtPlayer.get(it).playerInternalData.abmKnowledgeDynamicsData()
                .allCooperator().size.toDouble() + 1E-9
        }.first()
    }

    private fun selectionHomophily(
        numSelfCooperator: Int,
        preSelectedSet: Set<Int>,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ): Int {
        return WeightedReservoir.aRes(
            numItem = 1,
            itemList = preSelectedSet.toList(),
            random = random,
        ) {
            val numOtherCooperator: Int = universeData3DAtPlayer.get(it).playerInternalData
                .abmKnowledgeDynamicsData().allCooperator().size
            1.0 / (1.0 + abs(numSelfCooperator - numOtherCooperator))
        }.first()
    }
}