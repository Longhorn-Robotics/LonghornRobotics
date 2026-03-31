package org.firstinspires.ftc.teamcode.ragebait;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

// Finish this opmode to be able to figure out a servo's bounds.
// One should be able to control the read and control the servo's position while its running.
// Feel free to ask questions.

public class InterviewOp extends OpMode {
    Servo servo;

    @Override
    public void init() {
        servo = hardwareMap.get(Servo.class, "servo");
    }

    @Override
    public void loop() {

    }
}
