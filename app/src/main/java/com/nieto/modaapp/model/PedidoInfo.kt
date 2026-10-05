package com.nieto.modaapp.model

data class PedidoInfo(
    val id: Int,
    val idCliente: Int,
    val nombresCliente: String,
    val apellidosCliente: String,
    val telefonoCliente: String,
    val fecha: String,
    val total: Double,
    val estado: String,
    val fechaAtencion: String?
)