package com.adden00.tkstoragekeys.data.local

import com.russhwolf.settings.Settings

class AppSettings(private val settings: Settings) {

    companion object {
        private const val KEYHOLDER_NAME = "KEYHOLDER_NAME"
    }

    var keyHolderName: String
        get() = settings.getString(KEYHOLDER_NAME, "")
        set(value) {
            settings.putString(KEYHOLDER_NAME, value)
        }
}
