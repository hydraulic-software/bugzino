package dev.hydraulic.bugzino.frontend.app

import androidx.compose.runtime.*
import com.impossibl.postgres.jdbc.PGSQLSimpleException
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.pool.HikariPool
import dev.hydraulic.bugzino.frontend.db.routines.references.editTicket
import dev.hydraulic.bugzino.frontend.db.tables.references.TICKET
import dev.hydraulic.bugzino.frontend.utils.UserError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jooq.Configuration
import org.jooq.Context
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

/**
 * Wraps access to the database. Methods should be called only from the UI thread and only inside a side effect, not during
 * composition.
 */
class DatabaseAccess(val username: String, password: String) : AutoCloseable {
    private val jdbcURL = System.getProperty("jdbcURL") ?: "jdbc:pgsql://localhost/${APP_BRAND_NAME.lowercase()}"

    class UnknownUser(username: String, cause: Throwable) : UserError("User $username is unknown.", cause)

    private val config = run {
        val c = HikariConfig()
        c.driverClassName = "com.impossibl.postgres.jdbc.PGDriver"
        c.jdbcUrl = jdbcURL
        c.username = username
        c.password = password
        c.maximumPoolSize = 10
        c.connectionTimeout = 3000

        c
    }

    private val dataSource = try {
        HikariDataSource(config)
    } catch (e: HikariPool.PoolInitializationException) {
        val c = e.cause
        if (c is PGSQLSimpleException) {
            if (c.sqlState == "28000")
                throw UnknownUser(username, c)
            throw e
        } else {
            throw e
        }
    }

    override fun close() {
        dataSource.close()
    }

    private val sql: DSLContext get() = DSL.using(dataSource, SQLDialect.POSTGRES)
    private val c: Configuration get() = sql.configuration()

    private val injectedServerDelay get() = if (jdbcURL.startsWith("jdbc:pgsql://localhost")) 150L else 0L

    suspend fun <T> serverCall(body: suspend DSLContext.(org.jooq.Configuration) -> T): T {
        return withContext(Dispatchers.IO) {
            delay(injectedServerDelay)
            sql.body(c)
        }
    }

    suspend fun <T> serverTransaction(body: DSLContext.(org.jooq.Configuration) -> T): T {
        return withContext(Dispatchers.IO) {
            delay(injectedServerDelay)
            sql.transactionResult { it: org.jooq.Configuration ->
                DSL.using(it).body(it)
            }
        }
    }

    suspend fun loadTicket(id: Int): Ticket? = serverCall {
        selectFrom(TICKET).where(TICKET.ID.eq(id)).awaitFirstOrNull()?.into(Ticket::class.java)
    }

    suspend fun saveTicket(ticket: Ticket) {
        serverTransaction {
            editTicket(it, ticket.id, ticket.title, ticket.description, ticket.assignee)
            _currentSearchResults.value = doSearch(currentSearchQuery)
        }
    }

    private val _currentSearchQuery = mutableStateOf<String?>(null)
    // Not using a mutableStateListOf here because we replace the whole contents every time anyway.
    // We could try to get smarter and diff the lists here, but it's not currently worth it.
    private val _currentSearchResults = mutableStateOf<List<TicketSummary>>(emptyList())
    val currentSearchResults: List<TicketSummary> get() = _currentSearchResults.value
    val currentSearchQuery: String? get() = _currentSearchQuery.value

    suspend fun searchTickets(query: String?) {
        _currentSearchQuery.value = query
        _currentSearchResults.value = serverCall { doSearch(query) }
    }

    private fun DSLContext.doSearch(query: String?): List<TicketSummary> {
        val from = select(TICKET.ID, TICKET.TITLE).from(TICKET)

        val q = if (query != null)
            from.where(TICKET.TITLE.containsIgnoreCase(query))
        else
            from

        return q.orderBy(TICKET.ID).fetch().into(TicketSummary::class.java).toList()
    }
}
