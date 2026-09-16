package com.adden00.tkstoragekeys.data.local

import com.russhwolf.settings.Settings

class AppSettings(private val settings: Settings) {

    companion object {
        private const val KEYHOLDER_NAME = "KEYHOLDER_NAME"
        private const val TEST_ENV = "TEST_ENV"

        /** Флаг режима инвентаризации из старых версий. Режим убран, ключ читается только миграцией. */
        private const val LEGACY_INVENTORY_MODE = "INVENTORISATION_MODE"
    }

    /**
     * Сессия, открытая ключом инвентаризации, не должна превращаться в обычную: без флага
     * автовход пустил бы такого пользователя к выдаче, хотя основной ключ он не вводил.
     * Стираем фамилию, чтобы он вошёл заново. Идемпотентно: после первого запуска ключа нет.
     */
    fun dropLegacyInventorySession() {
        if (settings.getBooleanOrNull(LEGACY_INVENTORY_MODE) == true) {
            settings.remove(KEYHOLDER_NAME)
        }
        settings.remove(LEGACY_INVENTORY_MODE)
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

}
