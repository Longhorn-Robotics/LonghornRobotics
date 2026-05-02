package org.firstinspires.ftc.teamcode.ragebait.systems.core.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

/**
 * A collection of useful delegate utils.
 */

/**
 * A delegate wrapper to that handles casting a delegate from one type to another.
 */
class Caster<Inner, Outer>(
    private val target: KMutableProperty0<Inner>,
    val castTo: (Outer) -> Inner,
    val castFrom: (Inner) -> Outer,
) : ReadWriteProperty<Any?, Outer> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): Outer {
        return castFrom(target.get())
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Outer) {
        target.set(castTo(value))
    }
}

/**
 * A common use of a Caster delegate, wrapping a double as an int.
 */
fun IntAsDouble(target: KMutableProperty0<Int>) = Caster<Int, Double>(
    target, { it.toInt() }, { it.toDouble() }
)
