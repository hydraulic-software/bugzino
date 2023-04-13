package dev.hydraulic.bugzino.frontend.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.hydraulic.bugzino.common.emailAddressToRoleName
import dev.hydraulic.bugzino.frontend.app.DatabaseAccess
import dev.hydraulic.bugzino.frontend.app.TicketDatabase
import dev.hydraulic.bugzino.frontend.ui.BaseScreen
import dev.hydraulic.bugzino.frontend.ui.IOButton
import dev.hydraulic.bugzino.frontend.ui.RoundedTextField
import dev.hydraulic.bugzino.frontend.ui.SingleFormLayout
import dev.hydraulic.bugzino.frontend.utils.InitialFocus

class SignInScreen : BaseScreen() {
    private val username = mutableStateOf("")
    private val password = mutableStateOf("")

    @Composable
    override fun ScreenContent() {
        SingleFormLayout {
            val errorMessage = remember { mutableStateOf<String?>(null) }

            AnimatedVisibility(errorMessage.value != null) {
                Snackbar(modifier = Modifier.padding(bottom = 40.dp)) { Text(errorMessage.value!!) }
            }

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(painterResource("bug-logo.png"), "Logo")
            }

            InitialFocus { modifier ->
                RoundedTextField(
                    value = username.value,
                    onValueChange = { newValue -> username.value = newValue },
                    modifier = modifier.fillMaxWidth().height(80.dp),
                    labelText = "Username",
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RoundedTextField(
                value = password.value,
                onValueChange = { newValue -> password.value = newValue },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                labelText = "Password",
                visualTransformation = PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            val nav = LocalNavigator.currentOrThrow

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                val mod = Modifier.height(IntrinsicSize.Min)

                OutlinedButton(onClick = { nav.push(SignUpScreen()) }, mod) {
                    Text("Create Account")
                }

                Spacer(mod.width(16.dp))

                val enabled = username.value.isNotBlank()

                IOButton("Sign In", action = {
                    DatabaseAccess(emailAddressToRoleName(username.value.trim()), password.value.trim())
                }, onSuccess = { db ->
                    TicketDatabase = db
                    nav.replace(MainScreen)
                }, mod, enabled, errorMessage)
            }
        }
    }
}

// spec:2b5b86beee1d8b2
