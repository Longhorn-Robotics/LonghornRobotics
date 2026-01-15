package org.firstinspires.ftc.teamcode.ragebait.systems

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem

class MecanumDrive(
    opMode: OpMode,
    val normalSpeed: Double = 1.3,
    val slowSpeed: Double = 0.4,
    val motorFRHWMapName: String = "motorFR",
    val motorFLHWMapName: String = "motorFL",
    val motorBRHWMapName: String = "motorBR",
    val motorBLHWMapName: String = "motorBL",
) : SubSystem(opMode) {

    private fun initMotor(name: String): DcMotorEx {
        val motor = opMode.hardwareMap.get<DcMotorEx>(DcMotorEx::class.java, name)
        motor.power = 0.0
        motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        return motor
    }

    val motorFR: DcMotorEx by lazy { initMotor(motorFRHWMapName) }
    val motorFL: DcMotorEx by lazy { initMotor(motorFLHWMapName) }
    val motorBR: DcMotorEx by lazy { initMotor(motorBRHWMapName) }
    val motorBL: DcMotorEx by lazy { initMotor(motorBLHWMapName) }

    var inverseControls = false

    var slowMode = false

    override fun init() {
        // Also makes sure the lazy init happens
        motorFR.direction = DcMotorSimple.Direction.REVERSE
        motorFL.direction = DcMotorSimple.Direction.FORWARD
        motorBR.direction = DcMotorSimple.Direction.REVERSE
        motorBL.direction = DcMotorSimple.Direction.FORWARD
    }

    // This brings up an interesting design question in the subsystems; Some should distance themselves
    // as much as possible from the OpMode, like the outtake launcher, basically only using the hardware
    // because they should work in auto and are expected to handled interactively by the OpMode, whether
    // autonomously or through teleop with the gamepad.
    // Some, however, like a Mecanum drivetrain subsystem built *only for* a the teleop use case, handling
    // the gamepad controls from within the subsystem is exactly the level of abstraction/self-sufficiency
    // expected of the system.

    override fun loop() {
        val direction = if (inverseControls) -1.0 else 1.0

        val baseSpeed = if (slowMode) slowSpeed else normalSpeed

        val endMultiplier = baseSpeed * direction

        val finalThrottle =
            endMultiplier * opMode.gamepad1.left_stick_y + slowSpeed * opMode.gamepad2.left_stick_y
        val finalStrafe =
            endMultiplier * opMode.gamepad1.left_stick_x + slowSpeed * opMode.gamepad2.left_stick_x
        val finalYaw =
            baseSpeed * opMode.gamepad1.right_stick_x + 0.25 * opMode.gamepad2.right_stick_x

        motorFL.power = finalThrottle - finalStrafe - finalYaw
        motorBL.power = finalThrottle + finalStrafe - finalYaw
        motorFR.power = finalThrottle + finalStrafe + finalYaw
        motorBR.power = finalThrottle - finalStrafe + finalYaw
    }

    override fun stop() {}
}