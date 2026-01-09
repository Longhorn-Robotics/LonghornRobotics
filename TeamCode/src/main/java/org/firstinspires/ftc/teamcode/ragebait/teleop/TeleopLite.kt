package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite

@TeleOp(name = "TeleopLite", group = "Testing")
class TeleopLite : OpMode() {
      var robot = RobotHardwareLite();
//    var robot: RobotHardwareYousef = RobotHardwareYousef()

    var targetMotorPower: Double = 0.7
    val maxSpeed = 6000.0
    var targetMotorSpeed: Double = 0.0

    val tweakspeed = 0.0

    // Code to run once when the driver hits INIT
    override fun init() {
        robot.init(hardwareMap)
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    override fun init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    override fun start() {
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    override fun loop() {

        if (gamepad1.dpadUpWasPressed()) targetMotorPower += tweakspeed
        if (gamepad1.dpadDownWasPressed()) targetMotorPower -= tweakspeed

        telemetry.addData("Target Power", targetMotorPower)
        targetMotorSpeed = maxSpeed * targetMotorPower
        telemetry.addData("Target Speed", targetMotorSpeed)
    }

    // Code to run ONCE after the driver hits STOP
    override fun stop() {
    }
}