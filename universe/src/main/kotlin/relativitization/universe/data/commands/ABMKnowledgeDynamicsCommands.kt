package relativitization.universe.data.commands

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.components.MutableCooperation
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.maths.physics.Int4D

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