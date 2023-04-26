package relativitization.universe.mechanisms.knowledge

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.data.commands.EndCooperationCommand
import relativitization.universe.data.components.MutableCooperation
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import kotlin.random.Random

object SyncCooperator : Mechanism() {
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

        val cooperationLength: Int = universeSettings.getOtherIntOrDefault(
            "cooperationLength",
            5
        )

        val isSequential: Boolean = sequentialRun == 1

        val shouldRun: Boolean = if (isSequential) {
            mutablePlayerData.playerId % universeData3DAtPlayer.playerDataMap.size ==
                    mutablePlayerData.int4D.t % universeData3DAtPlayer.playerDataMap.size
        } else {
            true
        }

        val endCooperatorSet: Set<Int> = if (shouldRun) {
            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().cooperationOutMap
                .values.forEach {
                    it.time += 1
                }

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().cooperationInMap
                .values.forEach {
                    it.time += 1
                }

            val confirmedCooperator: Map<Int, MutableCooperation> =
                mutablePlayerData.playerInternalData
                    .abmKnowledgeDynamicsData().cooperationOutWaitMap.filterKeys {
                        universeData3DAtPlayer.get(it).playerInternalData.abmKnowledgeDynamicsData()
                            .cooperationInMap.containsKey(mutablePlayerData.playerId)
                    }

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .cooperationOutWaitMap -= confirmedCooperator.keys

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .cooperationOutMap += confirmedCooperator

            val endCooperators: Map<Int, MutableCooperation> = mutablePlayerData.playerInternalData
                .abmKnowledgeDynamicsData().cooperationOutMap.filterValues {
                    it.time >= cooperationLength
                }

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .cooperationOutMap -= endCooperators.keys

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .cooperationLearnMap += endCooperators

            endCooperators.keys
        } else {
            setOf()
        }

        return endCooperatorSet.map {
            EndCooperationCommand(it)
        }
    }
}