package org.firstinspires.ftc.teamcode.ragebait.systems

import com.qualcomm.robotcore.eventloop.opmode.OpMode

abstract class SubSystem(
    val opMode: OpMode
) {
    // TODO: Consider a depencency system
    // maybe use reflection to do this
    abstract fun init()
//    fun init_loop()
//    fun start()
    abstract fun loop()
    abstract fun stop()
}