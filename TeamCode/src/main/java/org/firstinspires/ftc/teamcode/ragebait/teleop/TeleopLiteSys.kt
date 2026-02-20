package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.ragebait.systems.core.ActionBuilder
import org.firstinspires.ftc.teamcode.ragebait.systems.core.GamepadBinder
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem
import org.firstinspires.ftc.teamcode.ragebait.systems.core.hardware.Motor

@Suppress("unused")
class MotorTest(motorNames: Array<String>) : SubSystem() {
    val motors: Array<Motor> by lazy { motorNames.map{Motor(it)}.toTypedArray() }
    var powers = DoubleArray(motorNames.size)

    override fun init() {
        motors.forEach { it.power = 0.0 }
        motors.forEach { it.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT }
    }

    override fun loop() {
        motors.withIndex().forEach { it.value.power = powers[it.index] }
    }

    override fun stop() {
        motors.forEach { it.power = 0.0 }
    }

    fun getPositions() = motors.map{ it.getCurrentPosition() }
    fun getSpeeds() = motors.map{ it.velocity }
}

@Suppress("unused")
@TeleOp(name = "LiteSys", group = "Testing")
class TeleopLiteSys : SubsystemTeleop() {
    val motorSys = MotorTest(arrayOf("motor1", "motor2"))

    init {
        bindings1?.bindButtonPress(GamepadBinder.Button.dpad_up, ActionBuilder.simple("Increase motor speed") { motorSys.powers[0] += 0.05 })
        bindings1?.bindButtonPress(GamepadBinder.Button.dpad_down, ActionBuilder.simple("Decrease motor speed") { motorSys.powers[0] -= 0.05 })
        bindings1?.bindButtonPress(GamepadBinder.Button.right_bumper, ActionBuilder.simple("Increase motor speed") { motorSys.powers[1] += 0.05 })
        bindings1?.bindButtonPress(GamepadBinder.Button.right_trigger, ActionBuilder.simple("Decrease motor speed") { motorSys.powers[1] -= 0.05 })
    }
}