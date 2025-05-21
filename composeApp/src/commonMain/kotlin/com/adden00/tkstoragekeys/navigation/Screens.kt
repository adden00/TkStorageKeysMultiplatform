package com.adden00.tkstoragekeys.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.lifecycle.NavigatorDisposable
import cafe.adriel.voyager.navigator.lifecycle.NavigatorLifecycleStore
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.features.add_equip_screen.NewEquipScreen
import com.adden00.tkstoragekeys.features.enter_password_screen.EnterPasswordScreen
import com.adden00.tkstoragekeys.features.people_search_screen.PeopleSearchScreen
import com.adden00.tkstoragekeys.features.reception_screen.ReceptionScreen
import com.adden00.tkstoragekeys.features.tutorial_screen.TutorialScreen

object Screens {

    object EnterPassword : Screen {
        @Composable
        override fun Content() {
            EnterPasswordScreen()
        }
    }

    object Reception : Screen {
        @Composable
        override fun Content() {
            ReceptionScreen()
        }
    }

    object PeopleSearch : Screen {
        @Composable
        override fun Content() {
            PeopleSearchScreen()
        }
    }

    data class AddNewEquip(
        val editingItemId: String = "",
        val startItem: EquipItem,
    ) : Screen {
        @Composable
        override fun Content() {
            NewEquipScreen(editingItemId, startItem)
        }
    }

    object Tutorial : Screen {
        @Composable
        override fun Content() {
            TutorialScreen()
        }
    }
}


@Composable
fun rememberNavigationResultExtension(): VoyagerResultExtension {
    val navigator = LocalNavigator.currentOrThrow

    return remember {
        NavigatorLifecycleStore.get(navigator) {
            VoyagerResultExtension(navigator)
        }
    }
}

class VoyagerResultExtension(
    private val navigator: Navigator
) : NavigatorDisposable {
    private val results = mutableStateMapOf<String, Any?>()

    override fun onDispose(navigator: Navigator) {
        // not used
    }

    fun setResult(screenKey: String, result: Any?) {
        results[screenKey] = result
    }

    fun popWithResult(result: Any? = null) {
        val currentScreen = navigator.lastItem
        results[currentScreen.key] = result
        navigator.pop()
    }

    fun clearResults() {
        results.clear()
    }

    @Composable
    fun <T> getResult(screenKey: String): State<T?> {
        val result = results[screenKey] as? T
        val resultState = remember(screenKey, result) {
            derivedStateOf {
                results.remove(screenKey)
                result
            }
        }
        return resultState
    }
}