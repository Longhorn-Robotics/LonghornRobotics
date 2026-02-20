package org.firstinspires.ftc.teamcode.ragebait.systems.core

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.ragebait.systems.core.utils.BiMap
import kotlin.reflect.KClass

abstract class SubSystem {
    // # STATICS ARE NOT CLEARED BETWEEN RUNNING OPMODES
    companion object {

        // TODO: Make a real error type
        var errorLog: ArrayList<String> = arrayListOf()
        fun pushError(message: String) {
            errorLog.add(message)
            withOpMode { it.telemetry.addLine("Err: $message") }
        }

        // The vision: prints out the errors to a set line count, and temporally old errors stop
        // getting printed
        const val MAX_WORDS_LOGGED = 5
        private fun logErrors() {
            val toLog = errorLog.takeLast(MAX_WORDS_LOGGED)
            if (toLog.isNotEmpty()) {
                println("Last ${toLog.size} (max $MAX_WORDS_LOGGED) errors:")
                toLog.forEach(::println)
                println("${errorLog.size} total errors.")
            } else {
                println("No errors so far!")
            }
        }

        /**
         * The lines displayed every update for telemetry.
         */
        private fun statusDisplay() {
            defaultOpMode?.let {
                println("Update order: " + updateOrder.joinToString(", ") { (s, _) -> getName(s) })
                if (disabledSystems.isNotEmpty()) {
                    println("Disabled: " + disabledSystems.joinToString(", ") { getName(it) })
                }
                logErrors()
            }
        }

        var defaultOpMode: OpMode? = null
        inline fun <R> withOpMode(block: (OpMode) -> R): R? =
            defaultOpMode.withErr("OpMode not registered!", block)

        val reservedHardware = mutableSetOf<String>()
        inline fun <reified T : Any> getHardwareStrict(name: String): T? =
            if (name in reservedHardware) {
                pushError("Hardware $name already reserved"); null
            } else getHardware<T>(name)

        inline fun <reified T : Any> getHardware(name: String): T? = withOpMode { opMode ->
            opMode.hardwareMap.withErr("Attempted to get hardware before hardwareMap initialized") {
                it.get(
                    T::class.java, name
                ).orErr("Failed to get hardware: $name")
            }
        }

        var systemCount = 0
        val systemEnumeration = BiMap<SubSystem, Int>()
        val systemClassMap = BiMap<KClass<out SubSystem>, SubSystem>()

        // Systems that have failed, crashed, or are missing dependencies that are
        val poisonedSystems = BiMap<Int, Boolean>()
        val depLinks: MutableMap<SubSystem, MutableSet<KClass<out SubSystem>>> = mutableMapOf()

        fun clear() {
            errorLog.clear()

            systemCount = 0
            systemEnumeration.clear()
            systemClassMap.clear()
            poisonedSystems.clear()
            depLinks.clear()
        }

        fun addDependency(dependent: SubSystem, dependency: KClass<out SubSystem>) {
            depLinks[dependent]!!.add(dependency)
        }

        private lateinit var systems: Array<Pair<SubSystem, Int>>

        // Debug functions have debug levels of null safety
        private fun println(str: String) = defaultOpMode!!.telemetry.addLine(str)

        @Suppress("unused")
        private fun getName(i: Int) = systemClassMap.inv[systemEnumeration.inv[i]!!]!!.simpleName!!
        private fun getName(s: SubSystem) = systemClassMap.inv[s]!!.simpleName!!

        /**
         * Adjacency matrix, `graph[ A ][ B ]` means A depends on B
         */
        private lateinit var depGraph: Array<IntArray>
        fun doInitializations() {

            val graph = constructDependencyGraph()
            // Deep copies to save this for later
            depGraph = Array(systemCount) { graph[it].clone() }

            // Uses Kahn's topological sort algorithm
            // We can preserve the mathematical perfection and excellence here with no regards to
            // error values
            val initList = arrayListOf<Int>()
            val edgeCounts = IntArray(systemCount) { graph[it].sum() }
            val freeNodes =
                edgeCounts.withIndex().filter { it.value == 0 }.mapTo(ArrayDeque()) { it.index }
            while (freeNodes.isNotEmpty()) {
                val current = freeNodes.removeFirst()
                initList.add(current)
                (0..<systemCount).filter { graph[it][current] == 1 }.forEach {
                    graph[it][current] = 0
                    edgeCounts[it]--
                    if (edgeCounts[it] == 0) {
                        freeNodes.addLast(it)
                    }
                }
            }
            // Check if there are still edges, if so then we have a loop folks
            if (edgeCounts.sum() > 0) {
                // Todo: More advanced error detection, spit out the dependency cycles
                pushError("Cyclical Dependencies Detected!")
            }
            systems = initList.indices.map { idx -> Pair(systemEnumeration.inv[idx]!!, idx) }
                .toTypedArray()
            // println("Initialization order:")
            // println(systems.joinToString { getName(it.first) })

            constructUpdateList()
            logErrors()
            tryOnAllSys("Failure to initialize") { sys, _ -> sys.init() }
        }


        /**
         * Generate an actual graph
         * Adjacency matrix, `graph[ A ][B ]` means A depends on B
         * Error cases:
         *   Marks subsystems as poisoned if they have any dependencies that don't exist.
         *   These poisoned subsystems will have completely clear rows, and depend on the
         *   later algorithm to "cascade" this poisoning to depending systems.
         *   This is because systems might also fail during initialization or later, and further
         *   systems should also be poisoned.
         */
        private fun constructDependencyGraph(): Array<IntArray> {
            val junk = Array(systemCount) { false }
            val result = Array(systemCount) row@{ sysIdx ->
                val sys = systemEnumeration.inv[sysIdx]!!
                val deps = depLinks[sys]!!
                // We store the links with junk deps
                // Since they're already poisoned, we don't need to actually build out their rows.
                val depIdx = deps.map {
                    systemClassMap[it] ?: run {
                        pushError("Missing dependency ${it.simpleName} needed by ${systemClassMap.inv[sys]!!.simpleName}")
                        junk[sysIdx] = true
                        return@row IntArray(systemCount) // Exits and leaves the row empty
                    }
                }.map { systemEnumeration[it]!! }
                val row = IntArray(systemCount)
                depIdx.forEach { row[it] = 1 }
                return@row row
            }
            junk.withIndex().filter { it.value }.forEach { propagatePoisoning(it.index) }
            return result
        }

        var updateOrder = arrayOf<Pair<SubSystem, Int>>()
        var disabledSystems = arrayOf<SubSystem>()

        /**
         * Propagates a poisoning event for a single subsystem to its children. Assumes the current
         * state is already valid, so it skips over already-poisoned branches.
         */
        fun propagatePoisoning(sysIdx: Int) {
            Log.d("HornLib", "Poisoning $sysIdx")
            // Uses BFS, terminating early on poisoned nodes
            // We already know there are no cycles, so we can skip explicitly checking them.
            var frontier = listOf(sysIdx)
            while (frontier.isNotEmpty()) {
                Log.v("HornLib", "Frontier is $frontier")
                frontier = frontier.flatMap { idx ->
                    poisonedSystems[idx] = true
                    depGraph.map { it[idx] }
                        .withIndex()
                        .filter { it.value == 1 && !(poisonedSystems[it.index]!!) }
                        .map { it.index }
                }
            }
        }

        /**
         * (re)constructs the list of Subsystems to update based on the initial dependency-ordered
         * construction and poisoned systems.
         * */
        fun constructUpdateList() {
            updateOrder = systems.filter { (_, idx) -> !(poisonedSystems[idx])!! }.toTypedArray()
            disabledSystems = systems.filter { (_, idx) -> poisonedSystems[idx]!! }
                .map { (s, _) -> s }
                .toTypedArray()
            Log.v("HornLib", "Updated systems: ${updateOrder.size}")
        }


        private inline fun tryOnAllSys(err: String, block: (SubSystem, Int) -> Unit) {
            for (orderIdx in (0..<systemCount)) {
                // We stay vigilant of a previous crash invalidating further elements in the update
                // ordering.
                val (sys, idx) = if (orderIdx < updateOrder.size) updateOrder[orderIdx] else break;
                // TODO Performance consideration: Get these error strings through
                // TODO inlined closures to lazy-evaluate them. Or cache specifically these ones?
                tryErr("$err on ${getName(sys)}") {
                    block(sys, idx)
                } ?: run {
                    propagatePoisoning(idx)
                    constructUpdateList()
                }
            }
        }

        fun doLoops() {
            statusDisplay()
            tryOnAllSys("Failure during loop") { sys, _ -> sys.loop() }
        }
        fun doStops() {
            statusDisplay()
            tryOnAllSys("Failure during stop") { sys, _ -> sys.stop() }
        }

        @Suppress("unused")
        inline fun <reified S : SubSystem, Ret> letSys(noinline block: (S) -> Ret): Ret? =
            letSys(S::class, block)

        @Suppress("UNCHECKED_CAST")
        inline fun <S : SubSystem, Ret> letSys(clazz: KClass<S>, block: (S) -> Ret): Ret? =
            (systemClassMap[clazz] as? S).withErr(
                "Couldn't find system ${clazz.simpleName}", block
            )
    }

    // We shall be... illegal.
    /** Helper getter for opmodes inside of subsystems */
    protected val opMode: OpMode
        get() = defaultOpMode!!

    init {
        if (systemClassMap[this::class] != null) {
            throw NullPointerException("Conflicting subsystems! Can't initialize two instances of the same subsystem, ${this::class.simpleName}")
        }
        systemClassMap[this::class] = this
        systemEnumeration[this] = systemCount
        poisonedSystems[systemCount] = false
        systemCount++
        depLinks[this] = mutableSetOf()
    }

    abstract fun init()

    //    fun init_loop()
//    fun start()
    abstract fun loop()
    abstract fun stop()
}