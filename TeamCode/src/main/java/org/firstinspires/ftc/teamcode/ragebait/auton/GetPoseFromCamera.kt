package org.firstinspires.ftc.teamcode.ragebait.auton

import android.annotation.SuppressLint
import android.util.Size
import com.pedropathing.ftc.FTCCoordinates
import com.pedropathing.ftc.InvertedFTCCoordinates
import com.pedropathing.ftc.PoseConverter
import com.pedropathing.geometry.PedroCoordinates
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D
import org.firstinspires.ftc.robotcore.external.navigation.Position
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor
import java.lang.String

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite


object GetPoseFromCamera {
    const val USING_WEBCAM: Boolean = true
    private lateinit var camera: CameraName
    private val visionPortal: VisionPortal by lazy {
        //var robot = RobotHardwareYousef()
        var robot = RobotHardwareLite()
        // Create the vision portal by using a builder.
        val builder = VisionPortal.Builder()

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USING_WEBCAM) {
            builder.setCamera(robot.cam)
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK)
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        builder.setCameraResolution(Size(1280, 720))

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true)

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2)

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false)

        // Set and enable the processor.
        builder.addProcessor(aprilTag)

        // Set the camera used for building
        builder.setCamera(camera)

        // Build the Vision Portal, using the above settings.
        val tempPortal = builder.build() ?: throw Error();

        // Disable or re-enable the aprilTag processor at any time.
        tempPortal.setProcessorEnabled(aprilTag, true)

        tempPortal.resumeStreaming()

        tempPortal
    }
    private var aprilTag: AprilTagProcessor? = null
    val INTERNAL_CAM_DIR: BuiltinCameraDirection = BuiltinCameraDirection.BACK
    const val RESOLUTION_WIDTH: Int = 640
    const val RESOLUTION_HEIGHT: Int = 480

    // Internal state
    var lastX: Boolean = false
    var frameCount: Int = 0
    var capReqTime: Long = 0

    //Camera Values
    var fx: Double = 1415.979838;
    var fy: Double = 1411.104157;
    var cx: Double = 644.1777;
    var cy: Double = 357.2814359;

    private val cameraPosition: Position = Position(DistanceUnit.INCH, 0.0, 0.0, 0.0, 0)
    private val cameraOrientation = YawPitchRollAngles(AngleUnit.DEGREES, 0.0, 0.0, 0.0, 0)

    //Telemetry Solution
    lateinit var telemetry: Telemetry

    enum class PoseType {
        FTC,
        PEDROPATHING
    }

    @SuppressLint("DefaultLocale")
    fun getPose(poseType : PoseType): Pose? {
        // visionPortal.resumeStreaming()

        val currentDetections: List<AprilTagDetection> = aprilTag!!.detections
        telemetry.addData("# AprilTags Detected", currentDetections.size)

        var ftcPose: Pose? = null

        // TODO: Refactor this to final form and stuff
        // We get multiple detections; We will discard all mosaic detections, since we don't care
        // and then we prioritize the latest detection time; If they're all from the same time,
        // then we average out the positions.
        // Step through the list of detections and display info for each one.
        for (detection in currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(
                    String.format(
                        "\n==== (ID %d) %s",
                        detection.id,
                        detection.metadata.name
                    )
                )
                telemetry.addLine(
                    String.format(
                        "XYZ %6.1f %6.1f %6.1f  (inch)",
                        detection.robotPose.getPosition().x,
                        detection.robotPose.getPosition().y,
                        detection.robotPose.getPosition().z
                    )
                )
                telemetry.addLine(
                    String.format(
                        "PRY %6.1f %6.1f %6.1f  (deg)",
                        detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
                        detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
                        detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)
                    )
                )
                val pose2d: Pose2D = Pose2D(
                    DistanceUnit.INCH,
                    detection.ftcPose.x,
                    detection.ftcPose.y,
                    AngleUnit.RADIANS,
                    detection.ftcPose.yaw
                )
                ftcPose = PoseConverter.pose2DToPose(pose2d, InvertedFTCCoordinates.INSTANCE)
            }
            else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id))
                telemetry.addLine(
                    String.format(
                        "Center %6.0f %6.0f   (pixels)",
                        detection.center.x,
                        detection.center.y
                    )
                )
            }
        }

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.")
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)")

        // Return different-formatted pose based on argument passed into function
        return when (poseType) {
            PoseType.FTC -> ftcPose
            // Format ftcPose to PedroPathing coordinate system using PedroCoordinates.INSTANCE
            PoseType.PEDROPATHING -> ftcPose?.getAsCoordinateSystem(PedroCoordinates.INSTANCE)
        }
    }

    fun initAprilTag(hardwareCamera: CameraName, tel: Telemetry) {
        this.telemetry = tel

        camera = hardwareCamera

        val ourLib = AprilTagLibrary.Builder()
            .addTag(
                21, "Mosaic GPP",
                6.5, DistanceUnit.INCH
            )
            .addTag(
                22, "Mosaic PGP",
                6.4, DistanceUnit.INCH
            )
            .addTag(
                23, "Mosaic PPG",
                6.0, DistanceUnit.INCH
            )
            .addTag(
                20, "Blue",
                6.0, DistanceUnit.INCH
            )
            .addTag(
                24, "Red",
                6.0, DistanceUnit.INCH
            )
            .build()

        // Create the AprilTag processor.
        aprilTag =
            AprilTagProcessor.Builder() // The following default settings are available to un-comment and edit as needed.
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(ourLib)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES) // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.

                .setLensIntrinsics(fx, fy, cx, cy)
                .setCameraPose(cameraPosition, cameraOrientation)
                .build()

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag?.setDecimation(3f)

        // Disable or re-enable the aprilTag processor at any time.
        visionPortal.setProcessorEnabled(aprilTag, true)
    }
}