package org.firstinspires.ftc.teamcode.ragebait.systems.core

/**
 * A bijective map interface that allows for a one-to-one mapping between keys and values.
 * This interface provides methods to put, get, invert, check existence, remove entries,
 * and retrieve the forward and reverse maps.
 *
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 */
class BiMap<K, V> {
    private val map = mutableMapOf<K,V>()
    private val invMap = mutableMapOf<V,K>()

    operator fun get(key: K): V? = map[key]
    operator fun set(key: K, value: V) {
        map[key] = value
        invMap[value] = key
    }

    class InverseMap<K,V>(
        val outer: BiMap<K, V>
    ) {
        operator fun get(value: V): K? = outer.invMap[value]
        operator fun set(value: V, key: K) {
            outer.map[key] = value
            outer.invMap[value] = key
        }
    }

    val inv = InverseMap<K,V>(this)
}