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
open class StartUserRegistration : AbstractRoutine<Boolean>("start_user_registration", Public.PUBLIC, SQLDataType.BOOLEAN) {
    companion object {

        /**
         * The parameter
         * <code>public.start_user_registration.RETURN_VALUE</code>.
         */
        val RETURN_VALUE: Parameter<Boolean?> = Internal.createParameter("RETURN_VALUE", SQLDataType.BOOLEAN, false, false)

        /**
         * The parameter <code>public.start_user_registration.name</code>.
         */
        val NAME: Parameter<String?> = Internal.createParameter("name", SQLDataType.VARCHAR, false, false)

        /**
         * The parameter
         * <code>public.start_user_registration.emailaddress</code>.
         */
        val EMAILADDRESS: Parameter<String?> = Internal.createParameter("emailaddress", SQLDataType.VARCHAR, false, false)

        /**
         * The parameter
         * <code>public.start_user_registration.desiredpassword</code>.
         */
        val DESIREDPASSWORD: Parameter<String?> = Internal.createParameter("desiredpassword", SQLDataType.VARCHAR, false, false)
    }

    init {
        returnParameter = RETURN_VALUE
        addInParameter(NAME)
        addInParameter(EMAILADDRESS)
        addInParameter(DESIREDPASSWORD)
    }

    /**
     * Set the <code>name</code> parameter IN value to the routine
     */
    fun setName_(value: String?): Unit = setValue(NAME, value)

    /**
     * Set the <code>name</code> parameter to the function to be used with a
     * {@link org.jooq.Select} statement
     */
    fun setName_(field: Field<String?>): Unit {
        setField(NAME, field)
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
     * Set the <code>desiredpassword</code> parameter IN value to the routine
     */
    fun setDesiredpassword(value: String?): Unit = setValue(DESIREDPASSWORD, value)

    /**
     * Set the <code>desiredpassword</code> parameter to the function to be used
     * with a {@link org.jooq.Select} statement
     */
    fun setDesiredpassword(field: Field<String?>): Unit {
        setField(DESIREDPASSWORD, field)
    }
}
