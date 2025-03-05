package com.adden00.tkstoragekeys

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.GoogleSheetAppTheme

@Composable
internal fun App() = GoogleSheetAppTheme {
    GoogleSheetAppTheme {
        Navigator(
            screen = Screens.Reception
        )
    }
}
