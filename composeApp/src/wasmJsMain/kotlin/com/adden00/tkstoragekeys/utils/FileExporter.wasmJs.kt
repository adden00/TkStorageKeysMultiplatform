package com.adden00.tkstoragekeys.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download
import kotlinx.coroutines.launch

@Composable
actual fun rememberFileExporter(): FileExporter {
    val scope = rememberCoroutineScope()
    return remember {
        FileExporter { bytes, baseName, extension ->
            scope.launch { FileKit.download(bytes = bytes, fileName = "$baseName.$extension") }
        }
    }
}
