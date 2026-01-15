package org.firstinspires.ftc.teamcode.ragebait.systems.core

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.ragebait.systems.core.utils.BiMap
import kotlin.reflect.KClass

abstract class SubSystem(
    val opMode: OpMode
) {

    // IDEA: Static opMode value

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
    //  - Handling different types of hardware
    //    - We have a base HardwareCell class that just has the basic functionality for reserving
    //        a hardware name, since the robot config basically constrains us to string unique ids
    //    - Then Specific subclasses handling each type of hardware
    //      - Disambiguation between encoder and full motor access!
    //      - Alright, make special wrapper classes for motors and
    //
    // TODO: Custom error handling thingymajigy
    //  - BEAM time, straight Erlanging it
    //  - Consideration: "Let it Crash": Allow some failed initializations, carry on with as many as possible
    //
    // TODO: allow for strict and weak dependencies
    //  - Strict dependency: this subsystem demands unique access to the super method
    //  - Two forms of weak: dependencies not required during initialization or are completely optional
    //     - These can be handled by just not considering them during resolution

    companion object {

        val defaultOpMode: OpMode by lazy {
            systemEnumeration.inv[0]?.opMode ?: throw java.lang.NullPointerException("Ts not defined yet")
        }

        // Hardware dependencies
        val hardwareDepMap = BiMap<String, SubSystem>()

        // Subsystem dependencies
        var systemCount = 0;
        val systemEnumeration = BiMap<SubSystem, Int>()
        val systemClassMap = BiMap<KClass<out SubSystem>, SubSystem>()
        val depLinks: MutableMap<SubSystem, MutableSet<KClass<out SubSystem>>> = mutableMapOf()

        fun addDependency(dependent: SubSystem, dependency: KClass<out SubSystem>) {
            depLinks[dependent]!!.add(dependency)
        }

        fun doInitializations() {
            // TODO: Proper error signalling
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
        }
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