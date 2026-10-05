package com.nieto.modaapp.model

data class Cliente(
    val id: Int = 0,
    val telefono: String,
    val nombres: String,
    val apellidos: String,
    val fechaRegistro: String? = null
)