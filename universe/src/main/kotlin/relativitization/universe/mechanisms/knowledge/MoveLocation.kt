package relativitization.universe.mechanisms.knowledge

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Movement
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.physics.Velocity
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

object MoveLocation : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val speedLimit: Double = mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .speedLimit

        if (speedLimit > 0.0) {
            val targetInt3D: Int3D = Int3D(
                (universeSettings.xDim - 1) / 2,
                (universeSettings.yDim - 1) / 2,
                (universeSettings.zDim - 1) / 2,
            )

            if (mutablePlayerData.int4D.toInt3D() != targetInt3D) {
                val velocity: Velocity = Movement.displacementToVelocity(
                    from = mutablePlayerData.double4D.toDouble3D(),
                    to = targetInt3D.toDouble3DCenter(),
                    speedOfLight = universeSettings.speedOfLight
                ).scaleVelocity(
                    mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().speedLimit
                )

                val deltaMass: Double = Relativistic.deltaMassByPhotonRocket(
                    mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().restMass,
                    mutablePlayerData.velocity.toVelocity(),
                    velocity,
                    universeSettings.speedOfLight,
                )

                mutablePlayerData.velocity = velocity.toMutableVelocity()
                mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                    .restMass -= deltaMass
            } else {
                val velocity = Velocity(0.0, 0.0, 0.0)

                val deltaMass: Double = Relativistic.deltaMassByPhotonRocket(
                    mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData().restMass,
                    mutablePlayerData.velocity.toVelocity(),
                    velocity,
                    universeSettings.speedOfLight,
                )
                mutablePlayerData.velocity = velocity.toMutableVelocity()
                mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
                    .restMass -= deltaMass
            }
        }

        return listOf()
    }
}