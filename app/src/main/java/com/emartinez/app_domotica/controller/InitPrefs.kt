package com.emartinez.app_domotica.controller

/**
 * La clase `Preferences` almacena la información de autenticación del usuario.
 * Esta información incluye el token de autenticación, la URL del servidor y la preferencia de modo oscuro.
 *
 * @property token El token de autenticación del usuario. Este token se utiliza para autenticar las solicitudes al servidor.
 * @property url La URL del servidor. Todas las solicitudes se envían a esta URL.
 * @property darkMode Un booleano que indica si el usuario ha habilitado el modo oscuro en la aplicación.
 */
object InitPrefs {
    var token: String = ""
    var url: String = "http://homeassistant.local:8123/"
    var darkMode: Boolean = false
}