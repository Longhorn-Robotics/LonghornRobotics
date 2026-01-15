package org.firstinspires.ftc.teamcode.ragebait.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ragebait.auton.GetPoseFromCamera;
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite;
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef;
import org.firstinspires.ftc.teamcode.ragebait.utils.PIDController;

@TeleOp(name = "TeleopLite", group = "Testing")
public class TeleopLite extends OpMode {
//    RobotHardwareLite robot = new RobotHardwareLite();

    RobotHardwareYousef robot = new RobotHardwareYousef();

    //Current Speeds
    double currentElevatorSpeed = 0.7;
    double currentIntakeSpeed = 0.7;

    //PID Gun Stuff
    double currentFlywheelSpeed1;
    double currentFlywheelSpeed2;
    double targetFlywheelPower = 0.7;
    double targetFlywheelSpeed;
    PIDController pidFlywheel1 = new PIDController(-0.001, 0.0, 0.0, () -> 0.0);
    PIDController pidFlywheel2 = new PIDController(-0.001, 0.0, 0.0, () -> 0.0 );

    //Add & Subtract For testing
    boolean isIntakeAdd = false;
    boolean isIntakeSubtract = false;
    boolean isElevatorAdd = false;
    boolean isElevatorSubtract = false;
    boolean isGunAdd = false;
    boolean isGunSubtract = false;

    //Buttons & Servo Extendeders
    boolean isKickerExtended = false;
    boolean x_pressed = false;
    boolean isFlickerExtended = false;
    boolean square_pressed = false;

    //Elapsed Time
    private ElapsedTime buttonElapsedTime = new ElapsedTime();
    private ElapsedTime pidElapsedTime = new ElapsedTime();

    double flickerGoon = 0.0;
    double tweakSpeed = 0.001;

    // Code to run once when the driver hits INIT
    @Override
    public void init() {
        robot.init(hardwareMap);
    }

    // Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
    @Override
    public void init_loop() {
    }

    // Code to run ONCE when the driver hits PLAY
    @Override
    public void start() {
        buttonElapsedTime.reset();
        pidElapsedTime.reset();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {

        robot.kicker.setPosition(flickerGoon);
        flickerGoon += gamepad1.left_stick_y * tweakSpeed;
        tweakSpeed += gamepad1.right_stick_y * 0.0001;
        // 0.1381
        // 0.2917
        telemetry.addData("Kick Position", flickerGoon);
        telemetry.addData("Tweakspeed", tweakSpeed);


//        //Gun Motor
//        if(gamepad1.dpad_up && !isGunAdd)
//        {
//            targetFlywheelPower += 0.05;
//            isGunAdd = true;
//        }
//        else if(!gamepad1.dpad_up)
//        {
//            isGunAdd = false;
//        }
//
//        if(gamepad1.dpad_down && !isGunSubtract)
//        {
//            targetFlywheelPower -= 0.05;
//            isGunSubtract = true;
//        }
//        else if(!gamepad1.dpad_down)
//        {
//            isGunSubtract = false;
//        }
//        targetFlywheelPower = Math.min(targetFlywheelPower, 1);
//        targetFlywheelPower = Math.max(targetFlywheelPower, 0);
//
//        targetFlywheelSpeed = targetFlywheelPower * 2800;
//
//        currentFlywheelSpeed1 = robot.motorOutR.getVelocity();
//        currentFlywheelSpeed2 = robot.motorOutL.getVelocity();
//
//        double fly1pid = pidFlywheel1.update(targetFlywheelSpeed, currentFlywheelSpeed1, pidElapsedTime.seconds());
//        double fly2pid = pidFlywheel2.update(targetFlywheelSpeed, currentFlywheelSpeed2, pidElapsedTime.seconds());
//        robot.motorOutR.setPower(fly1pid);
//        robot.motorOutL.setPower(fly2pid);
//        pidElapsedTime.reset();
//
//        //Intake Motor
//        if(gamepad1.right_bumper && !isIntakeAdd)
//        {
//            currentIntakeSpeed += 0.05;
//            isIntakeAdd = true;
//        }
//        else if(!gamepad1.right_bumper)
//        {
//            isIntakeAdd = false;
//        }
//
//        if(gamepad1.left_bumper && !isIntakeSubtract)
//        {
//            currentIntakeSpeed -= 0.05;
//            isIntakeSubtract = true;
//        }
//        else if(!gamepad1.left_bumper)
//        {
//            isIntakeSubtract = false;
//        }
//        currentIntakeSpeed = Math.min(currentIntakeSpeed, 1);
//        currentIntakeSpeed = Math.max(currentIntakeSpeed, 0);
//
//        //Elevator Motor
//        if(gamepad1.right_trigger > 0.5 && !isElevatorAdd)
//        {
//            currentElevatorSpeed += 0.001;
//            isElevatorAdd = true;
//        }
//        else
//        {
//            isElevatorAdd = false;
//        }
//
//        if(gamepad1.left_trigger > 0.5 && !isElevatorSubtract)
//        {
//            currentElevatorSpeed -= 0.001;
//            isElevatorSubtract = true;
//        }
//        else
//        {
//            isElevatorSubtract = false;
//        }
//        currentElevatorSpeed = Math.min(currentElevatorSpeed, 1);
//        currentElevatorSpeed = Math.max(currentElevatorSpeed, 0);
//
//        //Set Powers
//        robot.motorElevator.setPower(-currentElevatorSpeed);
//        robot.motorIn.setPower(currentIntakeSpeed);
//
//        //DATA
//        telemetry.addData("Current Intake Speed: ", currentIntakeSpeed);
//        telemetry.addData("Current Elevator Speed: ", currentElevatorSpeed);
//        telemetry.addData("Current Flywheel 1 Speed: ", currentFlywheelSpeed1);
//        telemetry.addData("Current Flywheel 2 Speed: ", currentFlywheelSpeed2);
//        telemetry.addData("Target Flywheel Speed: ", targetFlywheelSpeed);
//        telemetry.addData("Target Flywheel Power: ", targetFlywheelPower);
//        telemetry.addData("Flywheel 1 PID: ", fly1pid);
//        telemetry.addData("Flywheel 2 PID: ", fly2pid);
//
//
//        //KICKER
//        if(gamepad1.cross && !x_pressed)
//        {
//            isKickerExtended = !isKickerExtended;
//            buttonElapsedTime.reset();
//        } else if (buttonElapsedTime.seconds() > 0.5) {
//            isKickerExtended = false;
//        }
//        x_pressed = gamepad1.cross;
//
//        if(isKickerExtended)
//        {
//            robot.kicker.setPosition(0.07); //0.55
//        }
//        else if(!isKickerExtended)
//        {
//            robot.kicker.setPosition(0.247); //0.73
//        }
//
//        //FLICKER
//        if(gamepad1.square && !square_pressed)
//        {
//            isFlickerExtended = !isFlickerExtended;
//            buttonElapsedTime.reset();
//        } else if (buttonElapsedTime.seconds() > 0.25) {
//            isFlickerExtended = false;
//        }
//        square_pressed = gamepad1.square; //MERCURIO FOR TEO
//
//        if(isFlickerExtended)
//        {
//            robot.flicker.setPosition(0.35);
//        }
//        else if(!isFlickerExtended)
//        {
//            robot.flicker.setPosition(0.17);
//        }
//
//        //PID Controller
//
//
//        telemetry.addData("Flicker", isFlickerExtended);
//        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
    }
}