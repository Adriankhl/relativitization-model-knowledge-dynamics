package relativitization.universe.knowledge.mechanisms

import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.mechanisms.MechanismLists
import relativitization.universe.knowledge.mechanisms.components.Innovation
import relativitization.universe.knowledge.mechanisms.components.MoveLocation
import relativitization.universe.knowledge.mechanisms.components.Production
import relativitization.universe.knowledge.mechanisms.components.ResetRestMass
import relativitization.universe.knowledge.mechanisms.components.ResetReward
import relativitization.universe.knowledge.mechanisms.components.SelectCooperator
import relativitization.universe.knowledge.mechanisms.components.SyncCooperator

object ABMKnowledgeDynamicsMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        SyncCooperator,
        ResetReward,
        ResetRestMass,
        MoveLocation,
    )

    override val dilatedMechanismList: List<Mechanism> = listOf(
        SelectCooperator,
        Production,
        Innovation,
    )
}

object ABMKnowledgeDynamicsTestMechanismLists : MechanismLists() {
    override val regularMechanismList: List<Mechanism> = listOf(
        SyncCooperator,
    )

    override val dilatedMechanismList: List<Mechanism> = listOf(
        SelectCooperator,
    )
}