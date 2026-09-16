package com.example.cognigame

import android.app.Application
import com.example.cognigame.data.local.SettingsManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class CogniGameApp : Application() {
    lateinit var settingsManager: SettingsManager
        private set

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.getInstance().enableNetwork().addOnSuccessListener {
            println("Firebase connected successfully")
        }
    }
}
