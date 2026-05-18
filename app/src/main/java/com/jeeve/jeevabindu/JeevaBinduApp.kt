package com.jeeve.jeevabindu

import android.app.Application
import com.jeeve.jeevabindu.data.JeevaBinduRepository
import com.jeeve.jeevabindu.data.local.AppDatabase
import com.jeeve.jeevabindu.fcm.AlertDispatchService
import com.jeeve.jeevabindu.notifications.BloodAlertNotifier

class JeevaBinduApp : Application() {
    lateinit var repository: JeevaBinduRepository
        private set

    lateinit var alertDispatch: AlertDispatchService
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        repository = JeevaBinduRepository(db)
        alertDispatch = AlertDispatchService(this, repository)
        BloodAlertNotifier.ensureChannel(this)
    }
}
