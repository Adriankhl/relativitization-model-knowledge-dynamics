package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData

/**
 * Represent an agent in a SKIN model
 *
 * @property preSelectionStrategy strategy to select potential cooperation candidates
 * @property selectionStrategy strategy to select cooperator from the potential candidates
 * @property knowledgeGeneSet the set of knowledge genes of this agent
 * @property totalReward the reward stored by the agent
 * @property latestReward the reward received by ths agent in the latest turn
 * @property cooperationOutMap cooperation between the key and this agent, proposed by this agent
 * @property cooperationOutWaitMap to-be-confirmed cooperation between the key and this agent,
 * proposed by this agent
 * @property cooperationInMap cooperation between the key and this agent, proposed by other agent
 * @property cooperationLearnMap learn from this cooperator
 */
@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class ABMKnowledgeDynamicsData(
    val preSelectionStrategy: PreSelectionStrategy = PreSelectionStrategy.RANDOM,
    val selectionStrategy: SelectionStrategy = SelectionStrategy.RANDOM,
    val knowledgeGeneSet: List<KnowledgeGene> = listOf(),
    val totalReward: Int = 0,
    val latestReward: Int = 0,
    val cooperationOutMap: Map<Int, Cooperation> = mapOf(),
    val cooperationOutWaitMap: Map<Int, Cooperation> = mapOf(),
    val cooperationInMap: Map<Int, Cooperation> = mapOf(),
    val cooperationLearnMap: Map<Int, Cooperation> = mapOf(),
) : PlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutMap.keys + cooperationInMap.keys

    fun outCooperator(): Set<Int> = cooperationOutMap.keys + cooperationOutWaitMap.keys
}

@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class MutableABMKnowledgeDynamicsData(
    var preSelectionStrategy: PreSelectionStrategy = PreSelectionStrategy.RANDOM,
    var selectionStrategy: SelectionStrategy = SelectionStrategy.RANDOM,
    val knowledgeGeneSet: MutableList<MutableKnowledgeGene> = mutableListOf(),
    var totalReward: Int = 0,
    var latestReward: Int = 0,
    val cooperationOutMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
    val cooperationOutWaitMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
    val cooperationInMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
    val cooperationLearnMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
) : MutablePlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutMap.keys + cooperationInMap.keys

    fun outCooperator(): Set<Int> = cooperationOutMap.keys + cooperationOutWaitMap.keys
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
    val time: Int,
)

@Serializable
data class MutableCooperation(
    var time: Int,
)

enum class PreSelectionStrategy {
    RANDOM,
    TRANSITIVE,
}

enum class SelectionStrategy {
    RANDOM,
    PREFERENTIAL,
    HOMOPHILY,
}