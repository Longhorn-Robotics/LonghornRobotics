package org.firstinspires.ftc.teamcode.ragebait.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class RobotHardwareLite {
    HardwareMap hwMap;
    public DcMotorEx motorOut;
    public DcMotorEx motorCog;

    public WebcamName cam;


    public RobotHardwareLite() {}

    public void init(HardwareMap ahwMap) {
        // Save reference to hardware map
        hwMap = ahwMap;

        //Motor 1 Test
        motorOut = hwMap.get(DcMotorEx.class, "motorOut");
        motorOut.setDirection(DcMotor.Direction.FORWARD);
        motorOut.setPower(0);
        motorOut.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Motor 2 Test
        motorCog = hwMap.get(DcMotorEx.class, "motorCog");
        motorCog.setDirection(DcMotor.Direction.FORWARD);
        motorCog.setPower(0);
        motorCog.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Camera
        cam = hwMap.get(WebcamName.class, "Webcam 1");

    }
}
