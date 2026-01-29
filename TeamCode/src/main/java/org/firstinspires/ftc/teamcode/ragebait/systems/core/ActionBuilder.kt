package org.firstinspires.ftc.teamcode.ragebait.systems.core

class ActionBuilder private constructor(
    val name: String,
    private val compositionType: CompositionType,
    private val contextList: ArrayList<Action>,
) {

    constructor(name: String) : this(name, CompositionType.Single, arrayListOf()) {}

    // The composite actions work slightly differently
    // Interruptions simply interrupt the current action(s).
    // This means that sequenced and chain flows shouldn't depend too heavily on teardown,
    // so you should
    // TODO: More complicated teardown during composite action interrupts

    // They work by constructing special actions that dispatch to sub-actions that they hold
    // a reference to.

    // There are methods that very simply elevate a default single action into either parallel
    // (`with`) or sequenced (`chain`) actions. They will, however, give an error if trying
    // to mix and match, so for more advanced action flows use the dedicated static methods.
    // They are constructed using hidden context


    companion object {
        // TODO: These will likely need extra work once dependencies are a thing
        // “A closure is a poor man’s object; an object is a poor man’s closure”

        /**
         * Create a composite action that runs several actions in parallel.
         * Ends once all the actions end. Depending on the update order is
         * undefined behavior.
         * @param name Name of the action
         * @param actionList List of actions to be run in parallel
         * @param interruptible Whether this action can be interrupted
         * @param restartable Whether this action is restartable
         * @return The composite parallel action
         * */
        fun Parallel(
            name: String,
            actionList: ArrayList<Action>,
            interruptible: Boolean = true,
            restartable: Boolean = false
        ) =
            ActionBuilder(name)
                .init { actionList.forEach { it.start() } }
                .loop {
                    actionList.forEach { it.update() }
                    actionList.all { it.running }
                }
                .end { interrupted ->
                    if (interrupted)
                        actionList.filter { it.running }.forEach { it.interrupt() }
                    else
                    // Make sure we don't get any dead actions on an emergency stop
                        actionList.filter { it.running }.forEach {
                            it.stop(
                                doEnd = false,
                                asInterrupt = false
                            )
                        }
                    // We let actions end on their own, so we only have to deal with
                    // ending/interrupting them if we've been interrupted
                }
                .interruptible(interruptible)
                .restartable(restartable)
                .simpleBuild() // SimpleBuild to ensure we don't loop

        /**
         * Create a sequenced action that runs several actions in sequence.
         * Ends once all the actions end. Interrupting/stopping will only interrupt
         * the currently running action. This means stopping might not work as intended, be careful
         * @param name Name of the action
         * @param actionList List of actions to be run in parallel
         * @param interruptible Whether this action can be interrupted
         * @param restartable Whether this action is restartable
         * @return The composite parallel action
         * */
        fun Sequenced(
            name: String,
            actionList: ArrayList<Action>,
            interruptible: Boolean = true,
            restartable: Boolean = false
        ): Action {
            var currentAction = 0
            return ActionBuilder(name)
                .init {
                    currentAction = 0
                    actionList[currentAction].start()
                }
                .loop {
                    actionList[currentAction].update()
                    val result = actionList[currentAction].running
                    if (result) return@loop true
                    currentAction++
                    currentAction < actionList.size
                }
                .end { interrupted ->
                    if (actionList[currentAction].running) {
                        if (interrupted) actionList[currentAction].interrupt()
                        else actionList[currentAction].stop(
                            doEnd = false,
                            asInterrupt = false)
                    }
                }
                .interruptible(interruptible)
                .restartable(restartable)
                .simpleBuild()
        }

        // TODO: Nested Sequence
        // Make a sequence that undoes on interrupt by unwinding the stop actions
    }

    private enum class CompositionType {
        Single,
        Parallel,
        Sequenced
    }

    private var _init: () -> Unit = {}
    private var _loop: () -> Boolean = { true }
    private var _end: (Boolean) -> Unit = {}
    private var _interruptible: Boolean = true
    private var _restartable: Boolean = false
    private var _startRunning: Boolean = false

    fun init(func: () -> Unit): ActionBuilder {
        _init = func
        return this
    }

    fun loop(func: () -> Boolean): ActionBuilder {
        _loop = func
        return this
    }

    fun end(func: (Boolean) -> Unit): ActionBuilder {
        _end = func
        return this
    }

    fun interruptible(yn: Boolean): ActionBuilder {
        _interruptible = yn
        return this
    }

    fun restartable(yn: Boolean): ActionBuilder {
        _restartable = yn
        return this
    }

    fun startRunning(yn: Boolean): ActionBuilder {
        _startRunning = yn
        return this
    }

    fun chain(nextName: String): ActionBuilder =
        when (compositionType) {
            CompositionType.Parallel -> throw Error("Cannot mix action composition types; Use static `Parallel` and `Sequenced` methods for complex action flows")
            else -> {
                contextList.add(simpleBuild())
                ActionBuilder(nextName, CompositionType.Sequenced, contextList)
            }
        }

    fun with(nextName: String): ActionBuilder =
        when (compositionType) {
            CompositionType.Sequenced -> throw Error("Cannot mix action composition types; Use static `Parallel` and `Sequenced` methods for complex action flows")
            else -> {
                contextList.add(simpleBuild())
                ActionBuilder(nextName, CompositionType.Parallel, contextList)
            }
        }

    private fun simpleBuild(): Action =
        Action(name, _init, _loop, _end, _interruptible, _restartable)

    fun build(masterName: String? = null): Action {
        val built = simpleBuild()
        contextList.add(built)
        val finalName = masterName ?: contextList.first().name
        return when (compositionType) {
            CompositionType.Single -> built
            CompositionType.Parallel -> Parallel(finalName, contextList)
            CompositionType.Sequenced -> Sequenced(finalName, contextList)
        }
    }
}