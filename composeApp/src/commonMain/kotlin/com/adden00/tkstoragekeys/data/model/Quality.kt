package com.adden00.tkstoragekeys.data.model

enum class Quality(val value: String) {
    BEST("Отличное"),
    GOOD("Хорошее"),
    MEDIUM("Среднее"),
    TO_WRITE_OFF("На списание"),
    WRITE_OFF("Списано")
}

fun String.extractQuality() =
    Quality.entries.find {
        it.value == this
    }
