package org.firstinspires.ftc.teamcode.ragebait.systems

import java.lang.Error
import kotlin.reflect.KProperty

class DependencyCell<T: SubSystem>(private val systemClass: Class<T>) {

    private val cached by lazy {
        val l = SubSystem.systems.filterIsInstance<T>(systemClass)
        if (l.isEmpty()) throw Error("Dependency \"${systemClass.name}\" not found")
        if (l.size > 1) throw Error("Multiple subsystem references found")
        l[0]
    }

    companion object {
        operator fun <E: SubSystem>invoke(type: Class<E>) = DependencyCell<E>(type)
        inline operator fun <reified E: SubSystem>invoke() = DependencyCell<E>(E::class.java)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = cached
}