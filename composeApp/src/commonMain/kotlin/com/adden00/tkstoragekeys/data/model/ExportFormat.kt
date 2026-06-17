package com.adden00.tkstoragekeys.data.model

enum class ExportFormat(val path: String, val extension: String) {
    CSV(path = "csv", extension = "csv"),
    XLS(path = "xls", extension = "xlsx"),
}
