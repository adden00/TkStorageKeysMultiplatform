package com.adden00.tkstoragekeys.features.users_search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_info
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_search

/**
 * Выбранное местоположение. [userId] — id записи справочника,
 * пустая строка — текст вписан вручную (сервер снимет привязку).
 */
data class LocationPick(val name: String, val userId: String)

/** Нередактируемое поле: по тапу открывает выбор человека. */
@Composable
fun LocationPickerField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
            colors = OutlinedTextFieldDefaults.colors(unfocusedLabelColor = TkGrey),
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null
                )
            }
        )
        // read-only поле само забирает тапы под фокус, поэтому ловим их поверх
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(Constants.CORNERS_RADIUS))
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun UsersSearchField(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier,
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
        colors = OutlinedTextFieldDefaults.colors(unfocusedLabelColor = TkGrey),
        label = { Text("ФИО или телеграм") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = TkMain, strokeWidth = 2.dp)
            }
        }
    )
}

/**
 * Шторка выбора человека из справочника. Если никого не нашлось — можно вписать текст вручную.
 * [onOpenDetails] == null прячет кнопку перехода в карточку человека.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonPickerSheet(
    onDismiss: () -> Unit,
    onPicked: (LocationPick) -> Unit,
    onOpenDetails: ((userId: String) -> Unit)? = null,
) {
    val viewModel: UsersSearchViewModel = koinViewModel(key = "person_picker")
    val state = viewModel.viewState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    var manualText by remember { mutableStateOf<String?>(null) }

    // шторка каждый раз открывается с чистым поиском
    LaunchedEffect(Unit) {
        viewModel.reset()
        runCatching { focusRequester.requestFocus() }
    }

    fun closeThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TkWhite,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .imePadding()
        ) {
            UsersSearchField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal)
                    .focusRequester(focusRequester),
                query = state.value.query,
                isLoading = state.value.isLoading,
                onQueryChange = viewModel::onQueryChange
            )
            Spacer(modifier = Modifier.height(8.dp))
            UsersResultList(
                modifier = Modifier.fillMaxWidth().weight(1f),
                state = state.value,
                onUserClick = { user -> closeThen { onPicked(LocationPick(name = user.fullName, userId = user.id)) } },
                trailing = onOpenDetails?.let { openDetails ->
                    { user ->
                        IconButton(onClick = { closeThen { openDetails(user.id) } }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(Res.drawable.ic_info),
                                contentDescription = "подробнее",
                                tint = TkMain
                            )
                        }
                    }
                },
                notFoundContent = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("В справочнике никого не нашлось", color = TkGrey)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { manualText = state.value.query.trim() },
                            shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                            colors = ButtonDefaults.buttonColors(containerColor = TkMain)
                        ) {
                            Text("Ввести вручную")
                        }
                    }
                }
            )
        }
    }

    manualText?.let { text ->
        AlertDialog(
            onDismissRequest = { manualText = null },
            containerColor = TkWhite,
            title = { Text("Местоположение") },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { manualText = it },
                    singleLine = true,
                    shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedLabelColor = TkGrey),
                    label = { Text("ФИО или место") }
                )
            },
            confirmButton = {
                TextButton(
                    enabled = text.isNotBlank(),
                    onClick = {
                        manualText = null
                        closeThen { onPicked(LocationPick(name = text.trim(), userId = "")) }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { manualText = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}
