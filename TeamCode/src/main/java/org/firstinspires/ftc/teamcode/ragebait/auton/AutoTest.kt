//package org.firstinspires.ftc.teamcode.ragebait.auton
//
//import com.pedropathing.follower.Follower
//import com.pedropathing.geometry.Pose
//import com.pedropathing.paths.PathChain
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous
//import com.qualcomm.robotcore.util.ElapsedTime
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants
//import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef
//import org.firstinspires.ftc.vision.VisionPortal
//
//@Autonomous(name = "AutoTest", group = "Auto")
//class AutoTest : AutoLowBlue() {A
//    private val robot = RobotHardwareYousef()
//
//    override fun init() {
//        robot.init(hardwareMap)
//        GetPoseFromCamera.initAprilTag(robot, telemetry)
//    }
//    override fun loop() {
//        var currentPose: Pose? = GetPoseFromCamera.getPose()
//        telemetry.addData("Pose values: ", currentPose)
//        telemetry.update()
//    }
//}