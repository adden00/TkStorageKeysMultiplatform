package com.adden00.tkstoragekeys

import android.app.Application
import android.content.Context
import com.adden00.tkstoragekeys.di.initKoin

class TkStorageApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        appContext = this
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
