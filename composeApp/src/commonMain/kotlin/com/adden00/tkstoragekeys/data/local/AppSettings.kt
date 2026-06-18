package com.adden00.tkstoragekeys.data.local

import com.russhwolf.settings.Settings

class AppSettings(private val settings: Settings) {

    companion object {
        private const val KEYHOLDER_NAME = "KEYHOLDER_NAME"
        private const val INVENTORY_MODE = "INVENTORY_MODE"
    }

    var keyHolderName: String
        get() = settings.getString(KEYHOLDER_NAME, "")
        set(value) {
            settings.putString(KEYHOLDER_NAME, value)
        }

    var inventoryMode: Boolean
        get() = settings.getBoolean(INVENTORY_MODE, false)
        set(value) {
            settings.putBoolean(INVENTORY_MODE, value)
        }
}
