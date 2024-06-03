package com.emartinez.app_domotica.settings

import android.app.Dialog
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.api.Auth
import com.emartinez.app_domotica.databinding.ActivitySettingsBinding
import com.emartinez.app_domotica.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val KEY_DARK_MODE = "key_dark_mode"
        const val KEY_TOKEN = "key_token"
        const val PREFS_NAME = "com.emartinez.app_domotica"
        const val IS_LOGIN = "is_login"
    }

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var accessToken: String
    private var firstTime = true
    private var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.navigationIcon?.setTint(getColor(R.color.title_text))

        val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isLogin = settings.getBoolean(IS_LOGIN, false)

        CoroutineScope(Dispatchers.IO).launch {
            getSettings().filter{firstTime}.collect {settingsModel ->
                if(settingsModel != null){
                    runOnUiThread {
                        binding.swDarkMode.isChecked = settingsModel.darkMode
                        accessToken = settingsModel.token
                        Auth.token = accessToken
                        firstTime = !firstTime
                    }
                }
            }
        }
        initUi()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUi() {
        binding.swDarkMode.setOnCheckedChangeListener { _, value ->

            if(value) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
            CoroutineScope(Dispatchers.IO).launch {
                saveOption(KEY_DARK_MODE, value)
            }
        }
        binding.llTtoken.setOnClickListener {
            dialog()
        }
        binding.llLogout.setOnClickListener {
            Log.d("Logout", "Logout button clicked") // Agrega este registro de depuración
            LoginActivity.logout(this)
        }
    }

    private suspend fun saveToken(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    private suspend fun saveOption(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value

        }
    }

    suspend fun clearDataStore() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun dialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_token)

        val btnGuardarToken: Button = dialog.findViewById(R.id.btnGuardarToken)
        val etTokenUser: EditText = dialog.findViewById(R.id.etTokenUser)

        btnGuardarToken.setOnClickListener {
            val inputToken = etTokenUser.text.toString()
            if (inputToken.isNotEmpty()) {
                accessToken = inputToken
                CoroutineScope(Dispatchers.IO).launch {
                    saveToken(KEY_TOKEN, accessToken)
                    Auth.token = accessToken // Establece el token en la clase Auth
                }
                saveToken(accessToken) // Guarda el token en SharedPreferences
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun getSettings(): Flow<SettingsModel?> {
        return dataStore.data.map{preferences ->
            SettingsModel(
                preferences[booleanPreferencesKey(KEY_DARK_MODE)] ?: false,
                preferences[stringPreferencesKey(KEY_TOKEN)] ?: ""
            )
        }
    }

    private fun enableDarkMode() {
        // Habilitar modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun disableDarkMode() {
        // Deshabilitar modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
    }

    private fun saveToken(token: String) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(KEY_TOKEN, token)
            apply()
        }
    }

//    fun saveLoginState(isLogin: Boolean) {
//        val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        val editor = settings.edit()
//        editor.putBoolean(IS_LOGIN, isLogin)
//        editor.apply()
//    }
}