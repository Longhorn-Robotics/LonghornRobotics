package org.firstinspires.ftc.teamcode.ragebait.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class RobotHardwareLite {
    HardwareMap hwMap;

    //YOUSEF MOTORS AND SERVOS
    public DcMotorEx motorFlywheel1;
    public DcMotorEx motorFlywheel2;
    public DcMotorEx intakeMotor;
    public DcMotorEx elevatorMotor;
    public Servo kicker;
    public Servo flicker;

    private ElapsedTime period = new ElapsedTime();

    public RobotHardwareLite() {}

    public void init(HardwareMap ahwMap) {
        // Save reference to hardware map
        hwMap = ahwMap;

        motorFlywheel1 = hwMap.get(DcMotorEx.class, "motorFlywheel1");
        motorFlywheel2 = hwMap.get(DcMotorEx.class, "motorFlywheel2");
        motorFlywheel1.setDirection(DcMotor.Direction.FORWARD);
        motorFlywheel2.setDirection(DcMotor.Direction.REVERSE);
        motorFlywheel1.setPower(0);
        motorFlywheel2.setPower(0);
        motorFlywheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFlywheel2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        kicker = hwMap.get(Servo.class, "kicker");
        flicker = hwMap.get(Servo.class, "flicker");

        elevatorMotor = hwMap.get(DcMotorEx.class, "elevatorMotor");
        elevatorMotor.setDirection(DcMotor.Direction.FORWARD);
        elevatorMotor.setPower(0);

        intakeMotor = hwMap.get(DcMotorEx.class, "intakeMotor");
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setPower(0);
        //intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
