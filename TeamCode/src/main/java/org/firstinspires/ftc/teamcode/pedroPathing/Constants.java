package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(10.07)
            .forwardZeroPowerAcceleration(-34.543025)
            .lateralZeroPowerAcceleration(-47.6598)
            .translationalPIDFCoefficients(
                    new PIDFCoefficients(
                            0.2,
                            0.0,
                            0.025,
                            0.03
                    ));

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("motorFR")
            .rightRearMotorName("motorBR")
            .leftRearMotorName("motorBL")
            .leftFrontMotorName("motorFL")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(60.77333)
            .yVelocity(50.62893);

    public static ThreeWheelConstants localizerConstants = new ThreeWheelConstants()
            .forwardTicksToInches(0.001995713)
            .strafeTicksToInches(0.00202160515453)
            .turnTicksToInches(0.00202342663333)
            .leftPodY(2.2)
            .rightPodY(-2.2)
            .strafePodX(7)
            .leftEncoder_HardwareMapName("motorFR")
            .rightEncoder_HardwareMapName("motorBL")
            .strafeEncoder_HardwareMapName("motorFL")
            .leftEncoderDirection(Encoder.FORWARD)
            .rightEncoderDirection(Encoder.FORWARD)
            .strafeEncoderDirection(Encoder.FORWARD);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .threeWheelLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
