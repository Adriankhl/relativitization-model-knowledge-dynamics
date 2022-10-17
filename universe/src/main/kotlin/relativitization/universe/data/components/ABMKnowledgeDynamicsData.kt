package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
import relativitization.universe.data.UniverseData3DAtPlayer

/**
 * Represent an agent in a SKIN model
 *
 * @property knowledgeGeneSet the set of knowledge genes of this agent
 * @property totalReward the reward stored by the agent
 * @property latestReward the reward received by ths agent in the latest turn
 * @property cooperationOutList cooperation this agent proposes
 * @property cooperationInList cooperation which proposed by other agents
 */
@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class ABMKnowledgeDynamicsData(
    val knowledgeGeneSet: List<KnowledgeGene> = listOf(),
    val totalReward: Int = 0,
    val latestReward: Int = 0,
    val cooperationOutList: List<Cooperation> = listOf(),
    val cooperationInList: List<Cooperation> = listOf(),
) : PlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutList.map {
        it.otherPlayerId
    }.toSet() + cooperationInList.map {
        it.otherPlayerId
    }

    fun allConfirmedCooperator(
        thisPlayerId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Set<Int> {
        val inSet: Set<Int> = cooperationInList.filter { thisCooperation ->
            universeData3DAtPlayer.get(thisCooperation.otherPlayerId).playerInternalData.abmKnowledgeDynamicsData()
                .cooperationOutList.any { otherCooperation ->
                    otherCooperation.otherPlayerId == thisPlayerId
                }
        }.map {
            it.otherPlayerId
        }.toSet()

        val outSet: Set<Int> = cooperationOutList.filter { thisCooperation ->
            universeData3DAtPlayer.get(thisCooperation.otherPlayerId).playerInternalData.abmKnowledgeDynamicsData()
                .cooperationInList.any { otherCooperation ->
                    otherCooperation.otherPlayerId == thisPlayerId
                }
        }.map {
            it.otherPlayerId
        }.toSet()

        return inSet + outSet
    }
}

@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class MutableABMKnowledgeDynamicsData(
    val knowledgeGeneSet: MutableList<MutableKnowledgeGene> = mutableListOf(),
    var totalReward: Int = 0,
    var latestReward: Int = 0,
    val cooperationOutList: MutableList<Cooperation> = mutableListOf(),
    val cooperationInList: MutableList<Cooperation> = mutableListOf(),
) : MutablePlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutList.map {
        it.otherPlayerId
    }.toSet() + cooperationInList.map {
        it.otherPlayerId
    }

    fun allConfirmedCooperator(
        thisPlayerId: Int,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ): Set<Int> {
        val inSet: Set<Int> = cooperationInList.filter { thisCooperation ->
            universeData3DAtPlayer.get(thisCooperation.otherPlayerId).playerInternalData.abmKnowledgeDynamicsData()
                .cooperationOutList.any { otherCooperation ->
                    otherCooperation.otherPlayerId == thisPlayerId
                }
        }.map {
            it.otherPlayerId
        }.toSet()

        val outSet: Set<Int> = cooperationOutList.filter { thisCooperation ->
            universeData3DAtPlayer.get(thisCooperation.otherPlayerId).playerInternalData.abmKnowledgeDynamicsData()
                .cooperationInList.any { otherCooperation ->
                    otherCooperation.otherPlayerId == thisPlayerId
                }
        }.map {
            it.otherPlayerId
        }.toSet()

        return inSet + outSet
    }
}

fun PlayerInternalData.abmKnowledgeDynamicsData(): ABMKnowledgeDynamicsData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.abmKnowledgeDynamicsData(): MutableABMKnowledgeDynamicsData =
    playerDataComponentMap.get()

@Serializable
data class KnowledgeGene(
    val capabilities: Int,
    val ability: Int,
    val expertise: Int,
)

@Serializable
data class MutableKnowledgeGene(
    val capabilities: Int,
    val ability: Int,
    val expertise: Int,
)

@Serializable
data class Cooperation(
    val otherPlayerId: Int,
    val time: Int,
)

@Serializable
data class MutableCooperation(
    val otherPlayerId: Int,
    var time: Int,
)