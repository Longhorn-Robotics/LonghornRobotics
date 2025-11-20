package org.firstinspires.ftc.teamcode.ragebait.utils;


public class PIDController {

    double Kp;
    double Ki;
    double Kd;
    double lastError;
    double integralSum;

    /**
     * construct PID controller
     * @param _Kp Proportional coefficient
     * @param _Ki Integral coefficient
     * @param _Kd Derivative coefficient
     */
    public PIDController(double _Kp, double _Ki, double _Kd) {
        Kp = _Kp;
        Ki = _Ki;
        Kd = _Kd;
        lastError = 0;
        integralSum = 0;
    }

    /**
     * update the PID controller output
     * @param target where we would like to be, also called the reference
     * @param state where we currently are, I.E. motor position
     * @param dt delta time, time since last check
     * @return the command to our motor, I.E. motor power
     */
    public double update(double target, double state, double dt) {
        // PID logic and then return the output
        double error = state - target;
        double derivative = (error - lastError) / dt;
        lastError = error;
        integralSum += error * dt;
        return Kp * error + Kd * derivative + Ki * integralSum;
    }
}
