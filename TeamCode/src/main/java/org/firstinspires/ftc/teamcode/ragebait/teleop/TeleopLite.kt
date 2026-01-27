package org.firstinspires.ftc.teamcode.ragebait.teleop

import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite
import org.firstinspires.ftc.teamcode.ragebait.utils.ButtonAction
import kotlin.math.max
import kotlin.math.min

@TeleOp(name = "TeleopLite", group = "Testing")
class TeleopLite : OpMode() {
    //RobotHardwareLite robot = new RobotHardwareLite();
    var robot: RobotHardwareLite = RobotHardwareLite()
    var testMotor1Speed: Double = 2.0
    var testMotor2Power: Double = 0.0

    //Elapsed Time
    private val buttonElapsedTime = ElapsedTime()

    private val panelsTelemetry: TelemetryManager = PanelsTelemetry.telemetry

    private val buttonActions = arrayOf(
        //Increment Motor 2
        ButtonAction({ gamepad1.dpad_right || gamepad2.dpad_right }, {
            testMotor2Power += 0.05
            testMotor2Power = min(testMotor2Power, 1.0)
            testMotor2Power = max(testMotor2Power, -1.0)
        }),
        ButtonAction({ gamepad1.dpad_left || gamepad2.dpad_left }, {
            testMotor2Power -= 0.05
            testMotor2Power = min(testMotor2Power, 1.0)
            testMotor2Power = max(testMotor2Power, -1.0)
        }),
    )

    // Code to run once when the driver hits INIT
    override fun init() {
        robot.init(hardwareMap)
        panelsTelemetry.debug("Init was ran!")
        panelsTelemetry.update(telemetry)
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
//        testMotor1Speed = 0.3 * gamepad1.left_stick_x.toDouble()
//        robot.motor1Test.power = testMotor1Speed
        robot.motor2Test.power = testMotor2Power
//        panelsTelemetry.addData("Current Motor 1 Speed: ", testMotor1Speed)
        panelsTelemetry.addData("Current Motor 2 Speed: ", testMotor2Power)

        panelsTelemetry.addData("Target Speed", testMotor2Power * 2800)
        panelsTelemetry.addData("Current Speed F1", robot.motor2Test.velocity)
        panelsTelemetry.update(telemetry)
    }

    // Code to run ONCE after the driver hits STOP
    override fun stop() {
    }
}