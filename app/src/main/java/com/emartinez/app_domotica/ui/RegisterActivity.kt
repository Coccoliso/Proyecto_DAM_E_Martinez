package com.emartinez.app_domotica.ui

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
import com.emartinez.app_domotica.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * `RegisterActivity` es una actividad que proporciona la interfaz de usuario para el registro en la aplicación.
 *
 * @property binding El objeto de enlace que da acceso a las vistas en el diseño.
 * @property auth La instancia de FirebaseAuth utilizada para la autenticación.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    /**
     * Método que se llama al crear la actividad. Inicializa la interfaz de usuario y establece los listeners de los eventos.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUi()
    }

    /**
     * Método para verificar si los campos de correo electrónico y contraseña están vacíos.
     *
     * @param email El correo electrónico ingresado por el usuario.
     * @param password La contraseña ingresada por el usuario.
     * @param repeatPassword La confirmación de la contraseña ingresada por el usuario.
     * @return Verdadero si todos los campos están llenos, falso en caso contrario.
     */
    private fun checkEmpty(email: String, password: String, repeatPassword: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()
    }

    /**
     * Método para inicializar la interfaz de usuario. Establece los listeners de los eventos de los elementos de la interfaz de usuario.
     */
    private fun initUi() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val repeatPassword = binding.etRegisterRepeatPassword.text.toString()
            if (binding.etRegisterToken.text != null) {
                InitPrefs.token = binding.etRegisterToken.text.toString()
            }

            if (password == repeatPassword && checkEmpty(email, password, repeatPassword)) {
                register(email, password)
            }
        }

        binding.btnRegisterGoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Método para registrar un nuevo usuario en la aplicación con un correo electrónico y una contraseña.
     *
     * @param email El correo electrónico ingresado por el usuario.
     * @param password La contraseña ingresada por el usuario.
     */
    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, HomeAssistantActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext, "Register Failed!", Toast.LENGTH_LONG).show()
            }
        }
    }
}