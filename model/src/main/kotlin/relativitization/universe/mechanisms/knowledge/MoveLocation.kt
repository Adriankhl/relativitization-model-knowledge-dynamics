package relativitization.universe.mechanisms.knowledge

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.maths.physics.Int3D
import relativitization.universe.core.maths.physics.Movement
import relativitization.universe.core.maths.physics.Relativistic
import relativitization.universe.core.maths.physics.Velocity
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.data.components.abmKnowledgeDynamicsData
import kotlin.random.Random

object MoveLocation : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val switchLocationCoolDown: Int = universeSettings.getOtherIntOrDefault(
            "switchLocationCoolDown",
            Int.MAX_VALUE
        )

        val speedLimit: Double = mutablePlayerData.playerInternalData.abmKnowledgeDynamicsData()
            .speedLimit

        if (speedLimit > 0.0) {
            // Switch location per switchLocationCoolDown turn
            val location: Int = (mutablePlayerData.int4D.t / switchLocationCoolDown) % 2

            val targetInt3D: Int3D = if (location == 0) {
                Int3D(0, 0, 0)
            } else {
                Int3D(universeSettings.xDim, universeSettings.yDim, universeSettings.zDim)
            }

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