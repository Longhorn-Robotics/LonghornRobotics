package org.firstinspires.ftc.teamcode.ragebait.systems.core

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class DependencyCell<T: SubSystem>(thisSystem: SubSystem, val dependency: KClass<T>) {

    init {
        SubSystem.addDependency(thisSystem, dependency)
    }

    companion object {
        inline operator fun <reified DepType: SubSystem> invoke(thisSystem: SubSystem): DependencyCell<DepType> =
            DependencyCell(thisSystem, DepType::class)
    }

    var cache: T? = null

    // Subsystems will be run under a try-catch, but we still want to log this in the error push
    // So this will in fact throw. Don't depend on this throwing inside of the subsystem though; use
    // optional deps for that (whenever I decide to implement them)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = cache
            ?: SubSystem.letSys(dependency){ cache = it; it }
            ?: throw Exception("Failed to get dependency")
}