package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.auton.GetPoseFromCamera
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef
import org.firstinspires.ftc.teamcode.ragebait.utils.ButtonAction
import org.firstinspires.ftc.teamcode.ragebait.utils.PIDController
import kotlin.math.max
import kotlin.math.min

@TeleOp(name = "TeleopYousef", group = "Real OpMode")
class TeleopYousef : OpMode() {
    var robot: RobotHardwareYousef = RobotHardwareYousef()

    //Current Speeds
    var currentElevatorSpeed: Double = 0.7
    var currentIntakeSpeed: Double = 0.7

    //PID Gun Stuff
    var targetFlywheelPower: Double = 0.65
    var targetFlywheelSpeed: Double = 0.0
    var pidFlywheel1: PIDController = PIDController(-0.002, 0.0, -0.0002, {targetFlywheelPower})
    var pidFlywheel2: PIDController = PIDController(-0.0025, 0.0, -0.0002, {targetFlywheelPower})

    //Buttons & Servo Extenders
    var isKickerExtended: Boolean = false
    var isFlickerExtended: Boolean = false
    var joystickBaseSpeed: Double = 0.7 //0.3f;

    //OnOff INTAKE & OUTTAKE
    var intakeOn: Boolean = false
    var outtakeOn: Boolean = true
    var normalDirection: Boolean = true
    var slowMode: Boolean = false
    var directionMultiplier: Double = 1.0

    //Elapsed Time
    private val kickerElapsedTime = ElapsedTime()
    private val flickerElapsedTime = ElapsedTime()
    private val pidElapsedTime = ElapsedTime()

    private val buttonActions = arrayOf(
        //Increment Gun Motor
        ButtonAction({ gamepad1.dpad_up || gamepad2.dpad_up }, {
            targetFlywheelPower += 0.05
            targetFlywheelPower = min(targetFlywheelPower, 1.0)
            targetFlywheelPower = max(targetFlywheelPower, 0.0)
        }),
        ButtonAction({ gamepad1.dpad_down || gamepad2.dpad_down }, {
            targetFlywheelPower -= 0.05
            targetFlywheelPower = min(targetFlywheelPower, 1.0)
            targetFlywheelPower = max(targetFlywheelPower, 0.0)
        }),  //Increment Intake Motor

        ButtonAction({ gamepad2.right_bumper }, {
            currentIntakeSpeed += 0.05
            currentIntakeSpeed = min(currentIntakeSpeed, 1.0)
            currentIntakeSpeed = max(currentIntakeSpeed, 0.0)
        }),
        ButtonAction({ gamepad2.left_bumper }, {
            currentIntakeSpeed -= 0.05
            currentIntakeSpeed = min(currentIntakeSpeed, 1.0)
            currentIntakeSpeed = max(currentIntakeSpeed, 0.0)
        }),  //Increment Elevator Motor

        ButtonAction({ gamepad2.right_trigger > 0.5 }, {
            currentElevatorSpeed += 0.05
            currentElevatorSpeed = min(currentElevatorSpeed, 1.0)
            currentElevatorSpeed = max(currentElevatorSpeed, 0.0)
        }),
        ButtonAction({ gamepad2.left_trigger > 0.5 }, {
            currentElevatorSpeed -= 0.05
            currentElevatorSpeed = min(currentElevatorSpeed, 1.0)
            currentElevatorSpeed = max(currentElevatorSpeed, 0.0)
        }),
        // Kill All Intake
        ButtonAction(
            { gamepad1.circle || gamepad2.circle },
            { intakeOn = !intakeOn }),
        //Kill All Outtake Button
        ButtonAction(
            { gamepad1.triangle },
            { outtakeOn = !outtakeOn }),
        //Kicker
        ButtonAction({ gamepad1.cross || gamepad2.cross }, {
            isKickerExtended = !isKickerExtended
            kickerElapsedTime.reset()
        }),
        //Flicker
        ButtonAction({ gamepad1.square || gamepad2.square }, {
            isFlickerExtended = !isFlickerExtended
            flickerElapsedTime.reset()
        }),  //Reversing
        ButtonAction(
            { gamepad1.left_bumper },
            { normalDirection = !normalDirection }),  //Slowing down
        ButtonAction(
            { gamepad1.right_bumper },
            { slowMode = !slowMode }),
        )

