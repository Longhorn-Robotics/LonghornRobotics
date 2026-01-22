package org.firstinspires.ftc.teamcode.ragebait.systems

import com.qualcomm.robotcore.eventloop.opmode.OpMode

abstract class SubSystem(
    val opMode: OpMode
) {

    companion object SystemRegistrar {
        val systems = mutableSetOf<SubSystem>()
    }

    init {
        @Suppress
        systems.add(this)
    }

    // maybe use reflection to do this
//    ab

    abstract fun init()
//    fun init_loop()
//    fun start()
    abstract fun loop()
    abstract fun stop()
}