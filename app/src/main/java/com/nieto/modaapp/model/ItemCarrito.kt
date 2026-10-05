package com.nieto.modaapp.model

import com.nieto.modaapp.data.Ropa

data class ItemCarrito(
    val ropa: Ropa,
    var cantidad: Int
)