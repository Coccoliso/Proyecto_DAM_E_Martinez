package com.emartinez.app_domotica.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.emartinez.app_domotica.HomeAssistantActivity
import com.emartinez.app_domotica.R
import com.emartinez.app_domotica.api.Auth
import com.emartinez.app_domotica.databinding.ActivityLoginBinding
import com.emartinez.app_domotica.register.RegisterActivity
import com.emartinez.app_domotica.settings.SettingsActivity
import com.emartinez.app_domotica.settings.SettingsActivity.Companion.IS_LOGIN
import com.emartinez.app_domotica.settings.SettingsActivity.Companion.PREFS_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

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

    private fun checkEmpty(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    private fun initUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            if(checkEmpty(email, password)){
                login(email, password)
            }
        }

        binding.btnLoginGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){

                val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val editor = settings.edit()
                editor.putString("email", email)
                editor.putString("password", password)
                editor.apply()

                val intent = Intent(this, HomeAssistantActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun logout(context: Context) {
            val settings = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val editor = settings.edit()
            editor.putBoolean(SettingsActivity.IS_LOGIN, false)
            editor.apply()

            CoroutineScope(Dispatchers.IO).launch {
                (context as SettingsActivity).clearDataStore()
                Auth.token = "" // Limpia el token en la clase Auth
            }

            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

}