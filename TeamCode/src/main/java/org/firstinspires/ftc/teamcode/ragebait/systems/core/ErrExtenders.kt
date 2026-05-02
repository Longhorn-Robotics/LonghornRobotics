package org.firstinspires.ftc.teamcode.ragebait.systems.core

/**
 * A set of extension functions to work with errors within the Subsystem error system. They
 * alleviate the need for explicit catching blocks, while also making the possibility of error
 * more explicit. They handle logging the error automatically.
 *
 * Some use Null as an error-by-value, very akin to Rust's Option/Result methods `map`, `or_err`, etc.
 * Some use actual kotlin/jvm exceptions
 *
 * Further improvements:
 * Could we replace the ever-present `err: string` term with something different? For example, a closure
 * to make it so code doesn't always have to generate the error value, or maybe put such extensions
 * in their own separate overloads.
 * We might replace the usage of null with an actual `Result`-like type, maybe also as just new
 * methods rather than replacing the existing Null-y methods. This also leads naturally into adding
 * a proper error type, instead of the current system of just using string.
 * */

/**
 * Execute some code conditional on a value being nonnull.
 *
 * If the value is null, it logs a Subsystem error, otherwise it executes the block.
 * Warning: Don't call this with a `.?` null-safe operator, as that will silently skip this.
 *
 * @param err The error message to send if the value is null.
 * @param block The closure to execute on the value if it isn't null.
 * @return Null if the original value was null, otherwise the return value of the block (which may
 *  itself be null)
 * */
inline fun <T : Any, R> T?.withErr(err: String, block: (T) -> R): R? = if (this != null) {
    block(this)
} else {
    SubSystem.pushError(err)
    null
}

/**
 * Logs an error on a null value, and passes execution along.
 *
 * Warning: Don't call this with a `.?` null-safe operator, as that will silently skip this.
 * @param err The error message to send if the value is null.
 * @return The original value, even if it is null.
 * */
fun <T : Any> T?.orErr(err: String): T? = if (this != null) this else {
    SubSystem.pushError(err)
    null
}

/**
 * Runs some code, catching exceptions and logging them as subsystem errors. Adds some context to
 * the error with the exception.
 *
 * This *only* catches exceptions, not null values.
 *
 * If you want to also do special exception handling, you can leverage that this
 * returns null on error and do some sort of `?: run` and/or consider using a side-effecting
 * lambda block to isolate errors.
 * For example, if I wanted to log the error but then recover with another value, I might do this:
 * ```
 * val input = tryErr("Failed to process input, using defaults") { getInputs() }
 *             ?: { defaultInputs }
 * ```
 *
 * @param err The error message to add context to the exception when logging.
 * @param block The block to run within the exception catching block.
 * @return Null if an exception occurred, otherwise the return value of the block.
 */
inline fun <T, R> T.tryErr(err: String, block: (T) -> R): R? {
    try {
        return block(this)
    } catch (e: Exception) {
        SubSystem.pushError("TryError $err: $e")
        return null
    }
}