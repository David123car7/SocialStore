package com.ipca.socialstore

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.ipca.socialstore.Work.StockWorkManager
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SocialStoreApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .build()

    override fun onCreate() {
        super.onCreate()

        val stockWorkManager = StockWorkManager(this)
        stockWorkManager.notificationExpirationDate()
        PDFBoxResourceLoader.init(this)
    }
}