package relativitization.universe.mechanisms.knowledge

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.MutableKnowledgeGene
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
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
        val numProduct: Int = universeSettings.otherIntMap.getOrElse(
            "numProduct"
        ) {
            logger.error("Missing numProduct")
            20
        }

        val maxCapability: Int = universeSettings.otherIntMap.getOrElse(
            "maxCapability"
        ) {
            logger.error("Missing maxCapability")
            100
        }

        val maxAbility: Int = universeSettings.otherIntMap.getOrElse(
            "maxAbility"
        ) {
            logger.error("Missing maxAbility")
            10
        }

        val maxExpertise: Int = universeSettings.otherIntMap.getOrElse(
            "maxExpertise"
        ) {
            logger.error("Missing maxExpertise")
            20
        }

        val maxProductQuality: Int = universeSettings.otherIntMap.getOrElse(
            "maxProductQuality"
        ) {
            logger.error("Missing maxProductQuality")
            50
        }

        val maxReward: Int = universeSettings.otherIntMap.getOrElse(
            "maxReward"
        ) {
            logger.error("Missing maxReward")
            10
        }

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

        if (playerRank >= maxReward) {
            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .latestReward = 0
        } else {
            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .latestReward = maxReward - playerRank
            mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                .totalReward += maxReward - playerRank
        }

        return listOf()
    }
}