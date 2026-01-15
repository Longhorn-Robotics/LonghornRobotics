package org.firstinspires.ftc.teamcode.ragebait.systems.core.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem
import java.lang.NullPointerException

// We can safely assume everything is DcMotorEx because all legal FTC motors support it

class motorEncoder(val name: String) {
    private val realMotor: DcMotorEx by lazy {
        SubSystem.defaultOpMode.hardwareMap.get(DcMotorEx::class.java, name) ?: throw NullPointerException("Hardware device not found")
    }

    val targetPositionTolerance: Int
        get() = realMotor.targetPositionTolerance
    val velocity: Double
        get() = realMotor.velocity
    fun getVelocity(unit: AngleUnit): Double = realMotor.getVelocity(unit)
    val motorEnabled: Boolean
        get() = realMotor.isMotorEnabled
    val currentPosition: Int
        get() = realMotor.currentPosition
//    val mode: DcMotor.RunMode
//        get() = realMotor.mode
//    val motorType: MotorConfigurationType
//        get() = realMotor.motorType
    val portNumber: Int
        get() = realMotor.portNumber
    val powerIsFloat: Boolean
        get() = realMotor.powerFloat
    val targetPosition: Int
        get() = realMotor.targetPosition
    val busy: Boolean
        get() = realMotor.isBusy
    val direction: DcMotorSimple.Direction
        get() = realMotor.direction
    val power: Double
        get() = realMotor.power
}

class motorEx {
    val realMotor: DcMotorEx by lazy {
        TODO()
    }
}