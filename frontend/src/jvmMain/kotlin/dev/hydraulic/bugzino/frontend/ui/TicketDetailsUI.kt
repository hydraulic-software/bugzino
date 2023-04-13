package dev.hydraulic.bugzino.frontend.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hydraulic.bugzino.frontend.app.Ticket
import dev.hydraulic.bugzino.frontend.app.TicketDatabase
import dev.hydraulic.bugzino.frontend.utils.InitialFocus
import dev.hydraulic.bugzino.frontend.utils.UndoPoint

/**
 * The editing screen for a ticket.
 */
class TicketDetailsUI private constructor(
    val id: Int,
    val title: MutableState<String>,
    val description: MutableState<String>,
    val assignee: MutableState<String?>,
    private val undoPoint: UndoPoint = UndoPoint(title, description, assignee)
) : AutoCloseable by undoPoint {
    constructor(ticket: Ticket) : this(ticket.id, mutableStateOf(ticket.title), mutableStateOf(ticket.description), mutableStateOf(ticket.assignee))

    @Composable
    fun render() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            InitialFocus { modifier ->
                RoundedTextField(title.value, { title.value = it }, modifier, "Title")
            }

            RoundedTextField(
                value = assignee.value ?: "Nobody",
                onValueChange = { assignee.value = it },
                labelText = "Assignee",
            )

            RoundedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                modifier = Modifier.weight(1f),
                labelText = "Description",
                singleLine = false,
            )

            saveAndDiscard()
        }
    }

    @Composable
    private fun saveAndDiscard() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            undoPoint.saveAndDiscardButtons {
                TicketDatabase.saveTicket(Ticket(id, title.value, description.value, assignee.value))
            }
        }
    }
}
