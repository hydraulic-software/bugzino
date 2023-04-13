/*
 * This file is generated by jOOQ.
 */
package dev.hydraulic.bugzino.frontend.db.tables.records


import dev.hydraulic.bugzino.frontend.db.tables.TTicket

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record4
import org.jooq.Row4
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class TTicketRecord() : UpdatableRecordImpl<TTicketRecord>(TTicket.T_TICKET), Record4<Int?, String?, String?, String?> {

    open var id: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    open var title: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    open var description: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    open var assignee: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    override fun fieldsRow(): Row4<Int?, String?, String?, String?> = super.fieldsRow() as Row4<Int?, String?, String?, String?>
    override fun valuesRow(): Row4<Int?, String?, String?, String?> = super.valuesRow() as Row4<Int?, String?, String?, String?>
    override fun field1(): Field<Int?> = TTicket.T_TICKET.ID
    override fun field2(): Field<String?> = TTicket.T_TICKET.TITLE
    override fun field3(): Field<String?> = TTicket.T_TICKET.DESCRIPTION
    override fun field4(): Field<String?> = TTicket.T_TICKET.ASSIGNEE
    override fun component1(): Int? = id
    override fun component2(): String? = title
    override fun component3(): String? = description
    override fun component4(): String? = assignee
    override fun value1(): Int? = id
    override fun value2(): String? = title
    override fun value3(): String? = description
    override fun value4(): String? = assignee

    override fun value1(value: Int?): TTicketRecord {
        this.id = value
        return this
    }

    override fun value2(value: String?): TTicketRecord {
        this.title = value
        return this
    }

    override fun value3(value: String?): TTicketRecord {
        this.description = value
        return this
    }

    override fun value4(value: String?): TTicketRecord {
        this.assignee = value
        return this
    }

    override fun values(value1: Int?, value2: String?, value3: String?, value4: String?): TTicketRecord {
        this.value1(value1)
        this.value2(value2)
        this.value3(value3)
        this.value4(value4)
        return this
    }

    /**
     * Create a detached, initialised TTicketRecord
     */
    constructor(id: Int? = null, title: String? = null, description: String? = null, assignee: String? = null): this() {
        this.id = id
        this.title = title
        this.description = description
        this.assignee = assignee
    }
}