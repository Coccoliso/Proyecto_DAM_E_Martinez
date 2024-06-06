package com.emartinez.app_domotica.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.ui.settings.SettingsActivity.Companion.PREFS_NAME

/**
 * `SplashActivity` es una actividad que muestra una pantalla de inicio (splash screen) al usuario.
 * Verifica si ya hay un correo electrónico y una contraseña guardados en SharedPreferences.
 * Si los hay, inicia `HomeAssistantActivity`. Si no los hay, inicia `LoginActivity`.
 *
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    /**
     * Método que se llama al crear la actividad. Verifica si ya hay un correo electrónico y una contraseña guardados en SharedPreferences.
     * Si los hay, inicia `HomeAssistantActivity`. Si no los hay, inicia `LoginActivity`.
     * Finalmente, finaliza `SplashActivity`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = settings.getString("email", null)
        val password = settings.getString("password", null)

        if (email != null && password != null) {
            val intent = Intent(this, HomeAssistantActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}