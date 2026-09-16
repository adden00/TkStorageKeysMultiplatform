package com.adden00.tkstoragekeys.features.users_search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adden00.tkstoragekeys.Constants
import com.adden00.tkstoragekeys.data.model.ClubUserShort
import com.adden00.tkstoragekeys.features.users_search.mvi.UsersSearchState
import com.adden00.tkstoragekeys.theme.Dimens
import com.adden00.tkstoragekeys.theme.TkGrey
import com.adden00.tkstoragekeys.theme.TkLightBlue

/** Выдача поиска людей со всеми состояниями: пусто, никого не нашлось, список с подсказкой внизу. */
@Composable
fun UsersResultList(
    state: UsersSearchState,
    onUserClick: (ClubUserShort) -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable (ClubUserShort) -> Unit)? = null,
    notFoundContent: @Composable () -> Unit = { CenteredHint("Никого не нашлось") },
) {
    Box(modifier = modifier) {
        when {
            state.users.isNotEmpty() -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.users, key = { it.id }) { user ->
                        UserRow(user = user, onClick = { onUserClick(user) }, trailing = trailing)
                    }
                    state.hint?.let { hint ->
                        item { CenteredHint(hint) }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            state.isSearched -> notFoundContent()

            state.hint != null -> CenteredHint(state.hint)

            state.query.isBlank() -> CenteredHint("Введите ФИО или телеграм")
        }
    }
}

@Composable
fun UserRow(
    user: ClubUserShort,
    onClick: () -> Unit,
    trailing: (@Composable (ClubUserShort) -> Unit)? = null,
) {
    ElevatedCard(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = Dimens.PaddingHorizontal, vertical = 4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(Constants.CORNERS_RADIUS),
        colors = CardDefaults.cardColors(containerColor = TkLightBlue)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.fullName, style = TextStyle(fontSize = 16.sp))
                // ФИО не уникально: дата рождения и телеграм помогают отличить тёзок
                val details = listOf(user.birthDate, user.telegram).filter { it.isNotBlank() }
                if (details.isNotEmpty()) {
                    Text(
                        details.joinToString(" · "),
                        style = TextStyle(fontSize = 13.sp, color = TkGrey)
                    )
                }
            }
            if (trailing != null) {
                Spacer(modifier = Modifier.width(4.dp))
                trailing(user)
            }
        }
    }
}

@Composable
private fun CenteredHint(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        text = text,
        style = TextStyle(fontSize = 14.sp, fontStyle = FontStyle.Italic, color = TkGrey, textAlign = TextAlign.Center)
    )
}
