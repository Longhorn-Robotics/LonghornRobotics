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
    private ElapsedTime kickerElapsedTime = new ElapsedTime();
    private ElapsedTime flickerElapsedTime = new ElapsedTime();
    private ElapsedTime pidElapsedTime = new ElapsedTime();

    private final ButtonAction[] buttonActions = {
            //Increment Gun Motor
            new ButtonAction(() -> gamepad1.dpad_up, () -> {
                targetFlywheelPower += 0.05;
                targetFlywheelPower = Math.min(targetFlywheelPower, 1);
                targetFlywheelPower = Math.max(targetFlywheelPower, 0);
            }),
            new ButtonAction(() -> gamepad1.dpad_down, () -> {
                targetFlywheelPower -= 0.05;
                targetFlywheelPower = Math.min(targetFlywheelPower, 1);
                targetFlywheelPower = Math.max(targetFlywheelPower, 0);
            }),

            //Increment Intake Motor
            new ButtonAction(() -> gamepad1.right_bumper, () -> {
                currentIntakeSpeed += 0.05;
                currentIntakeSpeed = Math.min(currentIntakeSpeed, 1);
                currentIntakeSpeed = Math.max(currentIntakeSpeed, 0);
            }),
            new ButtonAction(() -> gamepad1.left_bumper, () -> {
                currentIntakeSpeed -= 0.05;
                currentIntakeSpeed = Math.min(currentIntakeSpeed, 1);
                currentIntakeSpeed = Math.max(currentIntakeSpeed, 0);
            }),

            //Increment Elevator Motor
            new ButtonAction(() -> gamepad1.right_trigger > 0.5, () -> {
                currentElevatorSpeed += 0.05;
                currentElevatorSpeed = Math.min(currentElevatorSpeed, 1);
                currentElevatorSpeed = Math.max(currentElevatorSpeed, 0);
            }),
            new ButtonAction(() -> gamepad1.left_trigger > 0.5, () -> {
                currentElevatorSpeed -= 0.05;
                currentElevatorSpeed = Math.min(currentElevatorSpeed, 1);
                currentElevatorSpeed = Math.max(currentElevatorSpeed, 0);
            }),

            //Kill All Intake Button
            new ButtonAction(() -> gamepad1.circle, () -> intakeOn = !intakeOn),

            //Kill All Outtake Button
            new ButtonAction(() -> gamepad1.triangle, () -> outtakeOn = !outtakeOn),

            //Kicker
            new ButtonAction(() -> gamepad1.cross, () -> {
                isKickerExtended = !isKickerExtended;
                kickerElapsedTime.reset();
            }),

            //Flicker
            new ButtonAction(() -> gamepad1.square, () -> {
                isFlickerExtended = !isFlickerExtended;
                flickerElapsedTime.reset();
            }),


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
        kickerElapsedTime.reset();
        flickerElapsedTime.reset();
        pidElapsedTime.reset();
    }

    // Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
    @Override
    public void loop() {
        //Button Actions
        ButtonAction.doActions(buttonActions);

        //Flywheel PID
        targetFlywheelSpeed = targetFlywheelPower * 2800;

        currentFlywheelSpeed1 = robot.motorOutR.getVelocity();
        currentFlywheelSpeed2 = robot.motorOutL.getVelocity();

        double fly1pid = pidFlywheel1.update(targetFlywheelSpeed, currentFlywheelSpeed1, pidElapsedTime.seconds());
        double fly2pid = pidFlywheel2.update(targetFlywheelSpeed, currentFlywheelSpeed2, pidElapsedTime.seconds());
        pidElapsedTime.reset();

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

        //Intake Kill
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

        //Outtake Kill
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
        if(kickerElapsedTime.seconds() > 0.5)
        {
            isKickerExtended = false;
        }

        if(isKickerExtended)
        {
            robot.kicker.setPosition(0.07); //0.55
        }
        else if(!isKickerExtended)
        {
            robot.kicker.setPosition(0.247); //0.73
        }

        //FLICKER
        if(flickerElapsedTime.seconds() > 0.25)
        {
            isFlickerExtended = false;
        }

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