package org.firstinspires.ftc.teamcode.ragebait.systems

/// An action
/// Supports chaining into other actions, parallelization, e.t.c.
class Action(
    val init: () -> Unit,
    val loop: () -> Boolean,
    val end: (Boolean) -> Unit,
    ) {

    var running: Boolean = false
            private set
    private var toRun = false; // Whether we're about to start running
    private var interrupted = false;


    /**
     * Schedules the action to start as soon as possible.
     * Made so it is safe to call multiple times at once.
     * But undefined to call it while already running. (i haven't
     * decided the elegant way to handle that, either ignore, setup
     * to rerun, e.t.c. (i think setup to rerun is bad)).
     * It returns false if unsuccessful (I want to avoid throwing
     * since this is a ROBOT that should KEEP RUNNING)
     * */
    fun schedule(): Boolean { TODO() }
    fun interrupt(): Boolean { TODO() }
}