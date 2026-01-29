package org.firstinspires.ftc.teamcode.ragebait.systems.core.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.ServoController
import com.qualcomm.robotcore.hardware.Servo as hardwareServo
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem
import java.lang.NullPointerException

// We can safely assume everything is DcMotorEx because all legal FTC motors support it

class MotorEncoder(val name: String) {
    private val realMotor: DcMotorEx by lazy {
        SubSystem.getHardware(name)
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

class Motor(val name: String) {
    private val realMotor: DcMotorEx by lazy {
        SubSystem.getHardwareStrict(name) ?: throw NullPointerException("Hardware is already reserved")
    }

    var motorEnabled: Boolean
        set(v) { if (v) {realMotor.setMotorEnable()} else {realMotor.setMotorDisable()} }
        get() = realMotor.isMotorEnabled

    var velocity: Double
        set(v) { realMotor.velocity = v }
        get() = realMotor.velocity

    var targetPositionTolerance: Int
        set(v) { realMotor.targetPositionTolerance = v }
        get() = realMotor.targetPositionTolerance

    fun getCurrent(unit: CurrentUnit?): Double = realMotor.getCurrent(unit)

    fun getCurrentAlert(unit: CurrentUnit?): Double = realMotor.getCurrentAlert(unit)

    fun setCurrentAlert(
        current: Double,
        unit: CurrentUnit?
    ) = realMotor.setCurrentAlert(current, unit)

    fun isOverCurrent(): Boolean = realMotor.isOverCurrent

    var motorType: MotorConfigurationType by realMotor::motorType

    fun getController(): DcMotorController? = realMotor.controller

    fun getPortNumber(): Int = realMotor.portNumber

    var zeroPowerBehavior: DcMotor.ZeroPowerBehavior by realMotor::zeroPowerBehavior

    var targetPosition: Int
        set(v) { realMotor.targetPosition = v}
        get() = realMotor.targetPosition

    fun isBusy(): Boolean = realMotor.isBusy

    fun getCurrentPosition(): Int = realMotor.currentPosition

    var mode: DcMotor.RunMode by realMotor::mode
    var direction: DcMotorSimple.Direction by realMotor::direction

    var power: Double
        set(v) { realMotor.power = v}
        get() = realMotor.power

    fun getConnectionInfo(): String? = realMotor.connectionInfo

    fun getVersion(): Int = realMotor.version

    fun resetDeviceConfigurationForOpMode() = realMotor.resetDeviceConfigurationForOpMode()

    fun close() = realMotor.close()
}

class Servo(val name: String) {
    val realServo: hardwareServo by lazy {
        SubSystem.getHardwareStrict(name) ?: throw NullPointerException("Hardware is already reserved")
    }

    companion object {
        val MAX_POSITION: Double = hardwareServo.MAX_POSITION
        val MIN_POSITION: Double = hardwareServo.MIN_POSITION
    }

    val controller: ServoController by lazy { realServo.controller }
    var direction: hardwareServo.Direction by realServo::direction
    var position: Double
        set(v) {realServo.position = v}
        get() = realServo.position

    fun scaleRange(min: Double, max: Double) = realServo.scaleRange(min, max)
}
