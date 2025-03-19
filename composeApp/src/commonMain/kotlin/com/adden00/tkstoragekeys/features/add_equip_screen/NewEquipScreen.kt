package com.adden00.tkstoragekeys.features.add_equip_screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.common.Constants
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.features.add_equip_screen.mvi.NewEquipScreenEffect
import com.adden00.tkstoragekeys.features.add_equip_screen.mvi.NewEquipScreenEvent
import com.adden00.tkstoragekeys.navigation.VoyagerResultExtension
import com.adden00.tkstoragekeys.navigation.rememberNavigationResultExtension
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEquipScreen(
    editingItemId: String,
    startItemFilled: EquipItem,
    navigatorExtension: VoyagerResultExtension = rememberNavigationResultExtension(),
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {

    val viewModel: NewEquipViewModel = koinViewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.viewState.collectAsState()

    LaunchedEffect("side effects") {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is NewEquipScreenEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message, duration = SnackbarDuration.Short)
                }

                is NewEquipScreenEffect.NavigateBack -> {
                    navigatorExtension.setResult("KEY", effect.addedItem)
                    navigator.pop()
                }
            }
        }
    }

    LaunchedEffect("start state") {
        println("LaunchedEffect(\"start state\") {")
        println(startItemFilled.toString())
        viewModel.obtainEvent(NewEquipScreenEvent.FillStartFields(editingItemId = editingItemId, item = startItemFilled))
    }

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .imePadding(), snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedIconButton(
                    onClick = {
                        navigator.pop()
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "back"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                    modifier = Modifier
                        .weight(1f),
                    value = state.value.enteredItem.id,
                    onValueChange = {
                        viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(id = it)))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedLabelColor = TkGrey
                    ),
                    label = {
                        Text("Номер")
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.obtainEvent(NewEquipScreenEvent.GetFreeId)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TkMain,
                        disabledContainerColor = TkMain.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                    enabled = !state.value.isReceivingId
                ) {
                    Text("Новый номер")
                    if (state.value.isReceivingId) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = TkWhite,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            val categoryExpanded = remember { mutableStateOf(false) }
            val categories = listOf(
                "ИСС",
                "Железо",
                "Веревки",
                "Петли",
                "Каски",
                "Палатки",
                "Горелки",
                "Рюкзаки",
                "Посуда",
                "Мебель",
                "Спальные мешки",
                "Документы",
                "Инструмент",
                "Компасы",
                "Электронные приборы",
                "Прочее"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal)
            ) {
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded.value,
                    onExpandedChange = {
                        categoryExpanded.value = !categoryExpanded.value
                    }
                ) {
                    OutlinedTextField(
                        value = state.value.enteredItem.category,
                        onValueChange = {},
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedLabelColor = TkGrey
                        ),
                        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                        label = {
                            Text("Категория")
                        },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded.value) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded.value,
                        onDismissRequest = { categoryExpanded.value = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category) },
                                onClick = {
                                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(item = state.value.enteredItem.copy(category = category)))
                                    categoryExpanded.value = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                value = state.value.enteredItem.brand,
                onValueChange = {
                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(brand = it)))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = TkGrey
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                label = {
                    Text("Производитель")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                value = state.value.enteredItem.name,
                onValueChange = {
                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(name = it)))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = TkGrey
                ),
                label = {
                    Text("Название")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                value = state.value.enteredItem.color,
                onValueChange = {
                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(color = it)))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = TkGrey
                ),
                label = {
                    Text("цвет")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                value = state.value.enteredItem.weigh,
                onValueChange = {
                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(weigh = it)))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = TkGrey
                ),
                label = {
                    Text("Масса")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            val qualityExpanded = remember { mutableStateOf(false) }
            val qualities = listOf(
                "Отличное",
                "Хорошее",
                "Среднее",
                "На списание",
                "Списано",
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal)
            ) {
                ExposedDropdownMenuBox(
                    expanded = qualityExpanded.value,
                    onExpandedChange = {
                        qualityExpanded.value = !qualityExpanded.value
                    }
                ) {
                    OutlinedTextField(
                        value = state.value.enteredItem.quality,
                        onValueChange = {},
                        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedLabelColor = TkGrey
                        ),
                        label = {
                            Text("Состояние")
                        },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = qualityExpanded.value) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = qualityExpanded.value,
                        onDismissRequest = { qualityExpanded.value = false }
                    ) {
                        qualities.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(text = category) },
                                onClick = {
                                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(item = state.value.enteredItem.copy(quality = category)))
                                    qualityExpanded.value = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                value = state.value.enteredItem.location,
                onValueChange = {
                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(location = it)))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = TkGrey
                ),
                label = {
                    Text("Местоположение")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                value = state.value.enteredItem.event,
                onValueChange = {
                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(event = it)))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = TkGrey
                ),
                label = {
                    Text("Мероприятие")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                value = state.value.enteredItem.info,
                onValueChange = {
                    viewModel.obtainEvent(NewEquipScreenEvent.OnEnteredItemChange(state.value.enteredItem.copy(info = it)))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (state.value.updatingItemId == "")
                            viewModel.obtainEvent(NewEquipScreenEvent.AddEquipItem)
                        else
                            viewModel.obtainEvent(NewEquipScreenEvent.UpdateEquipItem(state.value.updatingItemId))
                    }
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedLabelColor = TkGrey
                ),
                label = {
                    Text("Примечания")
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (state.value.updatingItemId == "")
                        viewModel.obtainEvent(NewEquipScreenEvent.AddEquipItem)
                    else
                        viewModel.obtainEvent(NewEquipScreenEvent.UpdateEquipItem(state.value.updatingItemId))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TkMain,
                    disabledContainerColor = TkMain.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                enabled = state.value.enteredItem.id.isNotEmpty() && state.value.enteredItem.name.isNotEmpty() && !state.value.isAdding
            ) {
                Text(if (state.value.updatingItemId == "") "Добавить" else "изменить")
                if (state.value.isAdding) {
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
}
