package com.emartinez.app_domotica.api


data class Entity(
    val entity_id: String,
    val state: String,
    val attributes: Map<String, Any>? = null
)

