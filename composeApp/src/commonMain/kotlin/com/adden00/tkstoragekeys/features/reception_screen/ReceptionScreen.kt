package com.adden00.tkstoragekeys.features.reception_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.data.local.AppSettings
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.ExportFormat
import com.adden00.tkstoragekeys.data.model.Quality
import com.adden00.tkstoragekeys.data.model.isOnStorage
import com.adden00.tkstoragekeys.features.reception_screen.mvi.ReceptionScreenEffect
import com.adden00.tkstoragekeys.features.reception_screen.mvi.ReceptionScreenEvent
import com.adden00.tkstoragekeys.features.reception_screen.mvi.ReceptionScreenState
import com.adden00.tkstoragekeys.features.reception_screen.mvi.UpdateType
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.navigation.VoyagerResultExtension
import com.adden00.tkstoragekeys.navigation.rememberNavigationResultExtension
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGreen
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkRed
import com.adden00.tkstoragekeys.theme.TkWhite
import com.adden00.tkstoragekeys.theme.TkYellow
import com.adden00.tkstoragekeys.utils.DateUtils
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.edit
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_back
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_log_out
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_menu
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_people_search
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_search
import tkstoragekeysmultiplatform.composeapp.generated.resources.storage

@Composable
fun ReceptionScreen(
    navigator: Navigator = LocalNavigator.currentOrThrow,
    navigatorExtension: VoyagerResultExtension = rememberNavigationResultExtension(),
    resultItem: State<EquipItem?> = navigatorExtension.getResult<EquipItem>("KEY"),
    appSettings: AppSettings = koinInject(),
    startItem: EquipItem? = null,
) {
    val viewModel: ReceptionViewModel = koinViewModel()

    val storageString = stringResource(Res.string.storage)

    val fromSearch = startItem != null

    val snackbarHostState = remember { SnackbarHostState() }

    val state = viewModel.viewState.collectAsState()

    val fileSaverLauncher = rememberFileSaverLauncher { /* no action needed after save */ }

    LaunchedEffect("side effects") {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is ReceptionScreenEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ReceptionScreenEffect.SaveFile -> {
                    fileSaverLauncher.launch(
                        bytes = effect.bytes,
                        baseName = "Клубное снаряжение_${DateUtils.getCurrentDateTimeForFileName()}",
                        extension = effect.extension
                    )
                }
            }
        }
    }

    LaunchedEffect("initial value") {
        resultItem.value?.let { item ->
            viewModel.obtainEvent(ReceptionScreenEvent.SetItem(item))
            viewModel.obtainEvent(ReceptionScreenEvent.ShowUpdatedItem(item.id))
        }
        startItem?.let { viewModel.obtainEvent(ReceptionScreenEvent.SetItem(it)) }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!fromSearch) {
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = TkMain,
                    onClick = {
                        navigator.push(
                            Screens.AddNewEquip(
                                startItem = EquipItem(
                                    location = storageString
                                )
                            )
                        )
                    }) {
                    Text("+", style = TextStyle(fontSize = 24.sp))
                }
            }
        }
    ) { innerPadding ->
        if (state.value.isNumberNoxExistsShown && !state.value.notFoundedId.isNullOrEmpty()) {
            Dialog(onDismissRequest = {
                viewModel.obtainEvent(ReceptionScreenEvent.DismissNotExistsDialog)
            }) {
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    color = TkWhite
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        state.value.notFoundedId?.let { id ->
                            Text("$id не найдено! добавить?")
                            Spacer(modifier = Modifier.height(32.dp))

                            Row {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TkRed
                                    ),
                                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                                    onClick = {
                                        viewModel.obtainEvent(ReceptionScreenEvent.DismissNotExistsDialog)
                                    }
                                ) {
                                    Text("Закрыть")
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TkGreen
                                    ),
                                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                                    onClick = {
                                        viewModel.obtainEvent(ReceptionScreenEvent.DismissNotExistsDialog)
                                        navigator.push(
                                            Screens.AddNewEquip(
                                                startItem = EquipItem(
                                                    id = id,
                                                    location = storageString
                                                )
                                            )
                                        )
                                    }) {
                                    Text("Добавить")
                                }
                            }
                        }
                    }
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.PaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (fromSearch) {
                    OutlinedIconButton(onClick = { navigator.pop() }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "back"
                        )
                    }
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Выдача снаряжения",
                        style = TextStyle(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                } else {
                    OutlinedIconButton(
                        onClick = {
                            appSettings.keyHolderName = ""
                            appSettings.inventoryMode = false
                            navigator.replace(Screens.EnterPassword)
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_log_out),
                            contentDescription = "back"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Поиск и выдача снаряжения",
                        style = TextStyle(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val menuExpanded = remember { mutableStateOf(false) }
                    Box {
                        OutlinedIconButton(onClick = { menuExpanded.value = true }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(Res.drawable.ic_menu),
                                contentDescription = "menu"
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded.value,
                            onDismissRequest = { menuExpanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Памятка") },
                                onClick = {
                                    menuExpanded.value = false
                                    navigator.push(Screens.Tutorial)
                                }
                            )
                            DropdownMenuItem(
                                enabled = state.value.exportingFormat == null,
                                text = { Text("Экспорт CSV") },
                                trailingIcon = if (state.value.exportingFormat == ExportFormat.CSV) {
                                    {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else null,
                                onClick = {
                                    menuExpanded.value = false
                                    viewModel.obtainEvent(ReceptionScreenEvent.Export(ExportFormat.CSV))
                                }
                            )
                            DropdownMenuItem(
                                enabled = state.value.exportingFormat == null,
                                text = { Text("Экспорт XLS") },
                                trailingIcon = if (state.value.exportingFormat == ExportFormat.XLS) {
                                    {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else null,
                                onClick = {
                                    menuExpanded.value = false
                                    viewModel.obtainEvent(ReceptionScreenEvent.Export(ExportFormat.XLS))
                                }
                            )
                            DropdownMenuItem(
                                enabled = !state.value.isExportingToSheets,
                                text = { Text("Экспорт в Google Sheets") },
                                trailingIcon = if (state.value.isExportingToSheets) {
                                    {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else null,
                                onClick = {
                                    menuExpanded.value = false
                                    viewModel.obtainEvent(ReceptionScreenEvent.ExportToSheets)
                                }
                            )
                        }
                    }
                }
            }

            if (!fromSearch) {
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
                            .padding(bottom = 8.dp)
                            .onPreviewKeyEvent { event ->
                                if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                                    viewModel.obtainEvent(ReceptionScreenEvent.GetInfo(state.value.enteredSearchText))
                                    true
                                } else false
                            },
                        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                        value = state.value.enteredSearchText,
                        onValueChange = {
                            viewModel.obtainEvent(ReceptionScreenEvent.OnSearchTextChanged(it))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Search
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedLabelColor = TkGrey
                        ),
                        label = {
                            Text("Номер")
                        },
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.obtainEvent(ReceptionScreenEvent.GetInfo(state.value.enteredSearchText))
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
                            viewModel.obtainEvent(ReceptionScreenEvent.GetInfo(state.value.enteredSearchText))
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

                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = TkMain,
                            contentColor = TkWhite,
                            disabledContainerColor = TkMain.copy(alpha = 0.8f)
                        ),
                        onClick = {
                            navigator.push(
                                Screens.Search
                            )
                        }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_people_search),
                            contentDescription = "search"
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {

                state.value.currentEquipItem?.let { equipItem ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingHorizontal),
                        text = equipItem.id,
                        style = TextStyle(fontSize = 22.sp)
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingHorizontal), text = equipItem.name
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingHorizontal), text = "Производитель: ${equipItem.brand.ifEmpty { "неизвестно" }}"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingHorizontal),
                    ) {
                        Text(
                            text = "Состояние: ",
                        )
                        Text(
                            text = equipItem.quality?.value.orEmpty(),
                            style = TextStyle(
                                color = when (state.value.currentEquipItem?.quality) {
                                    Quality.BEST -> TkGreen
                                    Quality.GOOD -> TkGrey
                                    Quality.MEDIUM -> TkYellow
                                    Quality.TO_WRITE_OFF -> TkRed
                                    Quality.WRITE_OFF -> TkRed
                                    null -> TkGreen
                                },
                                fontSize = 18.sp,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingHorizontal),
                    ) {
                        Text(
                            text = "Местоположение: ",
                        )
                        Text(
                            text = equipItem.location,
                            style = TextStyle(
                                color = when (state.value.currentEquipItem?.location) {
                                    storageString -> TkGreen
                                    else -> TkYellow
                                },
                                fontSize = 20.sp,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    }

                    if (equipItem.event.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.PaddingHorizontal), text = "мероприятие: ${equipItem.event}"
                        )
                    }

                    if (equipItem.info.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.PaddingHorizontal),
                            style = TextStyle(fontStyle = FontStyle.Italic),
                            text = "примечания: ${equipItem.info}"
                        )
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val target = if (appSettings.inventoryMode)
                                    equipItem.copy(location = storageString, event = "") else equipItem
                                navigator.push(Screens.AddNewEquip(editingItemId = equipItem.id, startItem = target))
                            }
                            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 8.dp),
                        style = TextStyle(fontSize = 16.sp, fontStyle = FontStyle.Italic, color = TkMain),
                        text = stringResource(Res.string.edit)
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.push(Screens.ItemHistory(itemId = equipItem.id))
                            }
                            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 4.dp),
                        style = TextStyle(fontSize = 16.sp, fontStyle = FontStyle.Italic, color = TkMain),
                        text = "История"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(16.dp))

                if (appSettings.inventoryMode) {
                    Button(
                        onClick = {
                            state.value.currentEquipItem?.let { equipItem ->
                                viewModel.obtainEvent(
                                    ReceptionScreenEvent.UpdateInfo(
                                        equipItem.id,
                                        equipItem.copy(
                                            location = storageString,
                                            event = "",
                                            date = DateUtils.getCurrentDate()
                                        ),
                                        updateType = UpdateType.MOVING_NO_NEW_STORAGE
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TkMain,
                            disabledContainerColor = TkMain.copy(alpha = 0.8f)
                        ),
                        enabled = state.value.currentEquipItem?.let {
                            !state.value.isBusy() && !it.isOnStorage()
                        } ?: false
                    ) {
                        Text(
                            text = "Инвентаризовать"
                        )
                        if (state.value.isMovingToNewStorage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = TkWhite,
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                } else if (state.value.currentEquipItem != null) {
                    Text(
                        "Выдача снаряжения",
                        style = TextStyle(
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingHorizontal),
                        value = state.value.enteredLocationText,
                        onValueChange = {
                            viewModel.obtainEvent(ReceptionScreenEvent.OnLocationTextChanged(it))
                        },
                        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedLabelColor = TkGrey
                        ),
                        label = {
                            Text("ФИО")
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingHorizontal),
                        value = state.value.enteredEventText,

                        onValueChange = {
                            viewModel.obtainEvent(ReceptionScreenEvent.OnEventTextChanged(it))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedLabelColor = TkGrey
                        ),
                        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                        label = {
                            Text("Мероприятие")
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(
                            onClick = {
                                state.value.currentEquipItem?.let { equipItem ->
                                    viewModel.obtainEvent(
                                        ReceptionScreenEvent.UpdateInfo(
                                            equipItem.id,
                                            equipItem.copy(
                                                location = storageString,
                                                event = "",
                                                date = DateUtils.getCurrentDate()
                                            ),
                                            updateType = UpdateType.MOVING_NO_NEW_STORAGE
                                        )
                                    )
                                }
                            },
                            shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TkMain,
                                disabledContainerColor = TkMain.copy(alpha = 0.8f)
                            ),
                            enabled = state.value.currentEquipItem?.let {
                                !state.value.isBusy() && !it.isOnStorage()
                            } ?: false
                        ) {
                            Text("на склад")
                            if (state.value.isMovingToNewStorage) {
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = TkWhite,
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                state.value.currentEquipItem?.let { equipItem ->
                                    viewModel.obtainEvent(
                                        ReceptionScreenEvent.UpdateInfo(
                                            equipItem.id,
                                            equipItem.copy(
                                                location = state.value.enteredLocationText,
                                                event = state.value.enteredEventText,
                                                date = DateUtils.getCurrentDate()
                                            ),
                                            updateType = UpdateType.MOVING
                                        )
                                    )
                                }
                            },
                            shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                            enabled = state.value.currentEquipItem != null && state.value.enteredLocationText.isNotEmpty() && !state.value.isBusy(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TkYellow,
                                disabledContainerColor = TkYellow.copy(alpha = 0.8f)
                            )
                        ) {
                            Text("Выдать")
                            if (state.value.isMovingToPerson) {
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = TkWhite,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))


                if (state.value.error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(state.value.error)
                }
            }
        }
    }
}

private fun ReceptionScreenState.isBusy(): Boolean =
    isMovingToPerson || isReturning || isMovingToNewStorage || isSearching
