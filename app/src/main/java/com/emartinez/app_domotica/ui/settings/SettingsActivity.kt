package com.emartinez.app_domotica.ui.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.controller.InitPrefs
import com.emartinez.app_domotica.databinding.ActivitySettingsBinding
import com.emartinez.app_domotica.ui.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * `SettingsActivity` es una actividad que proporciona la interfaz de usuario para la configuración de la aplicación.
 * Permite al usuario activar el modo oscuro, actualizar el token de acceso, actualizar la URL y cerrar sesión.
 *
 * @property binding El objeto de enlace que da acceso a las vistas en el diseño.
 * @property accessToken El token de acceso utilizado para la autenticación.
 * @property firstTime Una bandera booleana para verificar si es la primera vez que se cargan las configuraciones.
 * @property isLogin Una bandera booleana para verificar si el usuario ha iniciado sesión.
 */
class SettingsActivity : AppCompatActivity() {

    companion object {
        const val KEY_DARK_MODE = "key_dark_mode"
        const val KEY_TOKEN = "key_token"
        const val KEY_URL = "key_url"
        const val PREFS_NAME = "com.emartinez.app_domotica"
        const val IS_LOGIN = "is_login"
    }

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var accessToken: String
    private var firstTime = true
    private var isLogin = false

    /**
     * Método que se llama al crear la actividad. Inicializa la interfaz de usuario y establece los listeners de los eventos.
     */
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

        if (InitPrefs.url.isBlank() || (!URLUtil.isHttpUrl(InitPrefs.url) && !URLUtil.isHttpsUrl(
                InitPrefs.url
            ))
        ) {
            InitPrefs.url = "http://homeassistant.local:8123/"
            CoroutineScope(Dispatchers.IO).launch {
                savePreferences(KEY_URL, InitPrefs.url)
            }
        }

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isLogin = sharedPref.getBoolean(IS_LOGIN, false)

        initUi()
    }

    /**
     * Método que se llama cuando se selecciona un elemento del menú de opciones.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Método para inicializar la interfaz de usuario. Establece los listeners de los eventos de los elementos de la interfaz de usuario.
     */
    private fun initUi() {
        binding.swDarkMode.setOnCheckedChangeListener { _, value ->
            if (value) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
            CoroutineScope(Dispatchers.IO).launch {
                saveDarkModeOption(value)
            }
        }
        binding.llTtoken.setOnClickListener {
            dialogToken()
        }
        binding.llUrl.setOnClickListener {
            dialogUrl()
        }
        binding.llLogout.setOnClickListener {
            Log.d("Logout", "Logout button clicked") // Agrega este registro de depuración
            LoginActivity.logout(this)
        }
    }


    /**
     * Método para mostrar un diálogo que permite al usuario introducir un nuevo token de acceso.
     */
    private fun dialogToken() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_token)
        Log.d("Token", "Token: $accessToken")


        val btnGuardarToken: Button = dialog.findViewById(R.id.btnGuardarToken)
        val etTokenUser: EditText = dialog.findViewById(R.id.etTokenUser)

        btnGuardarToken.setOnClickListener {
            val inputToken = etTokenUser.text.toString()
            if (inputToken.isNotEmpty()) {
                accessToken = inputToken
                CoroutineScope(Dispatchers.IO).launch {
                    savePreferences(KEY_TOKEN, accessToken)
                    InitPrefs.token = accessToken
                }
                Log.d("Token", "Token: $accessToken")
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    /**
     * Método para mostrar un diálogo que permite al usuario introducir una nueva URL.
     */
    private fun dialogUrl() {
        val dialog = Dialog(this)
        var url: String = InitPrefs.url
        dialog.setContentView(R.layout.dialog_url)
        Log.d("URL", "URL: $url")
        val btnGuardarUrl: Button = dialog.findViewById(R.id.btnGuardarUrl)
        val etNewUrl: EditText = dialog.findViewById(R.id.etNewUrl)

        btnGuardarUrl.setOnClickListener {
            val inputUrl = etNewUrl.text.toString()
            if (inputUrl.isNotEmpty()) {
                url = inputUrl
                CoroutineScope(Dispatchers.IO).launch {
                    savePreferences(KEY_URL, url)
                    InitPrefs.url = url
                }
                Log.d("URL", "URL: $url")
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    /**
     * Método para habilitar el modo oscuro en la aplicación.
     */
    private fun enableDarkMode() {
        // Habilitar modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    /**
     * Método para deshabilitar el modo oscuro en la aplicación.
     */
    private fun disableDarkMode() {
        // Deshabilitar modo oscuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
    }

    /**
     * Método para guardar una preferencia en el almacenamiento de preferencias compartidas.
     */
    private fun savePreferences(key: String, value: String) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    /**
     * Método para guardar la opción del modo oscuro en el almacenamiento de preferencias compartidas.
     */
    private fun saveDarkModeOption(value: Boolean) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(KEY_DARK_MODE, value)
            apply()
        }
    }
}