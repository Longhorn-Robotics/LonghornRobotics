package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.ragebait.systems.core.GamepadBinder
import org.firstinspires.ftc.teamcode.ragebait.systems.LegacyOuttakeLauncher
import org.firstinspires.ftc.teamcode.ragebait.systems.MecanumDrive
import org.firstinspires.ftc.teamcode.ragebait.systems.OuttakeLauncher
import org.firstinspires.ftc.teamcode.ragebait.systems.PedroPathingLocalizer
import org.firstinspires.ftc.teamcode.ragebait.systems.core.DependencyCell
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem

@TeleOp(name = "SubsystemTest", group = "Testing")
open class SubsystemTeleop : OpMode() {


    // TODO: Add control binding system

//    val bindings1 = GamepadBinder(gamepad1)

    // A     F
    // |\
    // B C E
    //  \|/
    //   D

//    class SysA(o: OpMode) : SubSystem(o) {
//        override fun init() {
//            opMode.telemetry.addLine("A Initialization")
//        }
//
//        override fun loop() = TODO("Not yet implemented")
//
//        override fun stop() = TODO("Not yet implemented")
//    }
//    class SysB(o: OpMode) : SubSystem(o) {
//        val A: SysA by DependencyCell(this)
//        override fun init() {
//            opMode.telemetry.addLine("B Initialization")
//            opMode.telemetry.addLine("Got A from B: $A")
//        }
//
//        override fun loop() = TODO("Not yet implemented")
//
//        override fun stop() = TODO("Not yet implemented")
//    }
//    class SysC(o: OpMode) : SubSystem(o) {
//        val A: SysA by DependencyCell(this)
//        override fun init() {
//            opMode.telemetry.addLine("C Initialization")
//            opMode.telemetry.addLine("Got A from C: $A")
//        }
//
//        override fun loop() = TODO("Not yet implemented")
//
//        override fun stop() = TODO("Not yet implemented")
//    }
//    class SysD(o: OpMode) : SubSystem(o) {
//        val A: SysA by DependencyCell(this)
//        val B: SysB by DependencyCell(this)
//        val C: SysC by DependencyCell(this)
//        val E: SysE by DependencyCell(this)
//        override fun init() {
//            opMode.telemetry.addLine("D Initialization")
//            opMode.telemetry.addLine("Got A from D: $A")
//            opMode.telemetry.addLine("Got B from D: $B")
//            opMode.telemetry.addLine("Got C from D: $C")
//            opMode.telemetry.addLine("Got E from D: $E")
//        }
//
//        override fun loop() = TODO("Not yet implemented")
//
//        override fun stop() = TODO("Not yet implemented")
//    }
//    class SysE(o: OpMode) : SubSystem(o) {
//        override fun init() {
//            opMode.telemetry.addLine("E Initialization")
//        }
//
//        override fun loop() = TODO("Not yet implemented")
//
//        override fun stop() = TODO("Not yet implemented")
//    }
//    class SysF(o: OpMode) : SubSystem(o) {
//        override fun init() {
//            opMode.telemetry.addLine("F Initialization")
//        }
//
//        override fun loop() = TODO("Not yet implemented")
//
//        override fun stop() = TODO("Not yet implemented")
//    }
//
//    //open
//    val subsystems = arrayOf<SubSystem>(
////        PedroPathingLocalizer(this),
////        OuttakeLauncher(this)
////        Sys
//    )
//
//    val a = SysA(this)
//    val b = SysB(this)
//    val c = SysC(this)
//    val d = SysD(this)
//    val e = SysE(this)
//    val f = SysF(this)

    val mecanum = MecanumDrive(this)
    val bindings1 = GamepadBinder(gamepad1)

    init {
        bindings1.bind_analog(GamepadBinder.Analog.left_stick_y, mecanum::throttle.setter)
        bindings1.bind_analog(GamepadBinder.Analog.left_stick_x, mecanum::strafe.setter)
        bindings1.bind_analog(GamepadBinder.Analog.right_stick_y, mecanum::yaw.setter)
    }

    override fun init() {
        SubSystem.doInitializations()
    }

    override fun loop() {
        SubSystem.doLoops()
        bindings1.loop()
    }

    override fun stop() {
        SubSystem.doStops()
    }
}
