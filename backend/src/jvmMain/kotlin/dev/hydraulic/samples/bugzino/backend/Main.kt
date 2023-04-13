@file:Suppress("unused", "FunctionName")

package dev.hydraulic.samples.bugzino.backend

import dev.hydraulic.bugzino.common.emailAddressToRoleName
import org.postgresql.pljava.annotation.Function
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.random.Random

object BugzinoRPCs {
    private val newConnection: Connection get() = DriverManager.getConnection("jdbc:default:connection")

    private inline fun <T> withConnection(body: Connection.() -> T) = with(newConnection) { use(body) }
    private inline fun <T> Connection.withStatement(statement: String, body: PreparedStatement.() -> T) =
        prepareStatement(statement).use(body)

    @Function(trust = Function.Trust.UNSANDBOXED, security = Function.Security.DEFINER)   // so we can run sendmail
    @JvmStatic
    fun start_user_registration(name: String, emailAddress: String, desiredPassword: String): Boolean {
        try {
            val code = buildString {
                for (i in 1..4) {
                    val char = 'a' + Random.nextInt(26)
                    append(char)
                }
            }

            // TODO: Error handling. Duplicate registration handling.
            withConnection {
                withStatement("INSERT INTO t_pending_user_registrations (email, name, code, password) VALUES (?, ?, ?, ?);") {
                    setString(1, emailAddress)
                    setString(2, name)
                    setString(3, code)
                    setString(4, desiredPassword)
                    executeUpdate()
                }
            }

            // TODO: This is pretty insecure, we should be using javax.mail or similar here.
            val send = ProcessBuilder("/usr/sbin/sendmail", emailAddress).start()
            send.outputWriter().use {
                it.appendLine("Subject: Bugzino registration")
                it.appendLine()
                it.appendLine("Your registration code is ${code.uppercase()}")
            }

            return true
        } catch (e: Throwable) {
            Logger.getGlobal().log(Level.SEVERE, "error", e)
            e.printStackTrace()
            throw e
        }
    }

    private data class PendingRegistration(val name: String, val code: String, val password: String)

    private fun Connection.findRegistration(email: String): PendingRegistration? {
        withStatement("DELETE FROM t_pending_user_registrations WHERE email = ? RETURNING name, code, password") {
            setString(1, email)
            with(executeQuery()) {
                if (next())
                    return PendingRegistration(getString("name"), getString("code"), getString("password"))
            }
            return null
        }
    }

    @Function(security = Function.Security.DEFINER)
    @JvmStatic
    fun complete_user_registration(emailAddress: String, userSubmittedCode: String): String {
        withConnection {
            val reg = findRegistration(emailAddress) ?: return "No registration for that email address was found."
            if (reg.code != userSubmittedCode)
                return "Code didn't match."

            val roleName = emailAddressToRoleName(emailAddress)

            val safeChars = "!#@,/,+ "
            val unacceptablePasswordChars = reg.password.filterNot { it.isLetterOrDigit() || it in safeChars }
            if (unacceptablePasswordChars.isNotEmpty())
                return "Password contains bad characters: $unacceptablePasswordChars"

            // TODO: SQL injection! This is weak, but I don't know if you can use ? params in CREATE ROLE statements.
            // There is probably a way to abuse what we're doing here.

            // TODO: transactions? we're already in one here it seems.
            val statement = createStatement()
            statement.execute("CREATE ROLE $roleName LOGIN PASSWORD '${reg.password}' IN ROLE normal_user")
            statement.execute("GRANT normal_user TO $roleName")
            withStatement("INSERT INTO t_users (email, name, db_user) VALUES (?, ?, (SELECT oid FROM pg_catalog.pg_authid WHERE rolname = ?))") {
                setString(1, emailAddress)
                setString(2, reg.name)
                setString(3, roleName)
                execute()
            }

            return "OK"
        }
    }

    init {
        // Hack: force AutoCloseableKt to be initialized.
        //
        // Explanation: Kotlin use{} statements place a call to AutoCloseableKt.closeFinally in the generated finally block.
        // Unfortunately, once an exception is thrown PL/Java apparently won't let us do classloading anymore from that point on,
        // moaning about how we tried to do stuff after an elog(ERROR) occurred. Means: anything we access in a finally block
        // must have already been accessed!
        //
        // https://github.com/tada/pljava/issues/422
        Class.forName("kotlin.jdk7.AutoCloseableKt", true, javaClass.classLoader)
    }
}