    // Code to run once when the driver hits INIT
    override fun init() {
        robot.init(hardwareMap)
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    override fun init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    override fun start() {
        kickerElapsedTime.reset()
        flickerElapsedTime.reset()
        pidElapsedTime.reset()
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    override fun loop() {
        //Button Actions
        ButtonAction.doActions(buttonActions)

        intakeElevator()
        intakeFlicker()
        outtakeFlywheels()
        outtakeKicker()
        wheels()
        telemetry.update()
    }

    private fun intakeElevator() {
        //Intake Kill
        if (!intakeOn) {
            robot.motorElevator.power = 0.0
            robot.motorIn.power = 0.0
        } else {
            robot.motorElevator.power = -currentElevatorSpeed
            robot.motorIn.power = currentIntakeSpeed
        }
    }

    private fun outtakeKicker() {
        //KICKER
        if (kickerElapsedTime.seconds() > 0.5) {
            isKickerExtended = false
        }

        if (isKickerExtended) {
            robot.kicker.position = 0.0419
        } else {
            robot.kicker.position = 0.2358
        }
    }

    private fun intakeFlicker() {
        if (flickerElapsedTime.seconds() > 0.25) {
            isFlickerExtended = false
        }

        robot.flicker.position = if (isFlickerExtended) {
            0.35
        } else {
            0.17
        }

        telemetry.addData("Flicker", isFlickerExtended)
    }

    private fun wheels() {
        //movement code (tweak later)

        // double joystickMultiplier = joystickBaseSpeed + (1.0f - gamepad1.right_trigger);
        //REVERSE
        directionMultiplier = if (normalDirection) {
            1.0
        } else {
            -1.0
        }

        val slowSpeed = 0.4

        //SLOWING DOWN
        joystickBaseSpeed = if (slowMode) {
            slowSpeed
        } else {
            1.3
        }

        val finalThrottle = joystickBaseSpeed * gamepad1.left_stick_y * directionMultiplier + slowSpeed * gamepad2.left_stick_y
        val finalStrafe = joystickBaseSpeed * gamepad1.left_stick_x * directionMultiplier + slowSpeed * gamepad2.left_stick_x
        val finalYaw = joystickBaseSpeed * gamepad1.right_stick_x + 0.25 * gamepad2.right_stick_x

        robot.motorFL.power = finalThrottle - finalStrafe - finalYaw
        robot.motorBL.power = finalThrottle + finalStrafe - finalYaw
        robot.motorFR.power = finalThrottle + finalStrafe + finalYaw
        robot.motorBR.power = finalThrottle - finalStrafe + finalYaw
    }

    private fun outtakeFlywheels() {
        //Flywheel PID
        targetFlywheelSpeed = targetFlywheelPower * 2800

        val currentFlywheelSpeed1 = robot.motorOutR.velocity
        val currentFlywheelSpeed2 = robot.motorOutL.velocity

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

        //DATA
        telemetry.addData("Current Intake Speed: ", currentIntakeSpeed)
        telemetry.addData("Current Elevator Speed: ", currentElevatorSpeed)
        telemetry.addData("Current Flywheel 1 Speed: ", currentFlywheelSpeed1)
        telemetry.addData("Current Flywheel 2 Speed: ", currentFlywheelSpeed2)
        telemetry.addData("Target Flywheel Speed: ", targetFlywheelSpeed)
        telemetry.addData("Target Flywheel Power: ", targetFlywheelPower)
        telemetry.addData("Flywheel 1 PID: ", fly1pid)
        telemetry.addData("Flywheel 2 PID: ", fly2pid)

        telemetry.addData("Intake ON: ", intakeOn)
        telemetry.addData("Outtake ON: ", outtakeOn)

        //Outtake Kill
        if (!outtakeOn) {
            robot.motorOutR.power = 0.0
            robot.motorOutL.power = 0.0
        } else {
            robot.motorOutR.power = fly1pid
            robot.motorOutL.power = fly2pid
        }
    }

    // Code to run ONCE after the driver hits STOP
    override fun stop() {
    }
}