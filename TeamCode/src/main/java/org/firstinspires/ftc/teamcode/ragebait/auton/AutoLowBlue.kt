package org.firstinspires.ftc.teamcode.ragebait.auton

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef
import org.firstinspires.ftc.teamcode.ragebait.utils.PIDController


@Autonomous(name = "AutoLowBlue", group = "Auto")
open class AutoLowBlue : OpMode() {

    private val robot = RobotHardwareYousef()
    private val follower: Follower by lazy { Constants.createFollower(hardwareMap) }

    private var pathState = 0

    private val starttoScore: PathChain by lazy { PathBuilder.constructPath(follower, startPose, scorePose) }
    private val scoretoEnd: PathChain by lazy { PathBuilder.constructPath(follower, scorePose, endPose) }

    open val startPose = Pose(56.0, 9.0, Math.toRadians(90.0))
    open val scorePose = Pose(60.984, 13.732, Math.toRadians(118.0))
    open val endPose = Pose(38.598, 10.000, Math.toRadians(90.0))

    val pathTimer = ElapsedTime();
    val opmodeTimer = ElapsedTime();
    val pidElapsedTime = ElapsedTime();
    val scoringTimer = ElapsedTime();
    val kickerElapsedTime = ElapsedTime()


    override fun init() {
        robot.init(hardwareMap)
        pathState = 0
        targetFlywheelPower = 0.0

        follower.setStartingPose(startPose)
        follower.followPath(starttoScore)

        pathTimer.reset()
        opmodeTimer.reset()
    }

    override fun loop() {
        // These loop the movements of the robot, these must be called continuously in order to work
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.pose.x);
        telemetry.addData("y", follower.pose.y);
        telemetry.addData("heading", follower.pose.heading);
        telemetry.update();

    }

    private fun autonomousPathUpdate() {
        when (pathState) {
            // Start to score
            0 -> {
                if (!(follower.isBusy)) {
                    pathState = 1
                    startScoreLaunching()
                    scoringTimer.reset()
                    kickerElapsedTime.reset()
                }
            }
            // Shoot out balls x2-3 and score
            1 -> {
                if (!follower.isBusy) {
                    if (scoringTimer.seconds() > 10.0) {
                        pathState = 2
                        endScoreLaunching()
                    }
                }
                outtakeFlywheels()

                kickBall()
                flickBall()
            }
            // Run to base
            2 -> {
                robot.motorOutR.power = 0.0
                robot.motorOutL.power = 0.0
                if (!follower.isBusy) {
                    follower.followPath(scoretoEnd)
                    pathState = 3
                }
            }
        }
    }

    private fun startScoreLaunching() {
        scoringTimer.reset()
        targetFlywheelPower = 0.7
    }

    private fun endScoreLaunching() {
        targetFlywheelPower = 0.0
    }

    private fun kickBall() {
        if (kickerElapsedTime.seconds() > 0.5) {
            robot.kicker.position = robot.kickerInPosition
        }
        else
        {
            robot.kicker.position = robot.kickerOutPosition
        }

        if(kickerElapsedTime.seconds() > 1)
        {
            kickerElapsedTime.reset()
        }
    }

    private fun flickBall() {
        if (kickerElapsedTime.seconds() > 0.8) {
            robot.flicker.position = robot.flickerInPosition
        }
        else if (kickerElapsedTime.seconds() > 0.5) {
            robot.flicker.position = robot.flickerOutPosition
        }
    }

    var targetFlywheelPower = 0.6
    val pidFlywheel1: PIDController = PIDController(-0.002, 0.0, -0.0002, {targetFlywheelPower})
    val pidFlywheel2: PIDController = PIDController(-0.0025, 0.0, -0.0002, {targetFlywheelPower})

    private fun outtakeFlywheels() {
        //Flywheel PID
        val targetFlywheelSpeed = targetFlywheelPower * 2800

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

        robot.motorOutR.power = fly1pid
        robot.motorOutL.power = fly2pid
    }
}