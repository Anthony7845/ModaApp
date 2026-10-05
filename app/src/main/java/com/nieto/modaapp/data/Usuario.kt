package com.nieto.modaapp.data

data class Usuario(
    val id: Int = 0,
    val usuario: String,
    val clave: String,
    val rol: String,
    val telefono: String? = null
)