package relativitization.universe.mechanisms.knowledge

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutableCooperation
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
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

        if (shouldRun) {
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

            val endCooperator: Map<Int, MutableCooperation> = mutablePlayerData.playerInternalData
                .abmKnowledgeDynamicsData().cooperationOutMap.filterValues {
                    it.time >= cooperationLength
                } + mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().cooperationOutMap
                .filterKeys {
                    !universeData3DAtPlayer.get(it).playerInternalData.abmKnowledgeDynamicsData()
                        .cooperationInMap.containsKey(mutablePlayerData.playerId)
                }

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .cooperationOutMap -= endCooperator.keys

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .cooperationLearnMap += endCooperator

            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().cooperationInMap
                .values.removeAll {
                    it.time >= cooperationLength
                }
        }

        return listOf()
    }
}