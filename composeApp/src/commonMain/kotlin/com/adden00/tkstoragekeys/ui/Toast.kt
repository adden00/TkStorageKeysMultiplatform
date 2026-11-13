package com.adden00.tkstoragekeys.ui

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.showToast(host: SnackbarHostState, message: String) {
    host.currentSnackbarData?.dismiss()
    launch { host.showSnackbar(message) }
}
