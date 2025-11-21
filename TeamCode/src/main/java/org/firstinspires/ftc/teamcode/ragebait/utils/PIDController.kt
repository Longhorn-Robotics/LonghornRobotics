package org.firstinspires.ftc.teamcode.ragebait.utils

import com.bylazar.configurables.annotations.Configurable;


//@Configurable
class PIDController
/**
 * construct PID controller
 * @param _Kp Proportional coefficient
 * @param _Ki Integral coefficient
 * @param _Kd Derivative coefficient
 * @param _Kf Feedforward provider, optional (default always 0). Takes target, state -> ff val
 */(@JvmField var Kp: Double, @JvmField var Ki: Double, @JvmField var Kd: Double) {
    //     Kf = () -> 0.0;
    var lastError: Double = 0.0
    var integralSum: Double = 0.0

    /**
     * update the PID controller output
     * @param target where we would like to be, also called the reference
     * @param state where we currently are, I.E. motor position
     * @param dt delta time, time since last check
     * @return the command to our motor, I.E. motor power
     */
    fun update(target: Double, state: Double, dt: Double): Double {
        // PID logic and then return the output
        val error = state - target
        val derivative = (error - lastError) / dt
        lastError = error
        integralSum += error * dt
        return Kp * error + Kd * derivative + Ki * integralSum
    }
}
