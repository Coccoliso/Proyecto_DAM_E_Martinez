package com.emartinez.app_domotica.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.controller.InitPrefs
import com.emartinez.app_domotica.databinding.ActivityLoginBinding
import com.emartinez.app_domotica.ui.settings.SettingsActivity
import com.emartinez.app_domotica.ui.settings.SettingsActivity.Companion.PREFS_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * `LoginActivity` es una actividad que proporciona la interfaz de usuario para el inicio de sesión en la aplicación.
 *
 * @property binding El objeto de enlace que da acceso a las vistas en el diseño.
 * @property auth La instancia de FirebaseAuth utilizada para la autenticación.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    /**
     * `LoginActivity` es una actividad que proporciona la interfaz de usuario para el inicio de sesión en la aplicación.
     *
     * @property binding El objeto de enlace que da acceso a las vistas en el diseño.
     * @property auth La instancia de FirebaseAuth utilizada para la autenticación.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()

    }

    /**
     * Método para verificar si los campos de correo electrónico y contraseña están vacíos.
     *
     * @param email El correo electrónico ingresado por el usuario.
     * @param password La contraseña ingresada por el usuario.
     * @return Verdadero si ambos campos están llenos, falso en caso contrario.
     */
    private fun checkEmpty(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    /**
     * Método para inicializar la interfaz de usuario. Establece los listeners de los eventos de los elementos de la interfaz de usuario.
     */
    private fun initUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            if (checkEmpty(email, password)) {
                login(email, password)
            }
        }

        binding.btnLoginGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Método para iniciar sesión en la aplicación con un correo electrónico y una contraseña.
     *
     * @param email El correo electrónico ingresado por el usuario.
     * @param password La contraseña ingresada por el usuario.
     */
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val editor = settings.edit()
                editor.putString("email", email)
                editor.putString("password", password)
                editor.apply()

                val intent = Intent(this, HomeAssistantActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        /**
         * Método para cerrar la sesión del usuario y volver a la pantalla de inicio de sesión.
         *
         * @param context El contexto desde el cual se llama a este método.
         */
        fun logout(context: Context) {
            val settings =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = settings.edit()
            editor.putBoolean(SettingsActivity.IS_LOGIN, false)
            editor.apply()

            CoroutineScope(Dispatchers.IO).launch {
                InitPrefs.token = ""
            }

            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

}