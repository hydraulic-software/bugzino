package dev.hydraulic.bugzino.frontend.utils

import org.jetbrains.annotations.Contract
import java.io.Serial
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

/**
 * An exception type that indicates the end user made a correctable mistake of some sort.
 *
 * It's worth distinguishing these types of errors from other types, because it allows the message will be displayed and logged differently
 * to a crash i.e. some other kind of unexpected exception, if caught at the top level. In future this could be a place to add error
 * hashing, metrics/telemetry, for pointing users towards Stack Overflow, etc.
 *
 * Don't throw this exception or subtypes from libraries, as you don't know where your input data came from (unless the library is
 * specifically designed to handle user input only). Even then it's a good idea to throw a sub-type, so callers can customize the behaviour.
 */
open class UserError(override var message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    /**
     * The exception is constructed with the same message as the cause, preferring the localized message, then the message, then the
     * class name of the cause as the message if not available.
     */
    constructor(cause: Throwable) : this(
        cause.localizedMessage ?: cause.message ?: cause.javaClass.name,
        cause
    )

    companion object {
        @Serial
        private const val serialVersionUID: Long = 3225196550210736040L

        // Contracts disabled: https://youtrack.jetbrains.com/issue/KT-51704

        /** If condition is false, throw the given message as a [UserError]. */
        @OptIn(ExperimentalContracts::class)
        @JvmStatic
        fun verify(condition: Boolean, message: String) {
            contract {
                returns() implies (condition)
            }
            if (!condition)
                throw UserError(message)
        }

        /** If value is null, throws the given message as a [UserError]. */
        @OptIn(ExperimentalContracts::class)
        @Contract("value -> !null")
        @JvmStatic
        fun <T> verifyNotNull(value: T?, message: String): T {
            contract {
                returns() implies (value != null)
            }
            if (value == null)
                throw UserError(message)
            return value
        }

        /**
         * Runs the block and if the given exception class is thrown, catches it and rethrows it as a [UserError] with the given message
         * and that exception as a cause.
         *
         * @param exceptionClass UserError is only thrown if a thrown exception from [block] is this type or a subtype.
         * @param message If not null, the exception message is replaced. If null, the existing message is reused.
         */
        inline fun <T> rethrow(exceptionClass: KClass<out Throwable>, message: String? = null, block: () -> T): T =
            try {
                block()
            } catch (e: Throwable) {
                when {
                    e is UserError -> throw e
                    exceptionClass.java.isInstance(e) -> if (message != null) throw UserError(message, e) else throw UserError(e)
                    else -> throw e
                }
            }
    }
}
