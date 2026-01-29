package org.firstinspires.ftc.teamcode.ragebait.systems.core

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.ragebait.systems.core.utils.BiMap
import kotlin.reflect.KClass

abstract class SubSystem(
    val opMode: OpMode
) {

    // Dependency handling:
    // Subsystems are constructed (kotlin init block)
    //  - Their dependencies are declared through dependency cells
    //  - They register themselves in the subsystem set of the dep registrar
    //  - The cells are also captured
    //  - We construct a dependency graph
    // Then we do the resolution phase:
    //  - Typical graph propagation
    //  - We detect cycles; If any, error, otherwise proceed
    //  - Put in sorted list based on this ordering function: A < B if B depends on A
    //  - (Topological sort)
    //  - AN DATA STRUCTURE AND ALGORITHM ?!?!?
    // Now we wait to entire Runtime (fun init)
    //  - Go through the list, initializing in order.
    //  - It's now guaranteed that every dependency will be
    //     initialized before its dependents.
    //
    // TODO: Hardware Dependency Cell
    //  - If two things get the same motor, they should conflict
    // Possible TODO: Custom error handling thingymajigy
    //  - BEAM time, straight Erlanging it
    // Consideration: "Let it Crash"
    //  - Allow some failed initializations, carry on with as many as possible
    //
    // Possible TODO: allow for weak dependencies
    //  - Two forms: dependencies not required during initialization, and those that are completely optional
    //  - These can be handled by just not considering them during resolution

    companion object {

        val defaultOpMode by lazy {
            systemEnumeration.inv[0]?.opMode ?: throw NullPointerException("No subsystems initialized, tried to get opmode!")
        }

        val reservedHardware = mutableSetOf<String>()
        inline fun <reified T>getHardwareStrict(name: String): T? {
            if (name in reservedHardware)
                return null
            else {
                reservedHardware.add(name)
                return SubSystem.defaultOpMode.hardwareMap.get(T::class.java, name) ?: throw NullPointerException("Hardware not found")
            }
        }
        inline fun <reified T>getHardware(name: String): T =
            SubSystem.defaultOpMode.hardwareMap.get(T::class.java, name) ?: throw NullPointerException("Hardware not found")

        var systemCount = 0;
        val systemEnumeration = BiMap<SubSystem, Int>()
        val systemClassMap = BiMap<KClass<out SubSystem>, SubSystem>()
        val depLinks: MutableMap<SubSystem, MutableSet<KClass<out SubSystem>>> = mutableMapOf()

        fun addDependency(dependent: SubSystem, dependency: KClass<out SubSystem>) {
            depLinks[dependent]!!.add(dependency)
        }

        lateinit var systems: Array<SubSystem>
        fun doInitializations() {
            // Generate an actual graph
            // Adjacency list, graph[system] = setOf(deps)
            val graph = Array(systemCount) { IntArray(systemCount) }
            depLinks.forEach { entry ->
                val system = entry.key
                val deps = entry.value
                // Null assertion speedrun
                val idx = systemEnumeration[system]!!
                deps.map{systemClassMap[it]!!}.forEach { graph[idx][systemEnumeration[it]!!] = 1 }
            }

            fun println(str: String) = systemEnumeration.inv[0]!!.opMode.telemetry.addLine(str)

            fun getName(i: Int) = systemClassMap.inv[systemEnumeration.inv[i]!!]!!.simpleName!!
            graph.forEachIndexed { i, deps ->
                val sysname = getName(i)
                val depStr =
                    deps.indices.filter { deps[it] != 0 }.joinToString(", ") { getName(it) }
                val line = "$sysname: $depStr"
                println(line)
            }

            // Uses Kahn's topological sort algorithm
            val initList = arrayListOf<Int>()
            val edgeCounts = IntArray(systemCount) {graph[it].sum()}
            val freeNodes = edgeCounts.withIndex().filter { it.value == 0 }.mapTo(ArrayDeque()) { it.index }
            while (freeNodes.isNotEmpty()) {
                val current = freeNodes.removeFirst()
                initList.add(current)
                (0..<systemCount).filter{graph[it][current] == 1}.forEach {
                    graph[it][current] = 0
                    edgeCounts[it]--
                    if (edgeCounts[it] == 0) {
                        freeNodes.addLast(it)
                    }
                }
            }
            // Check if there are still edges, if so then we have a loop folks
            if (edgeCounts.sum() > 0) {
                // I have to use a NullPointerException here because some exception types
                // are uncaught and make the entire bot crash and restart
                throw NullPointerException("Cyclical Dependencies Detected!")
            }
            println(initList.joinToString{it.toString()})

            initList.forEach{
                systemEnumeration.inv[it]!!.init()
            }

            systems = initList.map{ systemEnumeration.inv[it]!! }.toTypedArray()
        }

        fun doLoops() = systems.forEach { it.loop() }
        fun doStops() = systems.forEach { it.stop() }

        inline fun <reified T: SubSystem> getSys(): T = (systemClassMap[T::class] ?: throw NullPointerException("")) as T
    }

    init {
        systemEnumeration[this] = systemCount
        systemCount++
        systemClassMap[this::class] = this
        depLinks[this] = mutableSetOf()
    }

    abstract fun init()
//    fun init_loop()
//    fun start()
    abstract fun loop()
    abstract fun stop()
}