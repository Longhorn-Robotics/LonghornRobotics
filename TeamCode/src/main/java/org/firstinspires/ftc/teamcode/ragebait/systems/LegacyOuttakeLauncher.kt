package org.firstinspires.ftc.teamcode.ragebait.systems

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem
import org.firstinspires.ftc.teamcode.ragebait.utils.PIDController

class LegacyOuttakeLauncher(opMode: OpMode) : SubSystem(opMode) {
    var targetFlywheelPower: Double = 0.65
    var targetFlywheelSpeed: Double
        get() = if (outtakeOn) targetFlywheelPower * 2800 else 0.0
        set(value) {
            targetFlywheelPower = value / 2800.0
        }
    private val pidFlywheel1: PIDController =
        PIDController(-0.002, 0.0, -0.0002, { targetFlywheelPower })
    private val pidFlywheel2: PIDController =
        PIDController(-0.0025, 0.0, -0.0002, { targetFlywheelPower })

    private val pidElapsedTime = ElapsedTime()

    var outtakeOn: Boolean = false

    private fun initMotor(name: String): DcMotorEx {
        val motor = opMode.hardwareMap.get<DcMotorEx>(DcMotorEx::class.java, name)
        motor.power = 0.0
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        return motor
    }

    val motorOutR by lazy { initMotor("motorOutR") }
    val motorOutL by lazy { initMotor("motorOutL") }

    override fun init() {
        motorOutR.direction = DcMotorSimple.Direction.REVERSE
        motorOutL.direction = DcMotorSimple.Direction.FORWARD
    }

    override fun loop() {
        val currentFlywheelSpeed1 = motorOutR.velocity
        val currentFlywheelSpeed2 = motorOutL.velocity

        val fly1pid = pidFlywheel1.update(
            targetFlywheelSpeed,
            currentFlywheelSpeed1,
            pidElapsedTime.seconds()
        )
        val fly2pid = pidFlywheel2.update(
            targetFlywheelSpeed,
            currentFlywheelSpeed2,
            pidElapsedTime.seconds()
        )
        pidElapsedTime.reset()

        //Outtake Kill
        if (!outtakeOn) {
            motorOutR.power = 0.0
            motorOutL.power = 0.0
        } else {
            motorOutR.power = fly1pid
            motorOutL.power = fly2pid
        }
    }

    override fun stop() {}
}