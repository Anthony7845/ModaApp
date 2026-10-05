package com.nieto.modaapp.model

data class DetallePedidoItem(
    val idRopa: Int,
    val modeloRopa: String,
    val tallaRopa: String,
    val colorRopa: String?,
    val fotoRopa: String,
    val cantidad: Int,
    val precioUnit: Double,
    val subtotal: Double
)