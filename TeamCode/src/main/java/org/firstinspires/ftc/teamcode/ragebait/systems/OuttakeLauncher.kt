package org.firstinspires.ftc.teamcode.ragebait.systems

import org.firstinspires.ftc.teamcode.ragebait.systems.core.DependencyCell
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem

@Suppress("unused")
class OuttakeLauncher() : SubSystem() {

    val localizer: PedroPathingLocalizer by DependencyCell(this)

    override fun init() {
    }

    override fun loop() {
    }

    override fun stop() {
    }

}