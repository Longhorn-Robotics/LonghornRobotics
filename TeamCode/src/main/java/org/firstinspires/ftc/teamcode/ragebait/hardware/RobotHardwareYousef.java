package org.firstinspires.ftc.teamcode.ragebait.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class RobotHardwareYousef {
    HardwareMap hwMap;

    //YOUSEF MOTORS AND SERVOS
    public DcMotorEx motorFR;
    public DcMotorEx motorFL;
    public DcMotorEx motorBR;
    public DcMotorEx motorBL;

    public DcMotorEx motorOutR;
    public DcMotorEx motorOutL;
    public DcMotorEx motorIn;
    public DcMotorEx motorElevator;

    public Servo kicker;
    public Servo flicker;

    private ElapsedTime period = new ElapsedTime();
    public double kickerOutPosition = 0.0419;
    public double kickerInPosition = 0.2358;

    public double flickerOutPosition = 0.35;
    public double flickerInPosition = 0.17;

    public RobotHardwareYousef() {}

    public void init(HardwareMap ahwMap) {
        // Save reference to hardware map
        hwMap = ahwMap;

        //Wheel Motors
        motorFR = hwMap.get(DcMotorEx.class, "motorFR");
        motorFL = hwMap.get(DcMotorEx.class, "motorFL");
        motorBR = hwMap.get(DcMotorEx.class, "motorBR");
        motorBL = hwMap.get(DcMotorEx.class, "motorBL");
        motorFR.setDirection(DcMotor.Direction.REVERSE);
        motorFL.setDirection(DcMotor.Direction.FORWARD);
        motorBR.setDirection(DcMotor.Direction.REVERSE);
        motorBL.setDirection(DcMotor.Direction.FORWARD);
        motorFR.setPower(0);
        motorFL.setPower(0);
        motorBR.setPower(0);
        motorBL.setPower(0);
        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Right Output Motor
        motorOutR = hwMap.get(DcMotorEx.class, "motorOutR");
        motorOutR.setDirection(DcMotor.Direction.REVERSE);
        motorOutR.setPower(0);
        motorOutR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Left Output Motor
        motorOutL = hwMap.get(DcMotorEx.class, "motorOutL");
        motorOutL.setDirection(DcMotor.Direction.FORWARD);
        motorOutL.setPower(0);
        motorOutL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Kicker & Flicker Servos
        kicker = hwMap.get(Servo.class, "kicker");
        flicker = hwMap.get(Servo.class, "flicker");

        motorElevator = hwMap.get(DcMotorEx.class, "motorElevator");
        motorElevator.setDirection(DcMotor.Direction.FORWARD);
        motorElevator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorElevator.setPower(0);

        motorIn = hwMap.get(DcMotorEx.class, "motorIn");
        motorIn.setDirection(DcMotor.Direction.FORWARD);
        motorIn.setPower(0);
        //intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
