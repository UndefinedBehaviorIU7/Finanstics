package com.ub.finanstics.fcm

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

const val WORKER_TAG = "FinansticsWorker"

class FinansticsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return Result.success()
    }

    companion object {
        private const val TAG = WORKER_TAG
    }
}
