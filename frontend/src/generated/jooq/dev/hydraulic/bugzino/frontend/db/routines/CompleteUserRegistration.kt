/*
 * This file is generated by jOOQ.
 */
package dev.hydraulic.bugzino.frontend.db.routines


import dev.hydraulic.bugzino.frontend.db.Public

import org.jooq.Field
import org.jooq.Parameter
import org.jooq.impl.AbstractRoutine
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class CompleteUserRegistration : AbstractRoutine<String>("complete_user_registration", Public.PUBLIC, SQLDataType.VARCHAR) {
    companion object {

        /**
         * The parameter
         * <code>public.complete_user_registration.RETURN_VALUE</code>.
         */
        val RETURN_VALUE: Parameter<String?> = Internal.createParameter("RETURN_VALUE", SQLDataType.VARCHAR, false, false)

        /**
         * The parameter
         * <code>public.complete_user_registration.emailaddress</code>.
         */
        val EMAILADDRESS: Parameter<String?> = Internal.createParameter("emailaddress", SQLDataType.VARCHAR, false, false)

        /**
         * The parameter
         * <code>public.complete_user_registration.usersubmittedcode</code>.
         */
        val USERSUBMITTEDCODE: Parameter<String?> = Internal.createParameter("usersubmittedcode", SQLDataType.VARCHAR, false, false)
    }

    init {
        returnParameter = RETURN_VALUE
        addInParameter(EMAILADDRESS)
        addInParameter(USERSUBMITTEDCODE)
    }

    /**
     * Set the <code>emailaddress</code> parameter IN value to the routine
     */
    fun setEmailaddress(value: String?): Unit = setValue(EMAILADDRESS, value)

    /**
     * Set the <code>emailaddress</code> parameter to the function to be used
     * with a {@link org.jooq.Select} statement
     */
    fun setEmailaddress(field: Field<String?>): Unit {
        setField(EMAILADDRESS, field)
    }

    /**
     * Set the <code>usersubmittedcode</code> parameter IN value to the routine
     */
    fun setUsersubmittedcode(value: String?): Unit = setValue(USERSUBMITTEDCODE, value)

    /**
     * Set the <code>usersubmittedcode</code> parameter to the function to be
     * used with a {@link org.jooq.Select} statement
     */
    fun setUsersubmittedcode(field: Field<String?>): Unit {
        setField(USERSUBMITTEDCODE, field)
    }
}
