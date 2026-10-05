package com.nieto.modaapp.model

import com.nieto.modaapp.data.Ropa

object Carrito {
    private val items = mutableListOf<ItemCarrito>()

    fun obtenerItems(): List<ItemCarrito> = items

    fun agregar(ropa: Ropa, cantidad: Int) {
        val existente = items.find { it.ropa.id == ropa.id }
        if (existente != null) {
            existente.cantidad += cantidad
            if (existente.cantidad > ropa.cantidad) {
                existente.cantidad = ropa.cantidad
            }
        } else {
            items.add(ItemCarrito(ropa, cantidad))
        }
    }

    fun quitar(ropaId: Int) {
        items.removeAll { it.ropa.id == ropaId }
    }

    fun totalItemsCount(): Int {
        return items.sumOf { it.cantidad }
    }

    fun calcularTotal(): Double {
        return items.sumOf { it.ropa.precio * it.cantidad }
    }

    fun vaciar() {
        items.clear()
    }
}