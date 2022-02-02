package com.udacity.asteroidradar

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.udacity.asteroidradar.work.RefreshDataWorker
import java.util.concurrent.TimeUnit

class AsteroidRadarApp: Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        context = this
        delayedInit()
    }

    companion object {
        var context: Context? = null
            private set
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setRequiresDeviceIdle(true)
                    }
                }.build()

        val repeatingRequest =
                PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
        )
    }
}