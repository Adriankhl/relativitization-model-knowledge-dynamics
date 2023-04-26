package relativitization.universe.mechanisms

import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.mechanisms.MechanismLists
import relativitization.universe.mechanisms.knowledge.Innovation
import relativitization.universe.mechanisms.knowledge.MoveLocation
import relativitization.universe.mechanisms.knowledge.Production
import relativitization.universe.mechanisms.knowledge.ResetRestMass
import relativitization.universe.mechanisms.knowledge.ResetReward
import relativitization.universe.mechanisms.knowledge.SelectCooperator
import relativitization.universe.mechanisms.knowledge.SyncCooperator

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