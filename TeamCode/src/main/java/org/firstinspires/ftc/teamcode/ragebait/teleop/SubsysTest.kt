package org.firstinspires.ftc.teamcode.ragebait.teleop

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.ragebait.systems.core.ActionBuilder
import org.firstinspires.ftc.teamcode.ragebait.systems.core.DependencyCell
import org.firstinspires.ftc.teamcode.ragebait.systems.core.GamepadBinder
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem

@Suppress("unused")
@TeleOp(name = "TestOp", group = "pushbot")
class SubsysTest : SubsystemTeleop() {
    class SysA : SubSystem() {
        override fun init() {
            opMode.telemetry.addLine("Initializing sysA")
        }

        var crash = false
        override fun loop() {
            opMode.telemetry.addLine("A looping")
            if (crash) {
                opMode.telemetry.addLine("")
                throw Exception("A crashed")
            }
        }

        override fun stop() {
        }
    }

    class SysB : SubSystem() {
        val aRef: SysA by DependencyCell(this)
        override fun init() {
            opMode.telemetry.addLine("Initializing sysB")
            opMode.telemetry.addLine("Got A: $aRef")
        }

        var crash = false
        override fun loop() {
            opMode.telemetry.addLine("B looping")
            if (crash) {
                opMode.telemetry.addLine("")
                throw Exception("B crashed")
            }
        }

        override fun stop() {
        }
    }

    class SysC : SubSystem() {
        val aRef: SysA by DependencyCell(this)
        override fun init() {
            opMode.telemetry.addLine("Initializing sysC")
            opMode.telemetry.addLine("Got A: $aRef")
        }

        var crash = false
        override fun loop() {
            opMode.telemetry.addLine("C looping")
            if (crash) {
                opMode.telemetry.addLine("")
                throw Exception("C crashed")
            }
        }

        override fun stop() {
        }
    }

    class SysD : SubSystem() {
        val cRef: SysC by DependencyCell(this)
        override fun init() {
            opMode.telemetry.addLine("Initializing sysD")
            opMode.telemetry.addLine("Got C: $cRef")
        }

        var crash = false
        override fun loop() {
            opMode.telemetry.addLine("D looping")
            if (crash) {
                opMode.telemetry.addLine("")
                throw Exception("D crashed")
            }
        }

        override fun stop() {
        }
    }

    val a = SysA()
    val b = SysB()
    val c = SysC()
    val d = SysD()

    init {
        bindings1.bindButtonPress(GamepadBinder.Button.cross, ActionBuilder.simple("Crash A") {
            a.crash = true
        })
        bindings1.bindButtonPress(GamepadBinder.Button.square, ActionBuilder.simple("Crash B") {
            b.crash = true
        })
        bindings1.bindButtonPress(GamepadBinder.Button.triangle, ActionBuilder.simple("Crash C") {
            c.crash = true
        })
        bindings1.bindButtonPress(GamepadBinder.Button.circle, ActionBuilder.simple("Crash D") {
            d.crash = true
        })
        Log.v("HornLib", "Initialized and stuff")
    }
}