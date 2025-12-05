package org.firstinspires.ftc.teamcode.ragebait.systems

class ActionBuilder {

    companion object {
        @JvmStatic
        fun demo_creation() {
            ActionBuilder()
                .init {}
                .loop {
                    // Ret true to end, false to continue
                    true
                }
                .end { interupted ->  }
                .interruptible(true) // By default
                // .requirements([Subsystems])
                .build()

            // How to Handle GROUPING (parallel/sequential)
            // I KNOW
            // I create helper functions to make actions that
            // have their own sub-actions: They delegate their calls
            // to currently running actions they hold references to, e.t.c.
            // In that case actions *should be stateful,* holding on to a
            // generic state class (or wait can this be handled by closures?)
            // CLOSURES ARE AWESOME, I CAN JUST USE THEM

            // Imagine I have a chain of commands acting as a macro
            // How to differentiate between and interrupt cancelling
            // the entire macro or just an intermediary step?
            // Within the macro I would probably want to cancel an
            // intermediary

            // Top-down iteration to figure out the best approach to
            // 1. I want a command that shoots a ball
            //    a. It should be useable from both teleop and auton
            val shootBall: Action;

        }
    }

    var init_f: () -> Unit = {}
    var loop_f: () -> Boolean = { true }
    var end_f: (Boolean) -> Unit = {}
//        var _v_requirements: ?
    var caninterrupt: Boolean = true

    fun init(func: () -> Unit): ActionBuilder {
        init_f = func
        return this
    }

    fun loop(func: () -> Boolean): ActionBuilder {
        loop_f = func
        return this
    }

    fun end(func: (Boolean) -> Unit): ActionBuilder {
        end_f = func
        return this
    }

    fun interruptible(yn: Boolean): ActionBuilder {
        caninterrupt = yn
        return this
    }

    fun build(): Action = Action(init_f, loop_f, end_f)
}