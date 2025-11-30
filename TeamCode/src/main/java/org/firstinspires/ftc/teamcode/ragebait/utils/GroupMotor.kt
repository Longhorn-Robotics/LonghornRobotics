package org.firstinspires.ftc.teamcode.ragebait.utils

import com.qualcomm.robotcore.hardware.DcMotorEx

// A Monad that can apply actions to groups of motors at once
class GroupMotor(val motors: List<DcMotorEx>) {
    fun apply(action: (DcMotorEx) -> Unit) {
        motors.forEach(action)
    }
}