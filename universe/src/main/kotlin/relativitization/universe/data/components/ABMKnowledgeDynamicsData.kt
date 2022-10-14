package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represent an agent in a SKIN model
 *
 * @property collaboratorSet the collaborator of this agent
 * @property knowledgeGeneSet the set of knowledge genes of this agent
 * @property totalReward the reward stored by the agent
 * @property latestReward the reward received by ths agent in the latest turn
 */
@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class ABMKnowledgeDynamicsData(
    val collaboratorSet: Set<Int> = setOf(),
    val knowledgeGeneSet: List<KnowledgeGene> = listOf(),
    val totalReward: Int = 0,
    val latestReward: Int = 0,
) : PlayerDataComponent()

@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class MutableABMKnowledgeDynamicsData(
    val collaboratorSet: MutableSet<Int> = mutableSetOf(),
    val knowledgeGeneSet: MutableList<MutableKnowledgeGene> = mutableListOf(),
    var totalReward: Int = 0,
    var newReward: Int = 0,
) : MutablePlayerDataComponent()

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