package relativitization.universe.knowledge

import ksergen.GeneratedModule
import relativitization.universe.core.RelativitizationInitializer
import relativitization.universe.knowledge.generate.ABMKnowledgeDynamicsGenerate
import relativitization.universe.knowledge.mechanisms.ABMKnowledgeDynamicsMechanismLists
import relativitization.universe.knowledge.mechanisms.ABMKnowledgeDynamicsTestMechanismLists

object KnowledgeDynamicsInitializer {
    fun initialize() {
        RelativitizationInitializer.initialize(
            serializersModule = GeneratedModule.serializersModule,
            generateUniverseMethod = ABMKnowledgeDynamicsGenerate,
        )

        RelativitizationInitializer.initialize(
            mechanismListsList = listOf(
                ABMKnowledgeDynamicsMechanismLists,
                ABMKnowledgeDynamicsTestMechanismLists,
            )
        )
    }
}