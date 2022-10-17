package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData

/**
 * Represent an agent in a SKIN model
 *
 * @property knowledgeGeneSet the set of knowledge genes of this agent
 * @property totalReward the reward stored by the agent
 * @property latestReward the reward received by ths agent in the latest turn
 * @property cooperationOutSet cooperation this agent proposes
 * @property cooperationInSet cooperation which proposed by other agents
 */
@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class ABMKnowledgeDynamicsData(
    val knowledgeGeneSet: List<KnowledgeGene> = listOf(),
    val totalReward: Int = 0,
    val latestReward: Int = 0,
    val cooperationOutSet: Set<Cooperation> = setOf(),
    val cooperationInSet: Set<Cooperation> = setOf(),
) : PlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutSet.map {
        it.toId
    }.toSet() + cooperationInSet.map {
        it.fromId
    }
}

@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class MutableABMKnowledgeDynamicsData(
    val knowledgeGeneSet: MutableList<MutableKnowledgeGene> = mutableListOf(),
    var totalReward: Int = 0,
    var latestReward: Int = 0,
    val cooperationOutSet: MutableSet<Cooperation> = mutableSetOf(),
    val cooperationInSet: MutableSet<Cooperation> = mutableSetOf(),
) : MutablePlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutSet.map {
        it.toId
    }.toSet() + cooperationInSet.map {
        it.fromId
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
    val fromId: Int,
    val toId: Int,
    val time: Int,
)

@Serializable
data class MutableCooperation(
    val fromId: Int,
    val toId: Int,
    var time: Int,
)