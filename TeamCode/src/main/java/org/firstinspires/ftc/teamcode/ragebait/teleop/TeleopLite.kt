package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.pedropathing.follower.Follower
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.auton.HybridLocalizer
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef
import org.firstinspires.ftc.teamcode.ragebait.utils.ButtonAction
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

@TeleOp(name = "TeleopLite", group = "Testing")
class TeleopLite : OpMode() {
    var robot: RobotHardwareLite = RobotHardwareLite()
    //var robot: RobotHardwareYousef = RobotHardwareYousef()

    var testMotorCogPower: Double = 2.0
    var testMotorOutPower: Double = 0.0

    //Camera Stuff
    //private lateinit var hybridLocalizer: HybridLocalizer
    var blueGoal : Pose = Pose(12.0, 132.0, 0.0)
    var redGoal : Pose = Pose(130.0, 132.0, 0.0)


    //Elapsed Time
    private val buttonElapsedTime = ElapsedTime()

    private val panelsTelemetry: TelemetryManager = PanelsTelemetry.telemetry

    private val buttonActions = arrayOf(
        //Increment Motor 2
        ButtonAction({ gamepad1.dpad_right || gamepad2.dpad_right }, {
            testMotorOutPower += 0.05
            testMotorOutPower = min(testMotorOutPower, 1.0)
            testMotorOutPower = max(testMotorOutPower, -1.0)
        }),
        ButtonAction({ gamepad1.dpad_left || gamepad2.dpad_left }, {
            testMotorOutPower -= 0.05
            testMotorOutPower = min(testMotorOutPower, 1.0)
            testMotorOutPower = max(testMotorOutPower, -1.0)
        }),
    )

    // Code to run once when the driver hits INIT
    override fun init() {
        robot.init(hardwareMap)
        panelsTelemetry.debug("Init was ran!")
        panelsTelemetry.update(telemetry)

        //val wheelConstants = ThreeWheelConstants()
        //hybridLocalizer = HybridLocalizer(hardwareMap, wheelConstants)

        //hybridLocalizer.setStartPose(Pose(0.0, 0.0, 0.0))
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    override fun init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    override fun start() {
        buttonElapsedTime.reset()
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    override fun loop() {
        ButtonAction.doActions(buttonActions)

//        hybridLocalizer.update()
//        val currentPose = hybridLocalizer.getPose()
//        panelsTelemetry.addData("Current Pose: ", currentPose)
//
//        val targetHeading : Double = 180 * atan2(blueGoal.x - currentPose.x, blueGoal.y - currentPose.y) / 3.1415926535

        testMotorCogPower = gamepad1.left_stick_x.toDouble() * 0.5
        robot.motorCog.power = testMotorCogPower
        panelsTelemetry.addData("Encoder: ", robot.motorCog.currentPosition)

        robot.motorOut.power = testMotorOutPower
        panelsTelemetry.addData("Current Motor 2 Speed: ", testMotorOutPower)

        panelsTelemetry.addData("Target Speed", testMotorOutPower * 2800)
        panelsTelemetry.addData("Current Speed F1", robot.motorOut.velocity)
        panelsTelemetry.update(telemetry)
    }

    // Code to run ONCE after the driver hits STOP
    override fun stop() {
    }
}