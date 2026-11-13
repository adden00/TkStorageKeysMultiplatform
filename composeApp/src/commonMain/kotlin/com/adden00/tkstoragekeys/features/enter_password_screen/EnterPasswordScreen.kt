package com.adden00.tkstoragekeys.features.enter_password_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.features.enter_password_screen.mvi.EnterPasswordScreenEffect
import com.adden00.tkstoragekeys.features.enter_password_screen.mvi.EnterPasswordScreenEvent
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import com.adden00.tkstoragekeys.ui.showToast
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EnterPasswordScreen(
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    val viewModel: EnterPasswordViewModel = koinViewModel()
    val state = viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val nameEditText = remember { mutableStateOf("") }
    val passwordEditText = remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }

    fun tryLogin() {
        viewModel.obtainEvent(EnterPasswordScreenEvent.Login(nameEditText.value, passwordEditText.value))
    }

    LaunchedEffect("side effects") {
        viewModel.obtainEvent(EnterPasswordScreenEvent.CheckSession)
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                EnterPasswordScreenEffect.OpenMain -> navigator.replace(Screens.Main)
                is EnterPasswordScreenEffect.ShowToast -> showToast(snackbarHostState, effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    // Enter с физической клавиатуры (web, десктоп) переводит к ключу, а не переносит строку
                    modifier = Modifier.onEnterKey { passwordFocus.requestFocus() },
                    singleLine = true,
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                    value = nameEditText.value,
                    onValueChange = {
                        nameEditText.value = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedLabelColor = TkGrey
                    ),
                    label = {
                        Text("Введите Фамилию ключника")
                    }
                )

                OutlinedTextField(
                    modifier = Modifier
                        .focusRequester(passwordFocus)
                        .onEnterKey(::tryLogin),
                    singleLine = true,
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                    value = passwordEditText.value,
                    onValueChange = {
                        passwordEditText.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { tryLogin() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedLabelColor = TkGrey
                    ),
                    label = {
                        Text("Введите ключ")
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = ::tryLogin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TkMain,
                        disabledContainerColor = TkMain.copy(alpha = 0.8f)
                    ),
                    enabled = nameEditText.value.isNotEmpty() && !state.value.isLoading,
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                ) {
                    if (state.value.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TkWhite, strokeWidth = 2.dp)
                    } else {
                        Text(
                            "Войти"
                        )
                    }
                }

            }
        }
    }
}

/** Enter и Enter на цифровом блоке: срабатывает на нажатие, событие дальше в поле не идёт. */
private fun Modifier.onEnterKey(action: () -> Unit): Modifier = onPreviewKeyEvent { event ->
    val isEnter = event.key == Key.Enter || event.key == Key.NumPadEnter
    if (isEnter && event.type == KeyEventType.KeyDown) action()
    isEnter
}