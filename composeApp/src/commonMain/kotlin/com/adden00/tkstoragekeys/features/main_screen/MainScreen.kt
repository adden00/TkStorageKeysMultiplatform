package com.adden00.tkstoragekeys.features.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.adden00.tkstoragekeys.features.global_history_screen.GlobalHistoryScreen
import com.adden00.tkstoragekeys.features.location_cleanup.LocationCleanupScreen
import com.adden00.tkstoragekeys.features.people_search_screen.SearchScreen
import com.adden00.tkstoragekeys.features.reception_screen.ReceptionScreen
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkLightBlue
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_backpack
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_history
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_link
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_people_search

private val BAR_HEIGHT = 64.dp

private enum class MainTab(val title: String, val icon: DrawableResource) {
    RECEPTION("Выдача", Res.drawable.ic_backpack),
    SEARCH("Поиск", Res.drawable.ic_people_search),
    HISTORY("История", Res.drawable.ic_history),
    CLEANUP("Разбор", Res.drawable.ic_link),
}

@Composable
fun MainScreen() {
    var tab by rememberSaveable { mutableStateOf(MainTab.RECEPTION) }
    var historyRequests by rememberSaveable { mutableIntStateOf(0) }
    val tabStates = rememberSaveableStateHolder()

    NavigationBackHandler(
        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
        isBackEnabled = tab != MainTab.RECEPTION,
        onBackCompleted = { tab = MainTab.RECEPTION },
    )

    Scaffold(
        bottomBar = {
            val systemBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            NavigationBar(
                modifier = Modifier.height(BAR_HEIGHT + systemBottom),
                containerColor = TkWhite,
            ) {
                MainTab.entries.forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = {
                            if (item == MainTab.HISTORY) historyRequests++
                            tab = item
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(item.icon),
                                contentDescription = item.title,
                            )
                        },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TkMain,
                            selectedTextColor = TkMain,
                            indicatorColor = TkLightBlue,
                            unselectedIconColor = TkGrey,
                            unselectedTextColor = TkGrey,
                        ),
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
            tabStates.SaveableStateProvider(tab.name) {
                when (tab) {
                    MainTab.RECEPTION -> ReceptionScreen(startItem = null, takeStartItem = { null })
                    MainTab.SEARCH -> SearchScreen()
                    MainTab.HISTORY -> GlobalHistoryScreen(refreshRequest = historyRequests)
                    MainTab.CLEANUP -> LocationCleanupScreen()
                }
            }
        }
    }
}
