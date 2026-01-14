package org.firstinspires.ftc.teamcode.ragebait.auton
/**
 * CONCEPTS:
 *  - HYBRID: uses both odometry wheels (local, smaller movement and positioning) AND camera
 *  - ALGORITHM:
 *      - Provide dynamic weighting to either odometry or camera depending on input
 *          - Weight odometry greater when last input small
 *          - Weight camera greater when last input larger
 *          - This is because odometry is more accurate on smaller scale and camera is more accurate on larger scale
 *      - Get and return actual pose from an weighted average formula between the two
 * **/
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants
import com.pedropathing.ftc.localization.localizers.ThreeWheelLocalizer
import com.pedropathing.geometry.Pose
import com.pedropathing.localization.Localizer
import com.pedropathing.math.Vector
import com.qualcomm.robotcore.hardware.HardwareMap

class HybridLocalizer(val hwmap: HardwareMap, val wheelConstants: ThreeWheelConstants) : Localizer {

    private val threeWheel = ThreeWheelLocalizer(hwmap, wheelConstants)
    private var displacementPose = Pose()
    private val weight = 0.05
    private var startPose = Pose()
    private var camPose : Pose? = null
    private var odoPose : Pose? = null
    override fun getPose(): Pose {
       camPose = GetPoseFromCamera.getPose("pedropathing")
       odoPose = threeWheel.pose
       val newPoseX: Double = odoPose?.x?.times(1 - weight)?.plus(camPose!!.x.times(weight)) ?:
       val newPoseY: Double = odoPose?.y?.times(1 - weight)?.plus(camPose?.y?.times(weight) ?: ) ?:

       return Pose(newPoseX, newPoseY, odoPose.heading)
    }


    override fun getVelocity(): Pose? = threeWheel.velocity

    override fun getVelocityVector(): Vector? = threeWheel.velocityVector

    override fun setStartPose(setStart: Pose) = threeWheel.setStartPose(setStart)

    override fun setPose(setPose: Pose?) = threeWheel.setPose(setPose)

    override fun update() {
        threeWheel.update()
        // TODO: Every once in a while*
        threeWheel.pose = cameraPose
    }

    override fun getTotalHeading(): Double = threeWheel.totalHeading

    override fun getForwardMultiplier(): Double = threeWheel.forwardMultiplier

    override fun getLateralMultiplier(): Double = threeWheel.lateralMultiplier

    override fun getTurningMultiplier(): Double = threeWheel.turningMultiplier

    override fun resetIMU() = threeWheel.resetIMU()

    override fun getIMUHeading(): Double = threeWheel.imuHeading

    override fun isNAN(): Boolean = threeWheel.isNAN

}