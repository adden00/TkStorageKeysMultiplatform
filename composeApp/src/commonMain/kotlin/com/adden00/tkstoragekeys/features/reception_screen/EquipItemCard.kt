package com.adden00.tkstoragekeys.features.reception_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adden00.tkstoragekeys.data.model.EquipItem
import com.adden00.tkstoragekeys.data.model.Quality
import com.adden00.tkstoragekeys.data.model.isOnStorage
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGreen
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkLightBlue
import com.adden00.tkstoragekeys.theme.TkMain
import com.adden00.tkstoragekeys.theme.TkRed
import com.adden00.tkstoragekeys.theme.TkWhite
import com.adden00.tkstoragekeys.theme.TkYellow
import org.jetbrains.compose.resources.stringResource
import tkstoragekeysmultiplatform.composeapp.generated.resources.Res
import tkstoragekeysmultiplatform.composeapp.generated.resources.edit

private const val EMPTY = "—"
private val WEIGHT_GRAMS = Regex("""\d+([.,]\d+)?""")

/**
 * Карточка найденной вещи: всё, что есть в таблице, без захода в редактирование.
 * Пустые характеристики показываются прочерком — так видно, что поле не заполнено,
 * а не что его забыли вывести.
 */
@Composable
fun EquipItemCard(
    item: EquipItem,
    onPersonClick: (userId: String) -> Unit,
    onEditClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingHorizontal),
        shape = RoundedCornerShape(12.dp),
        color = TkWhite,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // номер и категория — то, по чему вещь сверяют с той, что в руках
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "№ ${item.id}",
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                )
                if (item.category.isNotBlank()) {
                    Text(
                        modifier = Modifier
                            .background(TkLightBlue, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        text = item.category,
                        style = TextStyle(fontSize = 13.sp, color = TkMain)
                    )
                }
            }

            Text(
                text = item.name.ifBlank { EMPTY },
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)
            )

            Row {
                SpecCell("Производитель", item.brand)
                SpecCell("Цвет", item.color)
            }
            Row {
                SpecCell("Вес", item.weigh.toWeightText())
                SpecCell("Состояние", item.quality?.value.orEmpty(), item.quality.color())
            }

            HorizontalDivider(color = TkLightBlue)

            LocationBlock(item, onPersonClick)

            if (item.info.isNotBlank()) {
                Labeled("Примечания") {
                    Text(item.info, style = TextStyle(fontSize = 15.sp, fontStyle = FontStyle.Italic))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = if (item.date.isNotBlank()) "Изменено ${item.date}" else "",
                    style = TextStyle(fontSize = 12.sp, color = TkGrey)
                )
                TextButton(onClick = onHistoryClick) {
                    Text("История", color = TkMain)
                }
                TextButton(onClick = onEditClick) {
                    Text(stringResource(Res.string.edit).replaceFirstChar { it.uppercase() }, color = TkMain)
                }
            }
        }
    }
}

@Composable
private fun LocationBlock(item: EquipItem, onPersonClick: (String) -> Unit) {
    val personId = item.locationUserId
    val onStorage = item.isOnStorage()
    Labeled("Местоположение") {
        Text(
            modifier = if (!personId.isNullOrEmpty() && !onStorage) {
                Modifier.clickable { onPersonClick(personId) }
            } else Modifier,
            text = item.location.ifBlank { EMPTY },
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = when {
                    item.location.isBlank() -> TkGrey
                    onStorage -> TkGreen
                    else -> TkYellow
                },
                // подчёркнуто только то, что открывается: ФИО человека из справочника
                textDecoration = if (!personId.isNullOrEmpty() && !onStorage) TextDecoration.Underline else null
            )
        )
    }
    if (item.event.isNotBlank()) {
        Labeled("Мероприятие") {
            Text(item.event.trim(), style = TextStyle(fontSize = 15.sp))
        }
    }
}

@Composable
private fun RowScope.SpecCell(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Labeled(label, Modifier.weight(1f).padding(end = 8.dp)) {
        Text(
            text = value.trim().ifBlank { EMPTY },
            style = TextStyle(
                fontSize = 15.sp,
                color = if (value.isBlank()) TkGrey else valueColor
            )
        )
    }
}

@Composable
private fun Labeled(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Text(label, style = TextStyle(fontSize = 12.sp, color = TkGrey))
        Spacer(modifier = Modifier.height(1.dp))
        content()
    }
}

/** В таблице вес почти всегда граммы числом ("240"); записи вида "3.5кг" показываем как есть. */
private fun String.toWeightText(): String {
    val value = trim()
    return if (WEIGHT_GRAMS.matches(value)) "$value г" else value
}

private fun Quality?.color(): Color = when (this) {
    Quality.BEST -> TkGreen
    Quality.GOOD -> Color.Unspecified
    Quality.MEDIUM -> TkYellow
    Quality.TO_WRITE_OFF, Quality.WRITE_OFF -> TkRed
    null -> Color.Unspecified
}
