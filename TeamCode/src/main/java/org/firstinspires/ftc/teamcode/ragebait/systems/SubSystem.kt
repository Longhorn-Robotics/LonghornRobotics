package org.firstinspires.ftc.teamcode.ragebait.systems

import com.qualcomm.robotcore.eventloop.opmode.OpMode

abstract class SubSystem(
    val opMode: OpMode
) {

    private class DependencyStruct<>(
    ) {

    }

    // TODO: Consider a dependency system
    companion object DependencyRegistrar {
    }

    // maybe use reflection to do this
//    ab

    abstract fun init()
//    fun init_loop()
//    fun start()
    abstract fun loop()
    abstract fun stop()
}