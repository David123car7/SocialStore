package com.ipca.socialstore

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.ipca.socialstore.Work.StockWorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SocialStoreApp : Application(), Configuration.Provider { // 1. Implementa a interface

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // 2. O Hilt/WorkManager chama esta propriedade automaticamente
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .build()

    override fun onCreate() {
        super.onCreate()

        // 3. Agora o agendamento irá usar a factory injetada acima
        val stockWorkManager = StockWorkManager(this)
        stockWorkManager.notificationExpirationDate()
    }
}