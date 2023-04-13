package dev.hydraulic.bugzino.common

fun emailAddressToRoleName(emailAddress: String) =
    emailAddress.replace("@", "_at_").map { if (!it.isLetterOrDigit()) "_" else it }.joinToString("")

/**
 * How many characters are in the signup confirmation code.
 */
const val SIGNUP_CODE_LENGTH = 4
