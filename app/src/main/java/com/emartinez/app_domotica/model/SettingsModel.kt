package com.emartinez.app_domotica.model

/**
 * Clase `SettingsModel`, representa la configuración del usuario en la aplicación.
 *
 * @property darkMode Indica si el modo oscuro está activado o no.
 * @property token El token de autenticación del usuario.
 */
data class SettingsModel(var darkMode: Boolean, var token: String)
