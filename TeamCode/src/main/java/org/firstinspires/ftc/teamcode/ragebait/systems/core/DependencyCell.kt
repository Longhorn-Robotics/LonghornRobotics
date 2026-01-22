package org.firstinspires.ftc.teamcode.ragebait.systems.core

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class DependencyCell<T: SubSystem>(thisSystem: SubSystem, val dependency: KClass<T>) {

    init {
        SubSystem.addDependency(thisSystem, dependency as KClass<SubSystem>)
    }

    companion object {
        inline operator fun <reified DepType: SubSystem> invoke(thisSystem: SubSystem): DependencyCell<DepType> =
            DependencyCell(thisSystem, DepType::class)
    }

    val cache: T by lazy {
        @Suppress("UNCHECKED_CAST")
        SubSystem.systemClassMap[dependency]!! as T
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = cache
}