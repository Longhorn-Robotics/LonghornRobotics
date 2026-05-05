package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.ragebait.systems.core.Action
import org.firstinspires.ftc.teamcode.ragebait.systems.core.GamepadBinder
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem

open class SubsystemOpmode : OpMode() {
    val bindings1 = GamepadBinder{gamepad1}
    val bindings2 = GamepadBinder{gamepad2}

    val hubs: Array<LynxModule> by lazy { hardwareMap.getAll(LynxModule::class.java).toTypedArray() }

    init {
        Action.clear()
        // Must be done before any systems are even constructed
        SubSystem.clear()
        SubSystem.defaultOpMode = this
    }

    final override fun init() {
        hubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
        SubSystem.doInitializations()
        gamepad1 ?: telemetry.addLine("WARNING: No gamepad 1")
        gamepad2 ?: telemetry.addLine("WARNING: No gamepad 2")
    }

    final override fun loop() {
        hubs.forEach { it.clearBulkCache() }
        bindings1.loop()
        bindings2.loop()
        Action.doUpdates()
        SubSystem.doLoops()
    }

    final override fun stop() {
        SubSystem.doStops()
    }
}
