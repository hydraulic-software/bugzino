package dev.hydraulic.bugzino.frontend.app

// Types that mediate between the DB access code and the UI.
// We could use the data classes that jOOQ generates, but they
// are null-happy for working with views.

data class Ticket(val id: Int, val title: String, val description: String, val assignee: String?)

data class TicketSummary(val id: Int, val title: String)
