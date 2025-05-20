package com.ub.finanstics

import android.app.Application
import com.google.firebase.FirebaseApp
import com.vk.id.VKID
import java.util.Locale

class FinansticsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        VKID.init(this)
        VKID.instance.setLocale(Locale("ru"))
        FirebaseApp.initializeApp(this)
    }
}
