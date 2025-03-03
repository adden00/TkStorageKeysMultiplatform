package com.adden00.tkstoragekeys.utils

import io.ktor.client.plugins.logging.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    fun getCurrentDate(): String {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val formattedDate = currentDate.toString().split("T")[0].split("-").let {
            "${it[2]}.${it[1]}.${it[0]}"
        }
       return formattedDate
    }
}
