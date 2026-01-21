package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.auton.GetPoseFromCamera
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite
import org.firstinspires.ftc.teamcode.ragebait.utils.ButtonAction
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@TeleOp(name = "TeleopCameraProf", group = "Testing")
class TeleopCameraProf : OpMode() {
    var robot: RobotHardwareLite = RobotHardwareLite()
    var testMotor1Speed: Double = 0.0
    var testMotor2Speed: Double = 0.0

    var camPose : Pose? = null

    var currentTime : Double = 0.0
    var avgTime : Double = 0.0
    var squaredDifTime : Double = 0.0
    var standardDeviationTime : Double = 0.0
    var numLoops : Int = 0

    //Elapsed Time
    private val loopElapsedTime = ElapsedTime()
    private val lastCameraElapsedTime = ElapsedTime()


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
        loopElapsedTime.reset()
        lastCameraElapsedTime.reset()
        GetPoseFromCamera.initAprilTag(robot, telemetry)
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    override fun loop() {
        //Getting Cam
        camPose = GetPoseFromCamera.getPose("pedropathing")

        //Current time
        currentTime = loopElapsedTime.seconds()
        telemetry.addData("Time Elapsed Since Last Loop: ", currentTime)

        //Average Time
        numLoops++
        val prevAvgTime : Double = avgTime
        avgTime += (currentTime - avgTime) / numLoops
        telemetry.addData("Average Time Elapsed Since Last Loop: ", avgTime)

        //Standard Deviation for Time
        squaredDifTime += (currentTime - prevAvgTime) * (currentTime - avgTime)
        standardDeviationTime = sqrt(squaredDifTime / numLoops)
        telemetry.addData("SD For Average Time: ", standardDeviationTime)
        loopElapsedTime.reset()

        telemetry.update()
    }

    // Code to run ONCE after the driver hits STOP
    override fun stop() {
    }
}