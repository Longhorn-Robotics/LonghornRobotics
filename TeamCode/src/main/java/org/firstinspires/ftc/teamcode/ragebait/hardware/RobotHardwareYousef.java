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

    public RobotHardwareYousef() {}

    public void init(HardwareMap ahwMap) {
        // Save reference to hardware map
        hwMap = ahwMap;

        //Wheel Motors
        motorFR = hwMap.get(DcMotorEx.class, "motorFR");
        motorFR.setDirection(DcMotor.Direction.FORWARD);
        motorFR.setPower(0);
        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorFL = hwMap.get(DcMotorEx.class, "motorFL");
        motorFL.setDirection(DcMotor.Direction.FORWARD);
        motorFL.setPower(0);
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorBR = hwMap.get(DcMotorEx.class, "motorBR");
        motorBR.setDirection(DcMotor.Direction.FORWARD);
        motorBR.setPower(0);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorBL = hwMap.get(DcMotorEx.class, "motorBL");
        motorBL.setDirection(DcMotor.Direction.FORWARD);
        motorBL.setPower(0);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

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
        motorElevator.setPower(0);

        motorIn = hwMap.get(DcMotorEx.class, "motorIn");
        motorIn.setDirection(DcMotor.Direction.FORWARD);
        motorIn.setPower(0);
        //intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
