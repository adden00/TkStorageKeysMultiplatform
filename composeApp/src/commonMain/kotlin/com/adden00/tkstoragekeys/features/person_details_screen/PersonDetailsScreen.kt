package com.adden00.tkstoragekeys.features.person_details_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.adden00.tkstoragekeys.data.model.ClubUser
import com.adden00.tkstoragekeys.data.model.WAREHOUSE_ID
import com.adden00.tkstoragekeys.features.people_search_screen.EquipItemLayout
import com.adden00.tkstoragekeys.features.person_details_screen.mvi.PersonDetailsScreenEffect
import com.adden00.tkstoragekeys.features.person_details_screen.mvi.PersonDetailsScreenEvent
import com.adden00.tkstoragekeys.features.person_details_screen.mvi.isBusy
import com.adden00.tkstoragekeys.navigation.Screens
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkWhite
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.ic_back
import tkstoragekeysmultiplatform.composeapp.generated.resources.storage

@Composable
fun PersonDetailsScreen(
    userId: String,
    navigator: Navigator = LocalNavigator.currentOrThrow,
) {
    val viewModel: PersonDetailsViewModel = koinViewModel(key = "person_details_$userId")
    val state = viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val storageString = stringResource(Res.string.storage)

    // Voyager пересоздаёт контент при возврате на экран — список на руках обновится
    // после выдачи или возврата вещи из её карточки
    LaunchedEffect(userId) {
        viewModel.obtainEvent(PersonDetailsScreenEvent.Load(userId))
    }

    LaunchedEffect("side effects") {
        viewModel.viewEffect.collect { effect ->
            when (effect) {
                is PersonDetailsScreenEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingHorizontal),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedIconButton(onClick = { navigator.pop() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "back",
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = state.value.user?.fullName ?: "Карточка человека",
                    style = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center),
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            val user = state.value.user
            when {
                state.value.isLoading && user == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TkMain)
                    }
                }

                state.value.isNotFound -> {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Человек не найден. Возможно, справочник обновился — найдите его заново через поиск",
                            style = TextStyle(fontSize = 16.sp, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center),
                        )
                    }
                }

                user != null -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item { PersonInfoCard(user) }

                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Dimens.PaddingHorizontal, vertical = 8.dp),
                                text = "На руках: ${state.value.items.size}",
                                style = TextStyle(fontSize = 18.sp),
                            )
                        }

                        if (state.value.items.isEmpty()) {
                            item {
                                Text(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    text = "Ничего нет",
                                    style = TextStyle(fontSize = 14.sp, fontStyle = FontStyle.Italic, color = TkGrey, textAlign = TextAlign.Center),
                                )
                            }
                        }

                        items(state.value.items, key = { it.id }) { item ->
                            EquipItemLayout(
                                item = item,
                                isLoading = state.value.isReturning,
                                enabled = !state.value.isBusy(),
                                onReturnButtonClick = {
                                    viewModel.obtainEvent(
                                        PersonDetailsScreenEvent.ReturnItem(
                                            userId = userId,
                                            item = item.copy(location = storageString, event = "", locationUserId = WAREHOUSE_ID)
                                        )
                                    )
                                },
                                onItemClick = {
                                    navigator.push(Screens.Reception(startItem = item))
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

private enum class LinkType { NONE, PHONE, EMAIL, TELEGRAM, WEB }

private data class InfoField(val label: String, val value: String, val uri: String?)

private fun field(label: String, value: String, linkType: LinkType): InfoField? =
    value.takeIf { it.isNotBlank() }?.let { InfoField(label, it, it.toUri(linkType)) }

@Composable
private fun PersonInfoCard(user: ClubUser) {
    // анкету заполняли свободным текстом годами: любое поле, кроме ФИО, может быть пустым
    val rows = listOfNotNull(
        field("Дата рождения", user.birthDate, LinkType.NONE),
        field("Телефон", user.phone, LinkType.PHONE),
        field("Email", user.email, LinkType.EMAIL),
        // телеграм не указан — пробуем открыть чат по номеру телефона
        field("Telegram", user.telegram, LinkType.TELEGRAM)
            ?: telegramByPhoneUri(user.phone)?.let { InfoField("Telegram", "по номеру телефона", it) },
        field("VK", user.vk, LinkType.WEB),
        field("Год вступления", user.joinYear, LinkType.NONE),
        field("Набор", user.intake, LinkType.NONE),
        field("Турподготовка", user.tourTraining, LinkType.NONE),
        field("Разряд", listOf(user.rank, user.rankExtra).filter { it.isNotBlank() }.joinToString(" "), LinkType.NONE),
        field("Опыт", user.experience, LinkType.NONE),
    )

    if (rows.isEmpty()) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = TkWhite,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            rows.forEach { (label, value, uri) ->
                InfoRow(label = label, value = value, uri = uri)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, uri: String?) {
    val uriHandler = LocalUriHandler.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(
            modifier = Modifier.width(130.dp),
            text = label,
            style = TextStyle(fontSize = 14.sp, color = TkGrey),
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (uri != null) Modifier.clickable { runCatching { uriHandler.openUri(uri) } } else Modifier
                ),
            text = value,
            style = TextStyle(
                fontSize = 14.sp,
                color = if (uri != null) TkMain else TextStyle.Default.color,
                textDecoration = if (uri != null) TextDecoration.Underline else null,
            ),
        )
    }
}

private fun String.toUri(linkType: LinkType): String? {
    val value = trim()
    return when (linkType) {
        LinkType.NONE -> null
        LinkType.PHONE -> value.filter { it.isDigit() || it == '+' }.takeIf { it.length >= 5 }?.let { "tel:$it" }
        LinkType.EMAIL -> value.takeIf { '@' in it && ' ' !in it }?.let { "mailto:$it" }
        LinkType.TELEGRAM -> telegramUri(value)
        LinkType.WEB -> value.takeIf { it.startsWith("http://") || it.startsWith("https://") }
    }
}

private val TELEGRAM_NICK = Regex("""^@?([A-Za-z][A-Za-z0-9_]{3,31})$""")
private val URL_WITHOUT_SCHEME = Regex("""^(www\.)?([\w-]+\.)+[a-z]{2,}/\S*$""", RegexOption.IGNORE_CASE)
private val URL_IN_TEXT = Regex("""https?://\S+""", RegexOption.IGNORE_CASE)
private val MENTION_IN_TEXT = Regex("""(?:^|\s)@([A-Za-z][A-Za-z0-9_]{3,31})\b""")

/**
 * Колонку telegram в анкете заполняли как придётся: голый ник, @ник, ссылка (бывает и на VK, и на скайп),
 * ссылка без схемы или ник внутри фразы ("tg @nick"). Ссылку открываем как есть, из ника строим t.me.
 * Скайп-логины с подписью, номера телефонов и прочий текст ссылкой не делаем.
 */
private fun telegramUri(value: String): String? {
    TELEGRAM_NICK.matchEntire(value)?.let { return "https://t.me/${it.groupValues[1]}" }
    if (URL_WITHOUT_SCHEME.matches(value)) return "https://$value"
    URL_IN_TEXT.find(value)?.let { return it.value }
    // "Inst- @nick" — это ник в инстаграме, а не в телеграме
    if (value.contains("inst", ignoreCase = true) && !value.contains("tg", ignoreCase = true)
        && !value.contains("telegram", ignoreCase = true) && !value.contains("телеграм", ignoreCase = true)
    ) return null
    MENTION_IN_TEXT.find(value)?.let { return "https://t.me/${it.groupValues[1]}" }
    return null
}

/**
 * t.me/+79161234567 открывает чат по номеру, если человек не закрыл это в приватности.
 * Номера в анкете записаны как попало: "8 916 714 13 73", "8(967)222-62-49", "+7 (966) ...", "916 ...".
 * Несколько номеров в одном поле или непонятный формат — ссылки нет.
 */
private fun telegramByPhoneUri(phone: String): String? {
    val digits = phone.filter { it.isDigit() }
    val international = when {
        digits.length == 11 && (digits.startsWith("8") || digits.startsWith("7")) -> "7" + digits.drop(1)
        digits.length == 10 && digits.startsWith("9") -> "7$digits"
        phone.trim().startsWith("+") && digits.length in 11..15 -> digits
        else -> return null
    }
    return "https://t.me/+$international"
}
