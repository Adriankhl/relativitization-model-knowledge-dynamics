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
 * @property knowledgeGeneList the set of knowledge genes of this agent
 * @property innovationHypothesis the collection of knowledge genes used in production
 * @property productId the id of the produced good
 * @property productQuality the quality of the produced good
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
    val knowledgeGeneList: List<KnowledgeGene> = listOf(),
    val innovationHypothesis: List<KnowledgeGene> = listOf(),
    val productId: Int = -1,
    val productQuality: Double = 0.0,
    val totalReward: Int = 0,
    val latestReward: Int = 0,
    val cooperationOutMap: Map<Int, Cooperation> = mapOf(),
    val cooperationOutWaitMap: Map<Int, Cooperation> = mapOf(),
    val cooperationInMap: Map<Int, Cooperation> = mapOf(),
    val cooperationLearnMap: Map<Int, Cooperation> = mapOf(),
    val numSelfRadicalInnovation: Int = 0,
    val numSelfIncrementalInnovation: Int = 0,
    val numCooperationRadicalInnovation: Int = 0,
    val numCooperationIncrementalInnovation: Int = 0,
) : PlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutMap.keys + cooperationInMap.keys

    /**
     * Not equal to allCooperator().size, count out and in separately
     */
    fun numCooperation(): Int = cooperationOutMap.size + cooperationInMap.size

    fun outCooperator(): Set<Int> = cooperationOutMap.keys + cooperationOutWaitMap.keys
}

@Serializable
@SerialName("ABMKnowledgeDynamicsData")
data class MutableABMKnowledgeDynamicsData(
    var preSelectionStrategy: PreSelectionStrategy = PreSelectionStrategy.RANDOM,
    var selectionStrategy: SelectionStrategy = SelectionStrategy.RANDOM,
    val knowledgeGeneList: MutableList<MutableKnowledgeGene> = mutableListOf(),
    val innovationHypothesis: MutableList<MutableKnowledgeGene> = mutableListOf(),
    var productId: Int = -1,
    var productQuality: Double = 0.0,
    var totalReward: Int = 0,
    var latestReward: Int = 0,
    val cooperationOutMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
    val cooperationOutWaitMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
    val cooperationInMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
    val cooperationLearnMap: MutableMap<Int, MutableCooperation> = mutableMapOf(),
    var numSelfRadicalInnovation: Int = 0,
    var numSelfIncrementalInnovation: Int = 0,
    var numCooperationRadicalInnovation: Int = 0,
    var numCooperationIncrementalInnovation: Int = 0,
) : MutablePlayerDataComponent() {
    fun allCooperator(): Set<Int> = cooperationOutMap.keys + cooperationInMap.keys

    /**
     * Not equal to allCooperator().size, count out and in separately
     */
    fun numCooperation(): Int = cooperationOutMap.size + cooperationInMap.size

    fun outCooperator(): Set<Int> = cooperationOutMap.keys + cooperationOutWaitMap.keys
}

fun PlayerInternalData.abmKnowledgeDynamicsData(): ABMKnowledgeDynamicsData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.abmKnowledgeDynamicsData(): MutableABMKnowledgeDynamicsData =
    playerDataComponentMap.get()

@Serializable
data class KnowledgeGene(
    val capability: Int,
    val ability: Int,
    val expertise: Int,
)

@Serializable
data class MutableKnowledgeGene(
    var capability: Int,
    var ability: Int,
    var expertise: Int,
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
    DISTANCE,
}