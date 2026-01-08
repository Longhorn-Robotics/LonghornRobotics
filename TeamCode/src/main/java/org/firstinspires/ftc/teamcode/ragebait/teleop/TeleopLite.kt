package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite
import org.firstinspires.ftc.teamcode.ragebait.utils.ButtonAction
import kotlin.math.max
import kotlin.math.min

@TeleOp(name = "TeleopLite", group = "Testing")
class TeleopLite : OpMode() {
    //RobotHardwareLite robot = new RobotHardwareLite();
    var robot: RobotHardwareLite = RobotHardwareLite()
    var testMotor1Speed: Double = 0.0
    var testMotor2Speed: Double = 0.0

    //Elapsed Time
    private val buttonElapsedTime = ElapsedTime()

    private val buttonActions = arrayOf(
        //Increment Motor 2
        ButtonAction({ gamepad1.dpad_right || gamepad2.dpad_right }, {
            testMotor2Speed += 0.05
            testMotor2Speed = min(testMotor2Speed, 1.0)
            testMotor2Speed = max(testMotor2Speed, -1.0)
        }),
        ButtonAction({ gamepad1.dpad_left || gamepad2.dpad_left }, {
            testMotor2Speed -= 0.05
            testMotor2Speed = min(testMotor2Speed, 1.0)
            testMotor2Speed = max(testMotor2Speed, -1.0)
        }),
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
        buttonElapsedTime.reset()
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    override fun loop() {
        ButtonAction.doActions(buttonActions)
        testMotor1Speed = 0.1 * gamepad1.left_stick_x.toDouble()
        robot.motor1Test.power = testMotor1Speed
        robot.motor2Test.power = testMotor2Speed
        telemetry.addData("Current Motor 1 Speed: ", testMotor1Speed)
        telemetry.addData("Current Motor 2 Speed: ", testMotor2Speed)
        telemetry.update()
    }

    // Code to run ONCE after the driver hits STOP
    override fun stop() {
    }
}