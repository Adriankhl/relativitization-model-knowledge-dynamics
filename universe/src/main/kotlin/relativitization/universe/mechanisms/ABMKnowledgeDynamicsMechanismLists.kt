package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.knowledge.Innovation
import relativitization.universe.mechanisms.knowledge.Production
import relativitization.universe.mechanisms.knowledge.SelectCooperator

object ABMKnowledgeDynamicsMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf()

    override val dilatedMechanismList: List<Mechanism> = listOf(
        SelectCooperator,
        Production,
        Innovation,
    )
}