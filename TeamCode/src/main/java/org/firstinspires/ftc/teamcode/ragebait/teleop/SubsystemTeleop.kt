package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.ragebait.systems.MecanumDrive
import org.firstinspires.ftc.teamcode.ragebait.systems.OuttakeLauncher
import org.firstinspires.ftc.teamcode.ragebait.systems.SubSystem
import org.firstinspires.ftc.teamcode.ragebait.utils.ButtonAction
import kotlin.math.max
import kotlin.math.min

@TeleOp(name = "SubsystemTest", group = "Testing")
class SubsystemTeleop : OpMode() {

    // TODO: Add control binding system

    val subsystems = mutableSetOf<SubSystem>()

    open val systems = setOf(
        MecanumDrive::class,
        OuttakeLauncher::class
    )

    override fun init() {
        subsystems.forEach { it.init() }
    }

    override fun loop() {
        subsystems.forEach { it.loop() }
    }

    override fun stop() {
        subsystems.forEach { it.stop() }
    }
}
