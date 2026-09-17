package com.adden00.tkstoragekeys.utils

import androidx.compose.runtime.Composable

/** Сохраняет выгрузку снаряжения в файл. */
fun interface FileExporter {
    fun save(bytes: ByteArray, baseName: String, extension: String)
}

/**
 * На Android, desktop и iOS открывает системный диалог сохранения,
 * в браузере сразу скачивает файл: диалога сохранения там нет.
 */
@Composable
expect fun rememberFileExporter(): FileExporter
