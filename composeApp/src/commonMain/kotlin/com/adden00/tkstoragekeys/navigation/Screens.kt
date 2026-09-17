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
import com.adden00.tkstoragekeys.features.item_history_screen.ItemHistoryScreen
import com.adden00.tkstoragekeys.features.location_cleanup.LocationCleanupScreen
import com.adden00.tkstoragekeys.features.people_search_screen.SearchScreen
import com.adden00.tkstoragekeys.features.person_details_screen.PersonDetailsScreen
import com.adden00.tkstoragekeys.features.reception_screen.ReceptionScreen
import com.adden00.tkstoragekeys.features.tutorial_screen.TutorialScreen
import kotlin.jvm.Transient

object Screens {

    object EnterPassword : Screen {
        @Composable
        override fun Content() {
            EnterPasswordScreen()
        }
    }

    data class Reception(
        @Transient
        val startItem: EquipItem? = null,
    ) : Screen {
        /**
         * Стартовая вещь отдаётся экрану один раз на экземпляр. Флаг живёт в самом Screen:
         * новое открытие вещи — новый экземпляр, возврат назад — тот же. Сохранённое состояние
         * и вьюмодель для этого не годятся: в web экраны вещи делят и то и другое.
         */
        @Transient
        private var startItemTaken = false

        @Composable
        override fun Content() {
            ReceptionScreen(
                startItem = startItem,
                takeStartItem = {
                    if (startItemTaken) null else startItem.also { startItemTaken = true }
                }
            )
        }
    }

    object Search : Screen {
        @Composable
        override fun Content() {
            SearchScreen()
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

    object LocationCleanup : Screen {
        @Composable
        override fun Content() {
            LocationCleanupScreen()
        }
    }

    object Tutorial : Screen {
        @Composable
        override fun Content() {
            TutorialScreen()
        }
    }

    data class ItemHistory(val itemId: String) : Screen {
        @Composable
        override fun Content() {
            ItemHistoryScreen(itemId = itemId)
        }
    }

    data class PersonDetails(val userId: String) : Screen {
        @Composable
        override fun Content() {
            PersonDetailsScreen(userId = userId)
        }
    }
}


@Composable
fun rememberNavigationResultExtension(): VoyagerResultExtension {
    val navigator = LocalNavigator.currentOrThrow

    return remember {
        NavigatorLifecycleStore.get(navigator) {
            VoyagerResultExtension()
        }
    }
}

class VoyagerResultExtension(
) : NavigatorDisposable {
    private val results = mutableStateMapOf<String, Any?>()

    override fun onDispose(navigator: Navigator) {
        // not used
    }

    fun setResult(screenKey: String, result: Any?) {
        results[screenKey] = result
    }

    @Composable
    fun <T> getResult(screenKey: String): State<T?> {
        @Suppress("UNCHECKED_CAST")
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