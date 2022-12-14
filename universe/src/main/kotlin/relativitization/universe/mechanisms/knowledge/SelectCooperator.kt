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
import relativitization.universe.maths.physics.Int4D
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.MutableInt4D
import relativitization.universe.maths.sampling.WeightedSample
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.abs
import kotlin.math.pow
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
        val sequentialRun: Int = universeSettings.getOtherIntOrDefault(
            "sequentialRun",
            0
        )

        val maxOutCooperator: Int = universeSettings.getOtherIntOrDefault(
            "maxOutCooperator",
            Int.MAX_VALUE,
        )

        val numPreSelectedFirm: Int = universeSettings.getOtherIntOrDefault(
            "numPreSelectedFirm",
            5
        )

        val incrementalThreshold: Int = universeSettings.getOtherIntOrDefault(
            "incrementalThreshold",
            8
        )

        val preferentialPower: Double = universeSettings.getOtherDoubleOrDefault(
            "preferentialPower",
            1.0
        )

        val homophilyPower: Double = universeSettings.getOtherDoubleOrDefault(
            "homophilyPower",
            1.0
        )


        val latestReward: Int = mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .latestReward

        val isSequential: Boolean = sequentialRun == 1

        val shouldRun: Boolean = if (isSequential) {
            mutablePlayerData.playerId % universeData3DAtPlayer.playerDataMap.size ==
                    mutablePlayerData.int4D.t % universeData3DAtPlayer.playerDataMap.size
        } else {
            true
        }

        val hasTooMuchOutCooperator: Boolean = mutablePlayerData.playerInternalData
            .abmKnowledgeDynamicsData().outCooperator().size >= maxOutCooperator

        return if (latestReward > incrementalThreshold || !shouldRun || hasTooMuchOutCooperator) {
            listOf()
        } else {
            val distancePower: Double = mutablePlayerData.playerInternalData
                .abmKnowledgeDynamicsData().distancePower

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
                        preferentialPower = preferentialPower,
                        random = random,
                    )

                    SelectionStrategy.HOMOPHILY -> selectionHomophily(
                        numSelfCooperator = mutablePlayerData.playerInternalData
                            .abmKnowledgeDynamicsData().numCooperation(),
                        preSelectedSet = preSelectedSet,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        homophilyPower = homophilyPower,
                        random = random,
                    )

                    SelectionStrategy.DISTANCE -> selectionDistance(
                        currentInt4D = mutablePlayerData.int4D,
                        preSelectedSet = preSelectedSet,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        distancePower = distancePower,
                        random = random
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
            .fold(setOf<Int>()) { acc, cooperatorId ->
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
        preferentialPower: Double,
        random: Random,
    ): Int {
        return WeightedSample.sample(
            numItem = 1,
            itemList = preSelectedSet.toList(),
            random = random,
        ) {
            universeData3DAtPlayer.get(it).playerInternalData.abmKnowledgeDynamicsData()
                .numCooperation().toDouble().pow(preferentialPower) + 1E-9
        }.first()
    }

    private fun selectionHomophily(
        numSelfCooperator: Int,
        preSelectedSet: Set<Int>,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        homophilyPower: Double,
        random: Random,
    ): Int {
        return WeightedSample.sample(
            numItem = 1,
            itemList = preSelectedSet.toList(),
            random = random,
        ) {
            val numOtherCooperator: Int = universeData3DAtPlayer.get(it).playerInternalData
                .abmKnowledgeDynamicsData().numCooperation()
            1.0 / (1.0 + abs(numSelfCooperator - numOtherCooperator).toDouble().pow(homophilyPower))
        }.first()
    }

    private fun selectionDistance(
        currentInt4D: MutableInt4D,
        preSelectedSet: Set<Int>,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        distancePower: Double,
        random: Random,
    ): Int {
        return WeightedSample.sample(
            numItem = 1,
            itemList = preSelectedSet.toList(),
            random = random,
        ) {
            val otherInt4D: Int4D = universeData3DAtPlayer.get(it).int4D
            val distance: Int = Intervals.intDistance(currentInt4D, otherInt4D)
            if (distancePower >= 0.0) {
                1.0 / (1.0 + distance.toDouble().pow(distancePower))
            } else {
                1.0 + distance.toDouble().pow(-distancePower)
            }
        }.first()
    }
}