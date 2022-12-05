package relativitization.universe.mechanisms.knowledge

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.KnowledgeGene
import relativitization.universe.data.components.MutableKnowledgeGene
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

object Innovation : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val radicalThreshold: Int = universeSettings.getOtherIntOrDefault(
            "radicalThreshold",
            6
        )

        val incrementalThreshold: Int = universeSettings.getOtherIntOrDefault(
            "incrementalThreshold",
            8
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

        val forgetProbability: Double = universeSettings.getOtherDoubleOrDefault(
            "forgetProbability",
            0.05
        )

        val radicalInnovationProbability: Double = universeSettings.getOtherDoubleOrDefault(
            "radicalInnovationProbability",
            0.4
        )

        val incrementalInnovationProbability: Double = universeSettings.getOtherDoubleOrDefault(
            "incrementalInnovationProbability",
            0.4
        )

        // Learning by doing
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().innovationHypothesis
            .forEach {
                if (it.expertise < maxExpertise) {
                    it.expertise += 1
                }
            }

        // Forgetting by not doing
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().knowledgeGeneList
            .removeAll {
                if (it.expertise == 0) {
                    random.nextDouble() < forgetProbability
                } else {
                    false
                }
            }

        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().knowledgeGeneList
            .forEach {
                if (it.expertise > 0) {
                    it.expertise -= 1
                }
            }

        val latestReward: Int = mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .latestReward

        when {
            latestReward <= radicalThreshold -> {
                if (random.nextDouble() < radicalInnovationProbability) {
                    radicalSelfInnovation(
                        mutablePlayerData = mutablePlayerData,
                        maxCapability = maxCapability,
                        maxAbility = maxAbility,
                        random = random
                    )
                }

                if (random.nextDouble() < radicalInnovationProbability) {
                    radicalCooperationInnovation(
                        mutablePlayerData = mutablePlayerData,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        random = random
                    )
                }

                // Change innovation hypothesis
                if (mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().knowledgeGeneList.isNotEmpty()) {
                    val g1: MutableKnowledgeGene = mutablePlayerData.playerInternalData
                        .abmKnowledgeDynamicsData().innovationHypothesis.asSequence()
                        .shuffled(random).first()

                    val g2: MutableKnowledgeGene = mutablePlayerData.playerInternalData
                        .abmKnowledgeDynamicsData().knowledgeGeneList.asSequence()
                        .shuffled(random).first()

                    mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                        .innovationHypothesis.remove(g1)

                    mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                        .innovationHypothesis.add(g2)

                    mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                        .knowledgeGeneList.remove(g2)

                    mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                        .knowledgeGeneList.add(g1)
                }
            }

            latestReward in (radicalThreshold + 1) .. incrementalThreshold -> {
                if (random.nextDouble() < incrementalInnovationProbability) {
                    incrementalSelfInnovation(
                        mutablePlayerData = mutablePlayerData,
                        maxAbility = maxAbility,
                        random = random
                    )
                }

                if (random.nextDouble() < incrementalInnovationProbability) {
                    incrementalCooperationInnovation(
                        mutablePlayerData = mutablePlayerData,
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        random = random
                    )
                }
            }

            else -> {}
        }

        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().cooperationLearnMap.clear()

        return listOf()
    }

    private fun radicalSelfInnovation(
        mutablePlayerData: MutablePlayerData,
        maxCapability: Int,
        maxAbility: Int,
        random: Random,
    ) {
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().knowledgeGeneList
            .add(
                MutableKnowledgeGene(
                    capability = random.nextInt(0, maxCapability + 1),
                    ability = random.nextInt(0, maxAbility + 1),
                    expertise = 0
                )
            )
    }

    private fun radicalCooperationInnovation(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ) {
        val geneList: List<KnowledgeGene> = mutablePlayerData.playerInternalData
            .abmKnowledgeDynamicsData().cooperationLearnMap.keys.map {
                universeData3DAtPlayer.get(it)
                    .playerInternalData.abmKnowledgeDynamicsData().innovationHypothesis
                    .asSequence().shuffled(random).first().copy(
                        expertise = 0
                    )
            }
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().knowledgeGeneList.addAll(
            DataSerializer.copy(geneList)
        )
    }

    private fun incrementalSelfInnovation(
        mutablePlayerData: MutablePlayerData,
        maxAbility: Int,
        random: Random,
    ) {
        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().innovationHypothesis
            .asSequence().shuffled(random).first().ability = random.nextInt(0, maxAbility + 1)
    }

    private fun incrementalCooperationInnovation(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        random: Random,
    ) {
        val geneList: List<KnowledgeGene> = mutablePlayerData.playerInternalData
            .abmKnowledgeDynamicsData().cooperationLearnMap.keys.flatMap {
                universeData3DAtPlayer.get(it).playerInternalData.abmKnowledgeDynamicsData()
                    .innovationHypothesis
            }

        val geneMap: Map<Int, List<KnowledgeGene>> = geneList.groupBy { it.capability }
            .filterKeys { geneCapability ->
                mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                    .innovationHypothesis.any {
                        it.capability == geneCapability
                    }
            }

        mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .innovationHypothesis.filter {
                geneMap.containsKey(it.capability)
            }.forEach {
                it.ability = geneMap.getValue(it.capability).asSequence().shuffled(random).first()
                    .ability
            }
    }
}