package com.adden00.tkstoragekeys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.adden00.tkstoragekeys.data.auth.SessionManager
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.GoogleSheetAppTheme
import org.koin.compose.koinInject

@Composable
internal fun App() = GoogleSheetAppTheme {
    GoogleSheetAppTheme {
        val sessionManager: SessionManager = koinInject()
        Navigator(
            screen = Screens.EnterPassword
        ) { navigator ->
            LaunchedEffect(sessionManager) {
                sessionManager.expired.collect { navigator.replaceAll(Screens.EnterPassword) }
            }
            CurrentScreen()
        }
    }
}
