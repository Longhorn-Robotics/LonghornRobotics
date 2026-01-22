<<<<<<<< HEAD:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/ragebait/systems/core/EnumBucket.kt
package org.firstinspires.ftc.teamcode.ragebait.systems.core
========
package org.firstinspires.ftc.teamcode.ragebait.systems.core.utils
>>>>>>>> origin/homelib_indev:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/ragebait/systems/core/utils/EnumBucket.kt

import java.util.EnumMap
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries


// I LOVE TYPE ERASURE
// TYPE ERASURE MAKES ME NEED AN ENTIRE HELPER CONSTRUCTOR!
inline fun <reified K : Enum<K>, T> exhaustiveEnumMap(noinline init: (K) -> T): ExhaustiveEnumMap<K, T> = ExhaustiveEnumMap(K::class.java, enumEntries<K>(), init)

/**
 * A class that provides an enum map guaranteed to be exhaustive.
 * Allowing use of indexing as purely non-null (unless you chose to
 * make `T` a nullable type for some unexplained reason.
 * */
class ExhaustiveEnumMap<K: Enum<K>, T>(private val kClass: Class<K>, private val entries: EnumEntries<K>, init: (K) -> T) {
    private val enumMap by lazy {
        val map = EnumMap<K, T>(kClass)
        for (key in entries) {
            map[key] = init(key)
        }
        map
    }

    operator fun get(key: K): T = enumMap.getValue(key)
    operator fun set(key: K, value: T) {enumMap[key] = value}
}

typealias EnumBucket<K, T> = ExhaustiveEnumMap<K, MutableSet<T>>
/** Necessary helper constructor because of type erasure */
inline fun <reified K : Enum<K>, T> enumBucket(): EnumBucket<K, T> = exhaustiveEnumMap { mutableSetOf() }
