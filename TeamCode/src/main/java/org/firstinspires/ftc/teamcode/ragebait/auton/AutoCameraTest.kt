package org.firstinspires.ftc.teamcode.ragebait.auton

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef

@Autonomous(name = "AutoCameraTest", group = "Auto")
open class AutoCameraTest : OpMode() {
    override fun init() {

    }
    override fun loop() {
        GetPoseFromCamera.initAprilTag()
        telemetry.addData("Camera Pose:", GetPoseFromCamera.getPose())
        //telemetry.update()
    }

}