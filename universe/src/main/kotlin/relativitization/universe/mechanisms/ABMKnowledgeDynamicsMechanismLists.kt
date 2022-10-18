package relativitization.universe.mechanisms

import relativitization.universe.mechanisms.knowledge.Innovation
import relativitization.universe.mechanisms.knowledge.Production
import relativitization.universe.mechanisms.knowledge.SelectCooperator
import relativitization.universe.mechanisms.knowledge.SyncCooperator

object ABMKnowledgeDynamicsMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        SyncCooperator,
    )

    override val dilatedMechanismList: List<Mechanism> = listOf(
        SelectCooperator,
        Production,
        Innovation,
    )
}