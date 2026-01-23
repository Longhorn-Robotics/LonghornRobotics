package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.ragebait.auton.GetPoseFromCamera
import kotlin.math.sqrt


// Pure camera: 0.0016, dev = 0.0063
// Encoder with nothing else: no change
// Reading encoder once: 0.0035 | 0.0137
// Reading encoder twice: 0.005 | 0.0125
// Reading encoder thrice: 0.008 | 0.0235
// Bulk reads (1): 0.004 | 0.009
// Array bulk reads: 0.004
// Hardcoded hub: 0.004 | 0.0011
// Future tests are hardcoded hub
// Bulk reads (2): 0.0042 | 0.0012
// Bulk reads (3): 0.0043 | 0.0035
//
// BIG MONEY


@TeleOp(name = "TeleopCameraProf", group = "Testing")
class TeleopCameraProf : OpMode() {

    lateinit var encoderTest : DcMotorEx
    var camPose : Pose? = null

    var currentTime : Double = 0.0
    var avgTime : Double = 0.0
    var squaredDifTime : Double = 0.0
    var standardDeviationTime : Double = 0.0
    var numLoops : Int = 0

    //Elapsed Time
    private val loopElapsedTime = ElapsedTime()
    private val lastCameraElapsedTime = ElapsedTime()

    val hub: LynxModule by lazy { hardwareMap.getAll(LynxModule::class.java)[0] }
    // Code to run once when the driver hits INIT
    override fun init() {
        GetPoseFromCamera.initAprilTag(hardwareMap.get(WebcamName::class.java, "Webcam 1"), telemetry)
        encoderTest = hardwareMap.get(DcMotorEx::class.java, "motor1")
        encoderTest.power = 0.0
        encoderTest.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        hub.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    override fun init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    override fun start() {
        loopElapsedTime.reset()
        lastCameraElapsedTime.reset()
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    override fun loop() {

        hub.clearBulkCache()

        telemetry.addData("Encoder value 1: ", encoderTest.currentPosition)
        telemetry.addData("Encoder value 2: ", encoderTest.currentPosition)
        telemetry.addData("Encoder value 3: ", encoderTest.currentPosition)

        //Getting Cam
        camPose = GetPoseFromCamera.getPose(GetPoseFromCamera.PoseType.PEDROPATHING)

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