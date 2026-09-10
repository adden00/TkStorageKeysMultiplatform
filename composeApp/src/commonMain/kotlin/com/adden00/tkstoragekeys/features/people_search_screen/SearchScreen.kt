package com.adden00.tkstoragekeys.features.people_search_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.WAREHOUSE_ID
import com.adden00.tkstoragekeys.data.model.isOnStorage
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.PeopleSearchScreenEffect
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.PeopleSearchScreenEvent
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.isBusy
import com.adden00.tkstoragekeys.features.users_search.UsersResultList
import com.adden00.tkstoragekeys.features.users_search.UsersSearchField
import com.adden00.tkstoragekeys.features.users_search.UsersSearchViewModel
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGreen
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkLightBlue
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import com.adden00.tkstoragekeys.theme.TkYellow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_back
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_ok
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_return
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_search
import tkstoragekeysmultiplatform.composeapp.generated.resources.storage

private const val TAB_EQUIP = 0
private const val TAB_PEOPLE = 1

@Composable
fun SearchScreen(
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by rememberSaveable { mutableIntStateOf(TAB_EQUIP) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = {
                        navigator.pop()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "back"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TabRow(
                    modifier = Modifier.weight(1f),
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = TkMain,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = TkMain
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == TAB_EQUIP,
                        onClick = { selectedTab = TAB_EQUIP },
                        text = { Text("Снаряжение") }
                    )
                    Tab(
                        selected = selectedTab == TAB_PEOPLE,
                        onClick = { selectedTab = TAB_PEOPLE },
                        text = { Text("Люди") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                TAB_EQUIP -> EquipSearchContent(navigator, snackbarHostState)
                TAB_PEOPLE -> PeopleSearchContent(navigator)
            }
        }
    }
}

@Composable
private fun ColumnScope.PeopleSearchContent(navigator: Navigator) {
    val viewModel: UsersSearchViewModel = koinViewModel(key = "users_search_tab")
    val state = viewModel.viewState.collectAsState()

    UsersSearchField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingHorizontal),
        query = state.value.query,
        isLoading = state.value.isLoading,
        onQueryChange = viewModel::onQueryChange
    )
    Spacer(modifier = Modifier.height(8.dp))
    UsersResultList(
        modifier = Modifier.fillMaxWidth().weight(1f),
        state = state.value,
        onUserClick = { user -> navigator.push(Screens.PersonDetails(user.id)) }
    )
}

@Composable
private fun ColumnScope.EquipSearchContent(
    navigator: Navigator,
    snackbarHostState: SnackbarHostState,
) {
    val viewModel: PeopleSearchViewModel = koinViewModel()
    val storageString = stringResource(Res.string.storage)
    val state = viewModel.viewState.collectAsState()

    LaunchedEffect("side effects") {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is PeopleSearchScreenEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingHorizontal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp)
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                        viewModel.obtainEvent(PeopleSearchScreenEvent.Search(state.value.enteredSearchText))
                        true
                    } else false
                },
            shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
            value = state.value.enteredSearchText,
            onValueChange = {
                viewModel.obtainEvent(PeopleSearchScreenEvent.OnSearchTextChanged(it))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedLabelColor = TkGrey
            ),
            label = {
                Text("название, место, и т.д.")
            },
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.obtainEvent(PeopleSearchScreenEvent.Search(state.value.enteredSearchText))
                }
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = TkMain,
                contentColor = TkWhite,
                disabledContainerColor = TkMain.copy(alpha = 0.8f)
            ),
            enabled = state.value.enteredSearchText.isNotEmpty() && !state.value.isBusy(),
            onClick = {
                viewModel.obtainEvent(PeopleSearchScreenEvent.Search(state.value.enteredSearchText))
            }) {
            if (state.value.isSearching) {
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = TkWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = "search"
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (state.value.currentEquipList.isNotEmpty()) {
        LazyColumn {
            items(state.value.currentEquipList) { item ->
                EquipItemLayout(
                    item = item,
                    isLoading = state.value.isReturning,
                    enabled = !state.value.isBusy(),
                    onReturnButtonClick = {
                        viewModel.obtainEvent(
                            PeopleSearchScreenEvent.ReturnItem(item.copy(location = storageString, event = "", locationUserId = WAREHOUSE_ID))
                        )
                    },
                    onItemClick = {
                        navigator.push(Screens.Reception(startItem = item))
                    }
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text("введите запрос")
        }
    }
}

@Composable
fun EquipItemLayout(
    item: EquipItem,
    isLoading: Boolean,
    enabled: Boolean,
    onReturnButtonClick: () -> Unit,
    onItemClick: () -> Unit = {},
) {
    val onStorage = item.isOnStorage()

    ElevatedCard(
        modifier = Modifier.clickable { onItemClick() }.padding(horizontal = Dimens.PaddingHorizontal, vertical = Dimens.PaddingSmall).fillMaxWidth(),
        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
        colors = CardDefaults.cardColors(
            containerColor = TkLightBlue
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.PaddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.id,
                style = TextStyle(fontSize = 22.sp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(0.5f)) {
                Text(item.name)
                Text(
                    item.category,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                )
                Text(
                    item.location,
                    style = TextStyle(
                        color = if (onStorage) TkGreen else TkYellow,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
            Spacer(Modifier.width(4.dp))
            OutlinedIconButton(
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (onStorage) TkLightBlue else TkMain,
                    contentColor = if (onStorage) TkGreen else TkWhite,
                    disabledContainerColor = TkMain.copy(alpha = 0.8f)
                ),
                enabled = enabled,
                onClick = {
                    if (!onStorage) {
                        onReturnButtonClick.invoke()
                    }
                }) {
                when {
                    isLoading -> {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = TkWhite,
                            strokeWidth = 2.dp
                        )
                    }
                    onStorage -> {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_ok),
                            contentDescription = "search"
                        )
                    }
                    else -> {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_return),
                            contentDescription = "search"
                        )
                    }
                }
            }
        }
    }
}
