package org.firstinspires.ftc.teamcode.ragebait.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class RobotHardwareLite {
    HardwareMap hwMap;
    public DcMotorEx motor1Test;
    public DcMotorEx motor2Test;


    public RobotHardwareLite() {}

    public void init(HardwareMap ahwMap) {
        // Save reference to hardware map
        hwMap = ahwMap;

        //Motor 1 Test
        motor1Test = hwMap.get(DcMotorEx.class, "motor1");
        motor1Test.setDirection(DcMotor.Direction.FORWARD);
        motor1Test.setPower(0);
        motor1Test.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Motor 2 Test
        motor2Test = hwMap.get(DcMotorEx.class, "motor2");
        motor2Test.setDirection(DcMotor.Direction.FORWARD);
        motor2Test.setPower(0);
        motor2Test.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }
}
