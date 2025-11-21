package org.firstinspires.ftc.teamcode.ragebait.teleop


import com.bylazar.telemetry.PanelsTelemetry
import com.bylazar.telemetry.TelemetryManager
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef
import org.firstinspires.ftc.teamcode.ragebait.utils.PIDController
import kotlin.math.max
import kotlin.math.min

@Configurable
@TeleOp(name = "Outake PID Tuner", group = "Tuning")
class OutakePIDTuner : OpMode() {
    companion object{
        @JvmField var pidFlywheel1: PIDController = PIDController(-0.001, 0.0, 0.0)
        @JvmField var pidFlywheel2: PIDController = PIDController(-0.001, 0.0, 0.0)
    }
    private val panelsTelemetry: TelemetryManager = PanelsTelemetry.telemetry

    val robot: RobotHardwareYousef = RobotHardwareYousef()

    val timer = ElapsedTime()


    //PID Gun Stuff
    @JvmField var currentFlywheelSpeed1: Double = 0.0
    @JvmField var currentFlywheelSpeed2: Double = 0.0
    @JvmField var targetFlywheelPower: Double = 0.7
    @JvmField var targetFlywheelSpeed: Double = 0.0

    private val pidElapsedTime = ElapsedTime()
    private val buttonElapsedTime = ElapsedTime()

    // Kicker
    var isKickerExtended: Boolean = false
    var x_pressed_gmpd1: Boolean = false
    var isGunAdd: Boolean = false
    var isGunSubtract: Boolean = false

    override fun init() {
        panelsTelemetry.debug("Init was ran!")
        panelsTelemetry.update(telemetry)
    }

    override fun loop() {
//        val t = timer.seconds()
        //Gun Motor
        if (gamepad1.dpad_up && !isGunAdd) {
            targetFlywheelPower += 0.05
            isGunAdd = true
        } else if (!gamepad1.dpad_up) {
            isGunAdd = false
        }

        if (gamepad1.dpad_down && !isGunSubtract) {
            targetFlywheelPower -= 0.05
            isGunSubtract = true
        } else if (!gamepad1.dpad_down) {
            isGunSubtract = false
        }

        targetFlywheelPower = min(targetFlywheelPower, 1.0)
        targetFlywheelPower = max(targetFlywheelPower, 0.0)

        targetFlywheelSpeed = targetFlywheelPower * 2800

        currentFlywheelSpeed1 = robot.motorOutR.velocity
        currentFlywheelSpeed2 = robot.motorOutL.velocity

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

        //KICKER
        if(gamepad1.cross && !x_pressed_gmpd1)
        {
            isKickerExtended = !isKickerExtended;
            buttonElapsedTime.reset();
        } else if (buttonElapsedTime.seconds() > 0.5) {
            isKickerExtended = false;
        }
        x_pressed_gmpd1 = gamepad1.cross;

        if(isKickerExtended)
        {
            robot.kicker.setPosition(0.07); //0.55
        }
        else if(!isKickerExtended)
        {
            robot.kicker.setPosition(0.247); //0.73
        }

        panelsTelemetry.debug("Target Speed: $targetFlywheelSpeed")
        panelsTelemetry.debug("Current Speed F1: $currentFlywheelSpeed1")
        panelsTelemetry.debug("Current Speed F2: $currentFlywheelSpeed2")
        panelsTelemetry.debug("P1: ${pidFlywheel1.Kp}")
        panelsTelemetry.debug("I1: ${pidFlywheel1.Ki}")
        panelsTelemetry.debug("D1: ${pidFlywheel1.Kd}")
        panelsTelemetry.debug("P2: ${pidFlywheel2.Kp}")
        panelsTelemetry.debug("I2: ${pidFlywheel2.Ki}")
        panelsTelemetry.debug("D2: ${pidFlywheel2.Kd}")
        panelsTelemetry.addData("Target Speed", targetFlywheelSpeed)
        panelsTelemetry.addData("Current Speed F1", currentFlywheelSpeed1)
        panelsTelemetry.addData("Current Speed F2", currentFlywheelSpeed2)

//        panelsTelemetry.debug("LoopTime: ${timer.ms}ms / ${timer.hz}Hz")

        panelsTelemetry.update(telemetry)
//        timer.end()
    }
}
