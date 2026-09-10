package com.adden00.tkstoragekeys

import androidx.compose.ui.unit.dp

object Constants {
    val CORNERS_RADIUS = 15.dp
    const val VERSION_CODE = 3
    const val SPRING_BASE_URL = "https://tkstorrage-api.fly.dev/"

    // локальный бэкенд, включается ключом testenv при логине.
    // localhost вместо IP Мака: сеть изолирует Wi-Fi-клиентов от Мака на кабеле, поэтому
    // Android (и телефон по USB, и эмулятор) ходит через `adb reverse tcp:8080 tcp:8080`;
    // desktop и iOS-симулятор видят localhost Мака напрямую
    const val TEST_BASE_URL = "http://localhost:8080/"
}
