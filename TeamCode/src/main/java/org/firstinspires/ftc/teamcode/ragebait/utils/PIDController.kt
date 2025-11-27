package org.firstinspires.ftc.teamcode.ragebait.utils


/**
 * construct PID controller
 * @param _Kp Proportional coefficient
 * @param _Ki Integral coefficient
 * @param _Kd Derivative coefficient
 * @param _Kf Feedforward provider, optional (default always 0). Takes target, state -> ff val
 */
class PIDController @JvmOverloads constructor(
    @JvmField var proportional: Double,
    @JvmField var integral: Double,
    @JvmField var derivative: Double,
    @JvmField var feedforward: () -> Double = { 0.0 },
    @JvmField var integralCap: Double = Double.POSITIVE_INFINITY,) {
    //     Kf = () -> 0.0;
    private var lastError: Double = 0.0
    private var integralSum: Double = 0.0
    private var lastTarget = 0.0

    /**
     * update the PID controller output
     * @param target where we would like to be, also called the reference
     * @param state where we currently are, I.E. motor position
     * @param dt delta time, time since last check
     * @return the command to our motor, I.E. motor power
     */
    fun update(target: Double, state: Double, dt: Double): Double {

        // Integral Windup Prevention
        if (target != lastTarget) {
            integralSum = 0.0
        }
        if (integralSum > integralCap) integralSum = integralCap
        else if (integralCap < -integralSum) integralSum = -integralCap

        // PID logic and then return the output
        val error = state - target
        val derivative = (error - lastError) / dt
        lastError = error
        integralSum += error * dt
        return proportional * error + this.derivative * derivative + integral * integralSum + feedforward()
    }
}
