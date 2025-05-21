package com.adden00.tkstoragekeys.features.people_search_screen

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.PeopleSearchScreenEffect
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.PeopleSearchScreenEvent
import com.adden00.tkstoragekeys.features.people_search_screen.mvi.isBusy
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkDark
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
import tkstoragekeysmultiplatform.composeapp.generated.resources.new_storage

@Composable
fun PeopleSearchScreen(
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    val viewModel: PeopleSearchViewModel = koinViewModel()
    val newStorageString = stringResource(Res.string.new_storage)


    val snackbarHostState = remember { SnackbarHostState() }
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
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.PaddingHorizontal),
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

                Text(
                    text = "Поиск по местонахождению",
                    style = TextStyle(
                        fontSize = 18.sp,
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp),
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
                        Text("Местонахождение")
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.obtainEvent(PeopleSearchScreenEvent.GetInfo(state.value.enteredSearchText))
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
                        viewModel.obtainEvent(PeopleSearchScreenEvent.GetInfo(state.value.enteredSearchText))
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

            LazyColumn {
                items(state.value.currentEquipList) { item ->
                    EquipItemLayout(
                        item = item,
                        isLoading = state.value.isReturning,
                        enabled = !state.value.isBusy(),
                        onReturnButtonClick = {
                            viewModel.obtainEvent(PeopleSearchScreenEvent.ReturnItem(item.copy(location = newStorageString)))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EquipItemLayout(
    item: EquipItem,
    isLoading: Boolean,
    enabled: Boolean,
    onReturnButtonClick: () -> Unit
) {
    val newStorageString = stringResource(Res.string.new_storage)

    ElevatedCard(
        modifier = Modifier.padding(horizontal = Dimens.PaddingHorizontal, vertical = Dimens.PaddingSmall).fillMaxWidth(),
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
                        color = when (item.location) {
                            "склад" -> TkGreen
                            "новый склад" -> TkDark
                            else -> TkYellow
                        },
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
            Spacer(Modifier.width(4.dp))
            OutlinedIconButton(
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (item.location == newStorageString) TkLightBlue else TkMain,
                    contentColor = if (item.location == newStorageString) TkGreen else TkWhite,
                    disabledContainerColor = TkMain.copy(alpha = 0.8f)
                ),
                enabled = enabled,
                onClick = {
                    if (item.location != newStorageString) {
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
                    item.location == newStorageString -> {
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
