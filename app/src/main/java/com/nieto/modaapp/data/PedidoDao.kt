package com.nieto.modaapp.data

import android.content.ContentValues
import android.content.Context
import com.nieto.modaapp.model.ItemCarrito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun registrar(idCliente: Long, items: List<ItemCarrito>): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val total = items.sumOf { it.ropa.precio * it.cantidad }

            val cvPedido = ContentValues().apply {
                put(DBHelper.COL_PEDIDO_ID_CLIENTE, idCliente)
                put(DBHelper.COL_PEDIDO_FECHA, fechaActual)
                put(DBHelper.COL_PEDIDO_TOTAL, total)
                put(DBHelper.COL_PEDIDO_ESTADO, "PENDIENTE")
            }
            val pedidoId = db.insert(DBHelper.TABLE_PEDIDO, null, cvPedido)
            if (pedidoId == -1L) {
                throw Exception("Error al insertar pedido")
            }

            for (item in items) {
                val subtotal = item.ropa.precio * item.cantidad
                val cvDetalle = ContentValues().apply {
                    put(DBHelper.COL_DETALLE_ID_PEDIDO, pedidoId)
                    put(DBHelper.COL_DETALLE_ID_ROPA, item.ropa.id)
                    put(DBHelper.COL_DETALLE_CANTIDAD, item.cantidad)
                    put(DBHelper.COL_DETALLE_PRECIO_UNIT, item.ropa.precio)
                    put(DBHelper.COL_DETALLE_SUBTOTAL, subtotal)
                }
                val detalleId = db.insert(DBHelper.TABLE_DETALLE_PEDIDO, null, cvDetalle)
                if (detalleId == -1L) {
                    throw Exception("Error al insertar detalle de pedido")
                }

                val nuevoStock = item.ropa.cantidad - item.cantidad
                val cvStock = ContentValues().apply {
                    put(DBHelper.COL_ROPA_CANTIDAD, nuevoStock)
                }
                db.update(DBHelper.TABLE_ROPA, cvStock, "${DBHelper.COL_ROPA_ID} = ?", arrayOf(item.ropa.id.toString()))
            }

            db.setTransactionSuccessful()
            return pedidoId
        } catch (e: Exception) {
            e.printStackTrace()
            return -1L
        } finally {
            db.endTransaction()
        }
    }
}