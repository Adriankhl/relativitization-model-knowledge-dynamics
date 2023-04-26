package relativitization.universe.knowledge.data.commands

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.maths.physics.Int4D
import relativitization.universe.knowledge.data.components.MutableCooperation
import relativitization.universe.knowledge.data.components.abmKnowledgeDynamicsData

data class AskCooperationCommand(
    override val toId: Int
) : Command() {
    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.abmKnowledgeDynamicsData().cooperationInMap[fromId] =
            MutableCooperation(
                time = 0,
            )
    }
}

data class EndCooperationCommand(
    override val toId: Int
) : Command() {
    override fun execute(
        playerData: MutablePlayerData,
        fromId: Int,
        fromInt4D: Int4D,
        universeSettings: UniverseSettings
    ) {
        playerData.playerInternalData.abmKnowledgeDynamicsData().cooperationInMap.remove(fromId)
    }
}
