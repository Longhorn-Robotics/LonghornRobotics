package org.firstinspires.ftc.teamcode.ragebait.auton

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous

@Autonomous(name = "AutoLowRed", group = "Auto")
class AutoLowRed : AutoLowBlue() {
    override val startPose  = Pose(90.557, 9.5, Math.toRadians(90.0))
    override val scorePose = Pose(82.577, 15.216, Math.toRadians(62.0))
    override val endPose = Pose(105.958, 10.76, Math.toRadians(90.0))
}