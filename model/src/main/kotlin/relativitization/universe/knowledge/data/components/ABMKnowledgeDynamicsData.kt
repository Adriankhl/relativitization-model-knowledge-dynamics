package relativitization.universe.knowledge.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.core.data.components.MutablePlayerDataComponent
import relativitization.universe.core.data.components.PlayerDataComponent

/**
 * Represent an agent in a SKIN model
 *
 * @property preSelectionStrategy strategy to select potential cooperation candidates
 * @property selectionStrategy strategy to select cooperator from the potential candidates
 * @property knowledgeGeneList the set of knowledge genes of this agent
 * @property distancePower how strongly the cooperator selection is affected by distance,
 *  only active when selectionStrategy is DISTANCE
 * @property innovationHypothesis the collection of knowledge genes used in production
 * @property productId the id of the produced good
 * @property capabilityFactor the effect of capability on product quality
 * @property abilityFactor the effect of ability on product quality
 * @property expertiseFactor the effect of expertise on product quality
 * @property productQuality the quality of the produced good
 * @property totalReward the reward stored by the agent
 * @property latestReward the reward received by ths agent in the latest turn
 * @property cooperationOutMap cooperation between the key and this agent, proposed by this agent
 * @property cooperationOutWaitMap to-be-confirmed cooperation between the key and this agent,
 *  proposed by this agent
 * @property cooperationInMap cooperation between the key and this agent, proposed by other agent
 * @property cooperationLearnMap learn from this cooperator
 * @property numSelfRadicalInnovation store the number of radical innovation done by this player
 * @property numSelfIncrementalInnovation store the number of incremental innovation done by
 *  this player
 * @property numCooperationRadicalInnovation store the number of radical innovation done by
 *  cooperation
 * @property numCooperationIncrementalInnovation store the number of incremental innovation done by
 *  cooperation
 * @property speedLimit the maximum movement speed of this player
 */
@GenerateImmutable
@SerialName("ABMKnowledgeDynamicsData")
data class MutableABMKnowledgeDynamicsData(
    var preSelectionStrategy: PreSelectionStrategy = PreSelectionStrategy.RANDOM,
    var selectionStrategy: SelectionStrategy = SelectionStrategy.RANDOM,
    var distancePower: Double = 0.0,
    val knowledgeGeneList: MutableList<MutableKnowledgeGene> = mutableListOf(),
    val innovationHypothesis: MutableList<MutableKnowledgeGene> = mutableListOf(),
    var productId: Int = -1,
    var capabilityFactor: Double = -1.0,
    var abilityFactor: Double = -1.0,
    var expertiseFactor: Double = -1.0,
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
    var speedLimit: Double = 0.0,
    var restMass: Double = 1.0,
) : MutablePlayerDataComponent()

fun ABMKnowledgeDynamicsData.allCooperator(): Set<Int> = cooperationOutMap.keys + cooperationInMap.keys

fun MutableABMKnowledgeDynamicsData.allCooperator(): Set<Int> = cooperationOutMap.keys + cooperationInMap.keys


/**
 * Not equal to allCooperator().size, count out and in separately
 */
fun ABMKnowledgeDynamicsData.numCooperation(): Int = cooperationOutMap.size + cooperationInMap.size

fun MutableABMKnowledgeDynamicsData.numCooperation(): Int = cooperationOutMap.size + cooperationInMap.size

fun ABMKnowledgeDynamicsData.outCooperator(): Set<Int> = cooperationOutMap.keys + cooperationOutWaitMap.keys

fun MutableABMKnowledgeDynamicsData.outCooperator(): Set<Int> = cooperationOutMap.keys + cooperationOutWaitMap.keys

@GenerateImmutable
data class MutableKnowledgeGene(
    var capability: Int,
    var ability: Int,
    var expertise: Int,
)

@GenerateImmutable
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

fun PlayerInternalData.abmKnowledgeDynamicsData(): ABMKnowledgeDynamicsData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.abmKnowledgeDynamicsData(): MutableABMKnowledgeDynamicsData =
    playerDataComponentMap.get()
