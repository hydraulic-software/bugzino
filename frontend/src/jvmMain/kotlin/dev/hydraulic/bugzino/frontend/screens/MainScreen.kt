package dev.hydraulic.bugzino.frontend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import colorHash
import dev.hydraulic.bugzino.frontend.app.Ticket
import dev.hydraulic.bugzino.frontend.app.TicketDatabase
import dev.hydraulic.bugzino.frontend.app.TicketSummary
import dev.hydraulic.bugzino.frontend.ui.*

object MainScreen : BaseScreen() {
    private val EDGE_PADDING = 20.dp

    override val fontSize: TextUnit
        get() = 20.sp

    @Composable
    override fun ScreenContent() {
        // This will track what we're _trying_ to load right now, which may be changed in the middle of a load.
        var loadingTicketID: Int? by remember { mutableStateOf(1) }
        val isLoading = loadingTicketID != null

        // The ticket object shown on the right hand side.
        var detailsUI: TicketDetailsUI? by remember { mutableStateOf(null) }
        var selectedTicketID: Int by remember { mutableStateOf(-1) }

        // This block will run or cancel/rerun when the ticket we're trying to load changes.
        LaunchedEffect(loadingTicketID) {
            loadingTicketID?.let {
                selectedTicketID = -1
                val ticket: Ticket? = TicketDatabase.loadTicket(it)
                detailsUI?.close()
                if (ticket != null) {
                    detailsUI = TicketDetailsUI(ticket)
                    selectedTicketID = ticket.id
                    loadingTicketID = null
                } else {
                    // Ticket was deleted out from underneath us.
                    detailsUI = null
                    loadingTicketID = null
                }
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Column(Modifier.width(350.dp).graphicsLayer { clip = false }) {
                var userSearch by remember { mutableStateOf("") }
                RoundedTextField(
                    userSearch,
                    { userSearch = it },
                    Modifier.fillMaxWidth().padding(start = EDGE_PADDING, top = EDGE_PADDING, end = EDGE_PADDING, bottom = 0.dp),
                    "Search",
                )

                val searchResults: List<TicketSummary> = TicketDatabase.currentSearchResults
                var searching: Boolean by remember { mutableStateOf(false) }

                LaunchedEffect(userSearch) {
                    searching = true
                    TicketDatabase.searchTickets(userSearch.takeUnless { it.isBlank() })
                    searching = false
                }

                // Left pane with a scrollable list of tickets (ListView)
                Box {
                    if (searchResults.isNotEmpty()) {
                        val lazyListState = rememberLazyListState()
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxHeight()
                                .graphicsLayer { alpha = 0.99f; clip = false }
                                .padding(EDGE_PADDING)
                                .verticalFadingEdge(lazyListState, 100.dp, edgeColor = Color.Black)
                        ) {
                            items(searchResults, key = { it.id }) { summary ->
                                TicketSummaryCard(
                                    summary.id,
                                    summary.title,
                                    { loadingTicketID = summary.id },
                                    colorHash(summary),
                                    offsetToTheSide = !searching && selectedTicketID == summary.id
                                )

                                Spacer(Modifier.height(EDGE_PADDING))
                            }
                        }
                    }

                    // Placeholder box.
                    Box(
                        Modifier
                            .padding(EDGE_PADDING)
                            .fillMaxSize()
                            .placeholder(searching)
                    )
                }
            }

            Box(Modifier.padding(EDGE_PADDING).placeholder(isLoading).fillMaxSize(), contentAlignment = Alignment.Center) {
                val d = detailsUI
                if (d == null) {
                    Text("Sorry, this ticket has been deleted.")
                } else {
                    d.render()
                }
            }
        }
    }
}
