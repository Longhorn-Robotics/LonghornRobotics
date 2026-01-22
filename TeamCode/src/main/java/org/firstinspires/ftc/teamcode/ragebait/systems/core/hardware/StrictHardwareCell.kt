package org.firstinspires.ftc.teamcode.ragebait.systems.core.hardware

import org.firstinspires.ftc.teamcode.ragebait.systems.core.SubSystem
import kotlin.reflect.KProperty

/**
 * A cell that declares strict access to hardware. Used for things like servos,
 * and also motors and whatnot. This also ensures that
 * */
abstract class StrictHardwareCell<T>(name: String, subSystem: SubSystem) {
    init {
        when (SubSystem.Companion.hardwareDepMap[name]) {
            null -> SubSystem.Companion.hardwareDepMap[name] = subSystem
            else -> throw NullPointerException("Conflicting hardware reservation for $name")
        }
    }

    abstract operator fun getValue(thisRef: Any?, property: KProperty<*>): T;
}