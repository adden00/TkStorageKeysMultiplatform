package com.adden00.tkstoragekeys.features.enter_password_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.common.Constants
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EnterPasswordScreen(
    navigator: Navigator = LocalNavigator.currentOrThrow
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val passwordEditText = remember { mutableStateOf("") }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .imePadding(), snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                    value = passwordEditText.value,
                    onValueChange = {
                        passwordEditText.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedLabelColor = TkGrey
                    ),
                    label = {
                        Text("Введите ключ")
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if (passwordEditText.value == KEY) {
                            navigator.replace(Screens.Reception)
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                snackbarHostState.showSnackbar("Ключ неверный")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TkMain,
                        disabledContainerColor = TkMain.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                ) {
                    Text(
                        "Войти"
                    )
                }

            }
        }
    }
}

private const val KEY = "925720"