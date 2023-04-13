package dev.hydraulic.bugzino.frontend.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.hydraulic.bugzino.frontend.app.DatabaseAccess
import dev.hydraulic.bugzino.frontend.app.TicketDatabase
import dev.hydraulic.bugzino.frontend.db.routines.references.startUserRegistration
import dev.hydraulic.bugzino.frontend.ui.BaseScreen
import dev.hydraulic.bugzino.frontend.ui.IOButton
import dev.hydraulic.bugzino.frontend.ui.RoundedTextField
import dev.hydraulic.bugzino.frontend.ui.SingleFormLayout
import dev.hydraulic.bugzino.frontend.utils.InitialFocus

class SignUpScreen : BaseScreen() {
    private var name by mutableStateOf("")
    private var email by mutableStateOf("")
    private var password by mutableStateOf("")
    private var passwordCheck by mutableStateOf("")
    private var invalid by mutableStateOf(true)
    private val isValidSignUp get() = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == passwordCheck

    @Composable
    override fun ScreenContent() {
        SingleFormLayout {
            // TODO: Snackbar for error messages
            val errorMessage = remember { mutableStateOf<String?>(null) }

            Text(
                text = "Sign Up",
                fontSize = 2.em,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val mod = Modifier.fillMaxWidth().height(80.dp)

            InitialFocus { mod ->
                RoundedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        invalid = !isValidSignUp
                    },
                    label = { Text("Email address/username") },
                    modifier = mod,
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RoundedTextField(
                value = name,
                onValueChange = {
                    name = it
                    invalid = !isValidSignUp
                },
                label = { Text("Name") },
                modifier = mod,
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoundedTextField(
                value = password,
                onValueChange = {
                    password = it
                    invalid = !isValidSignUp
                },
                label = { Text("Password") },
                modifier = mod,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoundedTextField(
                value = passwordCheck,
                onValueChange = {
                    passwordCheck = it
                    invalid = !isValidSignUp
                },
                label = { Text("Confirm Password") },
                modifier = mod,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            val nav = LocalNavigator.currentOrThrow

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val mod = Modifier.height(IntrinsicSize.Min)

                OutlinedButton(onClick = { nav.pop() }, mod) {
                    Text("Cancel")
                }

                Spacer(mod.width(16.dp))

                IOButton(
                    action = {
                        // Log in as guest to create ourselves an account.
                        TicketDatabase = DatabaseAccess("guest", "guest")
                        TicketDatabase.serverCall { startUserRegistration(it, name, email, password) }
                    },
                    onSuccess = {
                        nav.push(ConfirmSignupCodeScreen(email, password))
                    },
                    modifier = mod,
                    enabled = !invalid,
                    buttonText = "Sign up",
                    errorMessage = errorMessage
                )
            }
        }
    }
}


// spec:85364e1a1b948df4
