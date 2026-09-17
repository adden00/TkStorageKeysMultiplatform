package com.adden00.tkstoragekeys.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.launch

@Composable
actual fun rememberFileExporter(): FileExporter {
    val scope = rememberCoroutineScope()
    val pending = remember { arrayOfNulls<ByteArray>(1) }
    val launcher = rememberFileSaverLauncher(dialogSettings = FileKitDialogSettings.createDefault()) { file ->
        val bytes = pending[0]
        pending[0] = null
        if (file != null && bytes != null) {
            scope.launch { file.write(bytes) }
        }
    }
    return remember(launcher) {
        FileExporter { bytes, baseName, extension ->
            pending[0] = bytes
            launcher.launch(suggestedName = baseName, defaultExtension = extension)
        }
    }
}
