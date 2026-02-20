package org.firstinspires.ftc.teamcode.ragebait.systems.core

/**
 * Actually call with just a dot `nullable.withErr(...)` or it will silently skip the error
 * */
inline fun <T : Any, R> T?.withErr(err: String, block: (T) -> R): R? = if (this != null) {
    block(this)
} else {
    SubSystem.pushError(err)
    null
}

/**
 * Actually call with just a dot `nullable.orErr(...)` or it will silently skip the error
 * */
fun <T : Any> T?.orErr(err: String): T? = if (this != null) this else {
    SubSystem.pushError(err)
    null
}

/**
 * Runs some code, catching exceptions and turning them into logged errors.
 * It adds some context to the error with the exception.
 * This *only* catches exceptions, not null values.
 * If you want to also do special exception handling, you can leverage that this
 * returns null on error and do some sort of `?: run` and/or consider using a side-effecting
 * lambda block to isolate errors.
 */
inline fun <T, R> T.tryErr(err: String, block: (T) -> R): R? {
    try {
        return block(this)
    } catch (e: Exception) {
        SubSystem.pushError("TryError $err: $e")
        return null
    }
}