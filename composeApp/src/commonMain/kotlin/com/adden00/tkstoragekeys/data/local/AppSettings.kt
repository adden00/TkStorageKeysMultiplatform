package com.adden00.tkstoragekeys.data.local

import com.russhwolf.settings.Settings

class AppSettings(private val settings: Settings) {

    companion object {
        private const val KEYHOLDER_NAME = "KEYHOLDER_NAME"
        private const val INVENTORISATION_MODE = "INVENTORISATION_MODE"
        private const val TEST_ENV = "TEST_ENV"
    }

    var isTestEnv: Boolean
        get() = settings.getBoolean(TEST_ENV, false)
        set(value) {
            settings.putBoolean(TEST_ENV, value)
        }

    var keyHolderName: String
        get() = settings.getString(KEYHOLDER_NAME, "")
        set(value) {
            settings.putString(KEYHOLDER_NAME, value)
        }

    var inventoryMode: Boolean?
        get() = settings.getBooleanOrNull(INVENTORISATION_MODE)
        set(value) {
            value?.let {
                settings.putBoolean(INVENTORISATION_MODE, value)
            }
        }
}
