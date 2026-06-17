package com.adden00.tkstoragekeys.utils

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

    fun getCurrentDateTimeForFileName(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val day = now.date.dayOfMonth.toString().padStart(2, '0')
        val month = now.date.monthNumber.toString().padStart(2, '0')
        val year = (now.date.year % 100).toString().padStart(2, '0')
        val hour = now.hour.toString().padStart(2, '0')
        val minute = now.minute.toString().padStart(2, '0')
        return "$day.$month.${year}_$hour.$minute"
    }
}
