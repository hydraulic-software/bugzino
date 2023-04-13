package dev.hydraulic.bugzino.frontend.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.hydraulic.bugzino.common.SIGNUP_CODE_LENGTH
import dev.hydraulic.bugzino.common.emailAddressToRoleName
import dev.hydraulic.bugzino.frontend.app.DatabaseAccess
import dev.hydraulic.bugzino.frontend.app.TicketDatabase
import dev.hydraulic.bugzino.frontend.db.routines.references.completeUserRegistration
import dev.hydraulic.bugzino.frontend.ui.BaseScreen
import dev.hydraulic.bugzino.frontend.ui.RoundedTextField
import dev.hydraulic.bugzino.frontend.ui.SingleFormLayout

class ConfirmSignupCodeScreen(private val email: String, private val password: String) : BaseScreen() {
    @Composable
    override fun ScreenContent() {
        SingleFormLayout {
            val focusRequesters = remember { List(SIGNUP_CODE_LENGTH) { FocusRequester() } }
            val codeEntry = remember { List(SIGNUP_CODE_LENGTH) { mutableStateOf("") } }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                codeEntry.forEachIndexed { index, entry ->
                    RoundedTextField(
                        value = entry.value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                entry.value = newValue.uppercase()
                                if (newValue.isNotEmpty() && index < codeEntry.lastIndex) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        label = {},
                        singleLine = true,
                        textStyle = LocalTextStyle.current + TextStyle(textAlign = TextAlign.Center, fontSize = 3.em),
                        modifier = Modifier
                            .focusRequester(focusRequesters[index])
                            .width(100.dp).height(100.dp)
                    )

                    Spacer(Modifier.width(16.dp))
                }
            }

            LaunchedEffect(Unit) { focusRequesters[0].requestFocus() }

            val inputCompleted = codeEntry.all { it.value.isNotEmpty() }

            AnimatedVisibility(inputCompleted) {
                CircularProgressIndicator()
            }

            var registrationFinalizing by remember { mutableStateOf(false) }

            if (inputCompleted && !registrationFinalizing) {
                registrationFinalizing = true
            }

            if (registrationFinalizing) {
                val code = codeEntry.joinToString("") { it.value }
                val nav = LocalNavigator.currentOrThrow

                LaunchedEffect(Unit) {
                    val r = TicketDatabase.serverCall { completeUserRegistration(it, email, code.lowercase()) }
                    if (r == "OK") {
                        TicketDatabase.close()
                        TicketDatabase = DatabaseAccess(emailAddressToRoleName(email), password)
                        nav.replaceAll(MainScreen)
                    } else {
                        throw Exception(r)   // todo: snackbar
                    }
                }
            }
        }

    }
}
