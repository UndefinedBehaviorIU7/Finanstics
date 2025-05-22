package com.ub.finanstics.fcm

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class FinansticsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d(TAG, "Performing long running task in scheduled job")
        // TODO(developer): add long running task here.
        return Result.success()
    }

    companion object {
        private val TAG = "FinansticsWorker"
    }
}
