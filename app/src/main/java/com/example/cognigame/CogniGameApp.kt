package com.example.cognigame

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class CogniGameApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.getInstance().enableNetwork().addOnSuccessListener {
            println("Firebase connected successfully")
        }
    }
}
