package org.firstinspires.ftc.teamcode.ragebait.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareLite;
import org.firstinspires.ftc.teamcode.ragebait.hardware.RobotHardwareYousef;
import org.firstinspires.ftc.teamcode.ragebait.utils.PIDController;

import org.firstinspires.ftc.teamcode.ragebait.utils.ButtonAction;

@TeleOp(name = "TeleopYousef")
public class TeleopYousef extends OpMode {
    RobotHardwareYousef robot = new RobotHardwareYousef();

    //Current Speeds
    double currentElevatorSpeed = 0.7;
    double currentIntakeSpeed = 0.7;

    //PID Gun Stuff
    double currentFlywheelSpeed1;
    double currentFlywheelSpeed2;
    double targetFlywheelPower = 0.7;
    double targetFlywheelSpeed;
    PIDController pidFlywheel1 = new PIDController(-0.001, 0, 0);
    PIDController pidFlywheel2 = new PIDController(-0.001, 0, 0);

    //Add & Subtract For testing
    boolean isIntakeAdd = false;
    boolean isIntakeSubtract = false;
    boolean isElevatorAdd = false;
    boolean isElevatorSubtract = false;
    boolean isGunAdd = false;
    boolean isGunSubtract = false;

    //Buttons & Servo Extendeders
    boolean isKickerExtended = false;
    boolean x_pressed_gmpd1 = false;
    boolean isFlickerExtended = false;
    boolean square_pressed_gmpd1 = false;
    final double joystickBaseSpeed = 0.7f;//0.3f;

    //OnOff INTAKE & OUTTAKE
    boolean intakeOn = true;
    boolean outtakeOn = true;
    boolean x_pressed_gmpd2 = false;
    boolean square_pressed_gmpd2 = false;

    //Elapsed Time
    private ElapsedTime buttonElapsedTime = new ElapsedTime();
    private ElapsedTime pidElapsedTime = new ElapsedTime();

    private final ButtonAction[] buttonActions = {
            //Kicker
            new ButtonAction(() -> gamepad1.left_bumper, () -> isKickerExtended = !isKickerExtended),
    };

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

        //Gun Motor
        if(gamepad1.dpad_up && !isGunAdd)
        {
            targetFlywheelPower += 0.05;
            isGunAdd = true;
        }
        else if(!gamepad1.dpad_up)
        {
            isGunAdd = false;
        }

        if(gamepad1.dpad_down && !isGunSubtract)
        {
            targetFlywheelPower -= 0.05;
            isGunSubtract = true;
        }
        else if(!gamepad1.dpad_down)
        {
            isGunSubtract = false;
        }
        targetFlywheelPower = Math.min(targetFlywheelPower, 1);
        targetFlywheelPower = Math.max(targetFlywheelPower, 0);

        targetFlywheelSpeed = targetFlywheelPower * 2800;

        currentFlywheelSpeed1 = robot.motorOutR.getVelocity();
        currentFlywheelSpeed2 = robot.motorOutL.getVelocity();

        double fly1pid = pidFlywheel1.update(targetFlywheelSpeed, currentFlywheelSpeed1, pidElapsedTime.seconds());
        double fly2pid = pidFlywheel2.update(targetFlywheelSpeed, currentFlywheelSpeed2, pidElapsedTime.seconds());
        pidElapsedTime.reset();

        //Intake Motor
        if(gamepad1.right_bumper && !isIntakeAdd)
        {
            currentIntakeSpeed += 0.05;
            isIntakeAdd = true;
        }
        else if(!gamepad1.right_bumper)
        {
            isIntakeAdd = false;
        }

        if(gamepad1.left_bumper && !isIntakeSubtract)
        {
            currentIntakeSpeed -= 0.05;
            isIntakeSubtract = true;
        }
        else if(!gamepad1.left_bumper)
        {
            isIntakeSubtract = false;
        }
        currentIntakeSpeed = Math.min(currentIntakeSpeed, 1);
        currentIntakeSpeed = Math.max(currentIntakeSpeed, 0);

