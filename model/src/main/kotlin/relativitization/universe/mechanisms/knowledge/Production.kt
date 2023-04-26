package relativitization.universe.mechanisms.knowledge

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.data.components.MutableKnowledgeGene
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import kotlin.random.Random

object Production : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val numProduct: Int = universeSettings.getOtherIntOrDefault(
            "numProduct",
            20
        )

        val maxCapability: Int = universeSettings.getOtherIntOrDefault(
            "maxCapability",
            100
        )

        val maxAbility: Int = universeSettings.getOtherIntOrDefault(
            "maxAbility",
            10
        )

        val maxExpertise: Int = universeSettings.getOtherIntOrDefault(
            "maxExpertise",
            20
        )

        val maxProductQuality: Int = universeSettings.getOtherIntOrDefault(
            "maxProductQuality",
            50
        )

        val maxReward: Int = universeSettings.getOtherIntOrDefault(
            "maxReward",
            10
        )

        val innovationHypothesis: List<MutableKnowledgeGene> = mutablePlayerData.playerInternalData
            .abmKnowledgeDynamicsData().innovationHypothesis

        val productId: Int = innovationHypothesis.sumOf { it.capability } % numProduct

        val capabilityFactor: Double = innovationHypothesis.sumOf {
            it.capability.toDouble()
        } / (maxCapability * innovationHypothesis.size)


        val abilityFactor: Double = (innovationHypothesis.sumOf { it.ability } % (maxAbility + 1))
            .toDouble() / maxAbility

        val expertiseFactor: Double = innovationHypothesis.sumOf {
            it.expertise.toDouble()
        } / (maxExpertise * innovationHypothesis.size)

        val productQuality: Double = maxProductQuality * capabilityFactor *
                abilityFactor * expertiseFactor

        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().productId = productId
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().capabilityFactor =
            capabilityFactor
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().abilityFactor =
            abilityFactor
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().expertiseFactor =
            expertiseFactor
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().productQuality =
            productQuality

        // Compute reward
        val idQualityMap: Map<Int, Double> = mapOf(
            mutablePlayerData.playerId to mutablePlayerData.playerInternalData
                .abmKnowledgeDynamicsData().productQuality
        ) + universeData3DAtPlayer.playerDataMap.filterValues {
            val isOtherPlayer: Boolean = it.playerId != mutablePlayerData.playerId
            val isProductIdSame: Boolean = it.playerInternalData.abmKnowledgeDynamicsData()
                .productId == mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .productId
            isOtherPlayer && isProductIdSame
        }.values.map {
            it.playerId to it.playerInternalData.abmKnowledgeDynamicsData().productQuality
        }

        val sortedId: List<Int> = idQualityMap.keys.shuffled(random).sortedByDescending {
            idQualityMap.getValue(it)
        }

        // Rank start from 0
        val playerRank: Int = sortedId.indexOf(mutablePlayerData.playerId)

        val reward: Int = if (playerRank >= maxReward) {
            0
        } else {
            maxReward - playerRank
        }

        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .latestReward = reward
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .totalReward += reward

        return listOf()
    }
}