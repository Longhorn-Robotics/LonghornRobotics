package org.firstinspires.ftc.teamcode.ragebait.systems.core

import kotlin.reflect.KClass
import kotlin.reflect.KProperty


/**
 * A delegate provider that allows for getting a reference to a subsystem from another class.
 * Just use like so:
 * ```
 * val otherSystem: OtherSystem by DependencyCell();
 * ```
 */
class DependencyCell {

    class DependencyDelegate<T: SubSystem>(val dependency: KClass<T>) {
        var cache: T? = null

        // Subsystems will be run under a try-catch, but we still want to log this in the error push
        // So this will in fact throw. Don't depend on this throwing inside of the subsystem though; use
        // optional deps for that (whenever I decide to implement them)
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = cache
            ?: SubSystem.letSys(dependency){ cache = it; it }
            ?: throw Exception("Failed to get dependency")
    }
    inline operator fun <reified T: SubSystem> provideDelegate(
        thisRef: SubSystem,
        prop: KProperty<*>,
    ): DependencyDelegate<T> {
        // The dependency verification is handled in the total
        SubSystem.addDependency(thisRef, T::class)
        return DependencyDelegate(T::class)
    }
}