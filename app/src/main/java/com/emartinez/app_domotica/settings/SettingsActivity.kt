package com.emartinez.app_domotica.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.databinding.ActivitySettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val KEY_DARK_MODE = "key_dark_mode"
        const val KEY_TOKEN = "key_token"
    }

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var accessToken: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar) // Asegúrate de que este es el id correcto
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initUi()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Este ID representa el botón de volver atrás
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    suspend fun getTokenFromDataStore(): String {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(KEY_TOKEN)] ?: ""
    }

    private fun initUi() {
        binding.swDarkMode.setOnCheckedChangeListener { _, value ->
            CoroutineScope(Dispatchers.IO).launch {
                saveOption(KEY_DARK_MODE, value)
            }
        }

        binding.llTtoken.setOnClickListener {
            dialog()
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
                }
                dialog.dismiss()
            }
        }

        dialog.show()

    }
}