package com.nieto.modaapp.data

data class Ropa(
    val id: Int = 0,
    val modelo: String,
    val idCategoria: Int,
    val nombreCategoria: String? = null,
    val talla: String,
    val marca: String? = null,
    val color: String? = null,
    val precio: Double,
    val cantidad: Int,
    val foto: String
)