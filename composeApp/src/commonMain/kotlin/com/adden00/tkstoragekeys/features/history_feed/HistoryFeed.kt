package com.adden00.tkstoragekeys.features.history_feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adden00.tkstoragekeys.data.model.ItemHistoryEntry
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGreen
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkLightBlue
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkRed
import com.adden00.tkstoragekeys.theme.TkWhite
import com.adden00.tkstoragekeys.theme.TkYellow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private const val DIFF = "-->"
private const val ACTION_ADDED = "ДОБАВЛЕНО"
private val STORAGES = setOf("склад", "новый склад")

fun LazyListScope.historyFeed(
    state: HistoryFeedState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onEntryClick: (itemId: String) -> Unit,
    emptyText: String = "История пуста",
) {
    historyRows(state.entries, onEntryClick = onEntryClick)

    item {
        when {
            state.error != null -> Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.error,
                    style = TextStyle(fontSize = 14.sp, color = TkRed, textAlign = TextAlign.Center),
                )
                TextButton(onClick = onRetry) { Text("Повторить", color = TkMain) }
            }

            state.isLoading -> Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TkMain, strokeWidth = 2.dp)
            }

            state.hasMore -> {
                LaunchedEffect(state.entries.size) { onLoadMore() }
                Spacer(modifier = Modifier.height(48.dp))
            }

            state.entries.isEmpty() -> Text(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                text = emptyText,
                style = TextStyle(fontSize = 14.sp, fontStyle = FontStyle.Italic, color = TkGrey, textAlign = TextAlign.Center),
            )
        }
    }
}

fun LazyListScope.historyRows(
    entries: List<ItemHistoryEntry>,
    showItem: Boolean = true,
    onEntryClick: ((itemId: String) -> Unit)? = null,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val days = mutableListOf<MutableList<IndexedValue<ItemHistoryEntry>>>()
    entries.withIndex().forEach { entry ->
        val last = days.lastOrNull()
        if (last != null && last.first().value.day() == entry.value.day()) last += entry else days += mutableListOf(entry)
    }

    days.forEach { day ->
        val first = day.first()
        stickyHeader(key = "history_day_${first.value.day()}_${first.value.key(first.index)}") {
            HistoryDayHeader(first.value.day().toDayTitle(today))
        }
        items(day, key = { it.value.key(it.index) }) { (_, entry) ->
            HistoryEntryRow(
                entry = entry,
                showItem = showItem,
                onClick = onEntryClick?.let { { it(entry.id.after()) } },
            )
            HorizontalDivider(modifier = Modifier.padding(start = TIME_WIDTH), color = TkLightBlue)
        }
    }
}

private fun ItemHistoryEntry.key(index: Int) = "history_${mongoId.ifBlank { "i$index" }}"

private val TIME_WIDTH = 56.dp
private val NUMBER_WIDTH = 52.dp

@Composable
private fun HistoryDayHeader(title: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(TkLightBlue)
            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 6.dp),
        text = title,
        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TkMain),
    )
}

@Composable
fun HistoryEntryRow(entry: ItemHistoryEntry, showItem: Boolean = true, onClick: (() -> Unit)? = null) {
    val kind = entry.kind()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TkWhite)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(end = Dimens.PaddingHorizontal, top = 8.dp, bottom = 8.dp),
    ) {
        Text(
            modifier = Modifier.width(TIME_WIDTH).padding(start = Dimens.PaddingHorizontal),
            text = entry.timestamp.substringAfter(' ', ""),
            style = TextStyle(fontSize = 13.sp, color = TkGrey),
        )
        if (showItem) {
            Text(
                modifier = Modifier.width(NUMBER_WIDTH),
                text = entry.id.pretty(),
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                maxLines = 2,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            if (showItem) {
                Text(
                    text = entry.name.after().ifBlank { entry.category.after() },
                    style = TextStyle(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = kind.color, fontWeight = FontWeight.SemiBold)) {
                        append(kind.title ?: entry.action.lowercase().replaceFirstChar { it.uppercase() })
                    }
                    val what = entry.whatHappened(kind)
                    if (what.isNotBlank()) append("  $what")
                },
                style = TextStyle(fontSize = 13.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val details = listOfNotNull(
                entry.relevantEvent(kind)?.let { "Мероприятие: $it" },
                entry.keyholderName.takeIf { it.isNotBlank() }?.let { "ключник $it" },
            ).joinToString(" · ")
            if (details.isNotBlank()) {
                Text(
                    text = details.replaceFirstChar { it.uppercase() },
                    style = TextStyle(fontSize = 12.sp, color = TkGrey),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun ItemHistoryEntry.whatHappened(kind: HistoryKind): String {
    val location = when {
        location.isChanged() -> location.pretty()
        kind == HistoryKind.ADDED -> location
        else -> ""
    }
    val changes = changedFields()
    return when {
        location.isNotBlank() && changes.isNotEmpty() -> "$location; изменено: ${changes.joinToString(", ")}"
        location.isNotBlank() -> location
        changes.isNotEmpty() -> changes.joinToString(", ")
        else -> ""
    }
}

private fun ItemHistoryEntry.relevantEvent(kind: HistoryKind): String? {
    val event = event.after()
    val matters = event.isNotBlank() && kind != HistoryKind.RETURNED &&
            (this.event.isChanged() || kind == HistoryKind.ISSUED || kind == HistoryKind.MOVED || kind == HistoryKind.ADDED)
    return event.takeIf { matters }
}

private fun ItemHistoryEntry.day(): String = timestamp.substringBefore(' ')

private val MONTHS = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

private fun String.toDayTitle(today: LocalDate): String {
    val parts = split('.').mapNotNull { it.toIntOrNull() }
    if (parts.size != 3 || parts[1] !in 1..12) return this
    val (d, m, y) = parts
    val date = runCatching { LocalDate(y, m, d) }.getOrNull() ?: return this
    val text = "$d ${MONTHS[m - 1]}" + if (y != today.year) " $y" else ""
    return when (date) {
        today -> "Сегодня, $text"
        today.minus(1, DateTimeUnit.DAY) -> "Вчера, $text"
        else -> text
    }
}

enum class HistoryKind(val title: String?, val color: Color) {
    ADDED("Добавлено", TkGreen),
    ISSUED("Выдано", TkYellow),
    RETURNED("Возвращено", TkGreen),
    MOVED("Передано", TkYellow),
    OTHER(null, TkMain),
}

fun ItemHistoryEntry.kind(): HistoryKind = when {
    action == ACTION_ADDED -> HistoryKind.ADDED
    !location.isChanged() -> HistoryKind.OTHER
    location.before().isStorage() && !location.after().isStorage() -> HistoryKind.ISSUED
    location.after().isStorage() -> HistoryKind.RETURNED
    else -> HistoryKind.MOVED
}

private fun ItemHistoryEntry.changedFields(): List<String> = listOf(
    "категория" to category,
    "производитель" to brand,
    "название" to name,
    "цвет" to color,
    "масса" to weigh,
    "состояние" to quality,
    "примечания" to info,
).filter { (_, value) -> value.isChanged() }.map { it.first }

private fun String.isChanged() = contains(DIFF)
private fun String.before() = substringBefore(DIFF).trim()
private fun String.after() = substringAfterLast(DIFF).trim()
private fun String.pretty() = split(DIFF).joinToString(" → ") { it.trim().ifBlank { "—" } }
private fun String.isStorage() = trim().lowercase() in STORAGES
