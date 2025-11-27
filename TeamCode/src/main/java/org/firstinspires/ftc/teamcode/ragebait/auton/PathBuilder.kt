package org.firstinspires.ftc.teamcode.ragebait.auton

import androidx.annotation.NonNull
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain

object PathBuilder {
//    enum class SetPositions(pose: Pose) {
//        BLUE_START_POSE(Pose(56.0, 9.0, Math.toRadians(90.0))),
//        BLUE_SCORE_POSE(Pose(60.984, 13.732, Math.toRadians(118.0))),
//        BLUE_END_POSE(Pose(38.598, 10.000, Math.toRadians(90.0)))
//    }

    fun constructPath(follower: Follower, start: Pose, end: Pose): PathChain = follower.pathBuilder()
        .addPath(
            BezierCurve(
                start,
                end
            )
        )
        .setLinearHeadingInterpolation(start.heading, end.heading)
        .build()
}