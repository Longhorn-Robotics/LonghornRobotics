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

    public DcMotorEx motor;

    private ElapsedTime period = new ElapsedTime();

    public RobotHardwareLite() {}

    public void init(HardwareMap ahwMap) {
        // Save reference to hardware map
        hwMap = ahwMap;

        //Right Output Motor
        motor = hwMap.get(DcMotorEx.class, "motor");
        motor.setDirection(DcMotor.Direction.FORWARD);
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
