package org.firstinspires.ftc.teamcode.ragebait.systems.core

/** An action
 * Supports chaining into other actions, parallelization, e.t.c.
 * @property restartable: Whether calling `start` on an action already running action will cancel
 * the current action and restart it
 * @property interruptible: Masks whether interrupt works on this method or not.
 * */
class Action(
    val name: String,
    private val init: () -> Unit,
    private val loop: () -> Boolean,
    private val end: (Boolean) -> Unit,
    var interruptible: Boolean,
    var restartable: Boolean,
    startRunning: Boolean = false
    ) {

    var running: Boolean = false
            private set
    // Whether we're about to start running
    private var toRun = false;
    private var toInterrupt = false;

    init {
        running = startRunning
    }

    /**
     * Updates the action. To be run every frame and stuff
     * */
    fun update() {
        if (running) {
            if (toInterrupt && interruptible) {
                running = false
                toInterrupt = false
                end(true)
            } else {
                val result = loop()
                if (!result) {
                    // Action has finished
                    running = false
                    end(false)
                }
            }
        }
        // To avoid same-frame bugs, we allow for the "edge case" (not really that edge)
        // of trying to start the action immediately after stopping
        if (toRun && !running) {
            running = true
            init()
        }
        // To avoid queuing a double-action
        toRun = false
    }

    /**
     * Sets the action to start on the next update call.
     * Made so it is safe to call multiple times at once.
     * If the action is currently running and set as `restartable`,
     * it is stopped prematurely (**DOES NOT CALL END**) and restarted.
     * It returns false if unsuccessful (I want to avoid throwing
     * since this is a ROBOT that should KEEP RUNNING).
     * */
    fun start(): Boolean {
        if (running) {
            if (restartable) {
                toRun = true
                running = false
                return true
            } else {
                return false
            }
        } else {
            toRun = true
            return true
        }
    }

    /**
     * Interrupts the action (stops it early). Can be masked by
     * the `interruptible` property. Returns false if non-interruptible
     * no matter what, or if the process wasn't already running */
    fun interrupt(): Boolean {
        if (!interruptible or !running) return false
        interruptible = true
        return true
    }

    /**
     * Force stops an action. Cannot be masked.
     * @param doEnd: Whether to still run the ending
     * @param asInterrupt: If still ending properly, pass as interrupt
     * */
    fun stop(doEnd: Boolean, asInterrupt: Boolean) {
        running = false
        if (doEnd) end(asInterrupt)
    }
}