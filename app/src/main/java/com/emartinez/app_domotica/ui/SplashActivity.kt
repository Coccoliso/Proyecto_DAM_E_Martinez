package com.emartinez.app_domotica.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.ui.settings.SettingsActivity.Companion.PREFS_NAME

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica si ya hay un correo electrónico guardado en SharedPreferences
        val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = settings.getString("email", null)
        val password = settings.getString("password", null)

        if (email != null && password != null) {
            // Inicia HomeAssistantActivity
            val intent = Intent(this, HomeAssistantActivity::class.java)
            startActivity(intent)
        } else {
            // Inicia LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Finaliza SplashActivity
        finish()
    }
}