        //Elevator Motor
        if(gamepad1.right_trigger > 0.5 && !isElevatorAdd)
        {
            currentElevatorSpeed += 0.005;
            isElevatorAdd = true;
        }
        else
        {
            isElevatorAdd = false;
        }

        if(gamepad1.left_trigger > 0.5 && !isElevatorSubtract)
        {
            currentElevatorSpeed -= 0.001;
            isElevatorSubtract = true;
        }
        else
        {
            isElevatorSubtract = false;
        }
        currentElevatorSpeed = Math.min(currentElevatorSpeed, 1);
        currentElevatorSpeed = Math.max(currentElevatorSpeed, 0);

        //DATA
        telemetry.addData("Current Intake Speed: ", currentIntakeSpeed);
        telemetry.addData("Current Elevator Speed: ", currentElevatorSpeed);
        telemetry.addData("Current Flywheel 1 Speed: ", currentFlywheelSpeed1);
        telemetry.addData("Current Flywheel 2 Speed: ", currentFlywheelSpeed2);
        telemetry.addData("Target Flywheel Speed: ", targetFlywheelSpeed);
        telemetry.addData("Target Flywheel Power: ", targetFlywheelPower);
        telemetry.addData("Flywheel 1 PID: ", fly1pid);
        telemetry.addData("Flywheel 2 PID: ", fly2pid);

        telemetry.addData("Intake ON: ", intakeOn);
        telemetry.addData("Outtake ON: ", outtakeOn);

        //Intake Kill Button
        if(gamepad1.circle && !x_pressed_gmpd2)
        {
            intakeOn = !intakeOn;
            x_pressed_gmpd2 = true;
        }
        else if(!gamepad1.circle)
        {
            x_pressed_gmpd2 = false;
        }

        if(!intakeOn)
        {
            robot.motorElevator.setPower(0);
            robot.motorIn.setPower(0);
        }
        else
        {
            robot.motorElevator.setPower(-currentElevatorSpeed);
            robot.motorIn.setPower(currentIntakeSpeed);
        }

        //Outtake Kill Button
        if(gamepad1.triangle && !square_pressed_gmpd2)
        {
            outtakeOn = !outtakeOn;
            square_pressed_gmpd2 = true;
        }
        else if(!gamepad1.triangle)
        {
            square_pressed_gmpd2 = false;
        }

        if(!outtakeOn)
        {
            robot.motorOutR.setPower(0);
            robot.motorOutL.setPower(0);
        }
        else
        {
            robot.motorOutR.setPower(fly1pid);
            robot.motorOutL.setPower(fly2pid);
        }

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

        //FLICKER
        if(gamepad1.square && !square_pressed_gmpd1)
        {
            isFlickerExtended = !isFlickerExtended;
            buttonElapsedTime.reset();
        } else if (buttonElapsedTime.seconds() > 0.25) {
            isFlickerExtended = false;
        }
        square_pressed_gmpd1 = gamepad1.square;

        if(isFlickerExtended)
        {
            robot.flicker.setPosition(0.35);
        }
        else if(!isFlickerExtended)
        {
            robot.flicker.setPosition(0.17);
        }

        //movement code (tweak later)
        double final_throttle = 0.0f;
        double final_strafe = 0.0f;
        double final_yaw = 0.0f;

//        double joystickMultiplier = joystickBaseSpeed + (1.0f - gamepad1.right_trigger);
        double joystickMultiplier = joystickBaseSpeed;

        final_throttle += (gamepad1.left_stick_y * joystickMultiplier);
        final_strafe += (gamepad1.left_stick_x * joystickMultiplier);
        final_yaw += (gamepad1.right_stick_x * joystickMultiplier);

        robot.motorFL.setPower(final_throttle - final_strafe - final_yaw);
        robot.motorBL.setPower(final_throttle + final_strafe - final_yaw);
        robot.motorFR.setPower(final_throttle + final_strafe + final_yaw);
        robot.motorBR.setPower(final_throttle - final_strafe + final_yaw);

        telemetry.addData("Flicker", isFlickerExtended);
        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
    }
}