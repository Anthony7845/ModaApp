package com.nieto.modaapp.data

import android.content.ContentValues
import android.content.Context
import com.nieto.modaapp.model.DetallePedidoItem
import com.nieto.modaapp.model.ItemCarrito
import com.nieto.modaapp.model.PedidoInfo
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

    fun listarPorEstado(estadoFiltro: String): List<PedidoInfo> {
        val lista = mutableListOf<PedidoInfo>()
        val db = dbHelper.readableDatabase

        val sql = """
            SELECT p.*, c.${DBHelper.COL_CLIENTE_NOMBRES} as nombres, c.${DBHelper.COL_CLIENTE_APELLIDOS} as apellidos, c.${DBHelper.COL_CLIENTE_TELEFONO} as telefono
            FROM ${DBHelper.TABLE_PEDIDO} p
            INNER JOIN ${DBHelper.TABLE_CLIENTE} c ON p.${DBHelper.COL_PEDIDO_ID_CLIENTE} = c.${DBHelper.COL_CLIENTE_ID}
            WHERE p.${DBHelper.COL_PEDIDO_ESTADO} = ?
            ORDER BY p.${DBHelper.COL_PEDIDO_ID} DESC
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(estadoFiltro))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_ID))
                val idCliente = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_ID_CLIENTE))
                val nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"))
                val apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_FECHA))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_TOTAL))
                val estado = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_ESTADO))
                val fechaAtencion = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_FECHA_ATENCION))

                lista.add(PedidoInfo(id, idCliente, nombres, apellidos, telefono, fecha, total, estado, fechaAtencion))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerPedido(idPedido: Int): PedidoInfo? {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT p.*, c.${DBHelper.COL_CLIENTE_NOMBRES} as nombres, c.${DBHelper.COL_CLIENTE_APELLIDOS} as apellidos, c.${DBHelper.COL_CLIENTE_TELEFONO} as telefono
            FROM ${DBHelper.TABLE_PEDIDO} p
            INNER JOIN ${DBHelper.TABLE_CLIENTE} c ON p.${DBHelper.COL_PEDIDO_ID_CLIENTE} = c.${DBHelper.COL_CLIENTE_ID}
            WHERE p.${DBHelper.COL_PEDIDO_ID} = ?
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(idPedido.toString()))
        var pedido: PedidoInfo? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_ID))
            val idCliente = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_ID_CLIENTE))
            val nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"))
            val apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"))
            val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_FECHA))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_TOTAL))
            val estado = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_ESTADO))
            val fechaAtencion = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PEDIDO_FECHA_ATENCION))

            pedido = PedidoInfo(id, idCliente, nombres, apellidos, telefono, fecha, total, estado, fechaAtencion)
        }
        cursor.close()
        return pedido
    }

    fun obtenerDetalles(idPedido: Int): List<DetallePedidoItem> {
        val lista = mutableListOf<DetallePedidoItem>()
        val db = dbHelper.readableDatabase

        val sql = """
            SELECT d.*, r.${DBHelper.COL_ROPA_MODELO} as modelo, r.${DBHelper.COL_ROPA_TALLA} as talla, r.${DBHelper.COL_ROPA_COLOR} as color, r.${DBHelper.COL_ROPA_FOTO} as foto
            FROM ${DBHelper.TABLE_DETALLE_PEDIDO} d
            INNER JOIN ${DBHelper.TABLE_ROPA} r ON d.${DBHelper.COL_DETALLE_ID_ROPA} = r.${DBHelper.COL_ROPA_ID}
            WHERE d.${DBHelper.COL_DETALLE_ID_PEDIDO} = ?
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(idPedido.toString()))
        if (cursor.moveToFirst()) {
            do {
                val idRopa = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_DETALLE_ID_ROPA))
                val modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"))
                val talla = cursor.getString(cursor.getColumnIndexOrThrow("talla"))
                val color = cursor.getString(cursor.getColumnIndexOrThrow("color"))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_DETALLE_CANTIDAD))
                val precioUnit = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_DETALLE_PRECIO_UNIT))
                val subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_DETALLE_SUBTOTAL))

                lista.add(DetallePedidoItem(idRopa, modelo, talla, color, foto, cantidad, precioUnit, subtotal))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun atender(idPedido: Int): Pair<Boolean, String> {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val detalles = obtenerDetalles(idPedido)

            for (detalle in detalles) {
                val cursor = db.rawQuery("SELECT ${DBHelper.COL_ROPA_CANTIDAD} FROM ${DBHelper.TABLE_ROPA} WHERE ${DBHelper.COL_ROPA_ID} = ?", arrayOf(detalle.idRopa.toString()))
                if (cursor.moveToFirst()) {
                    val stockActual = cursor.getInt(0)
                    cursor.close()
                    // Nota: el stock ya se descontó al registrar. Si se valida stock disponible actual >= 0:
                    if (stockActual < 0) {
                        return Pair(false, "Stock insuficiente para: ${detalle.modeloRopa}")
                    }
                } else {
                    cursor.close()
                    return Pair(false, "Prenda no encontrada: ${detalle.modeloRopa}")
                }
            }

            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val cvPedido = ContentValues().apply {
                put(DBHelper.COL_PEDIDO_ESTADO, "ATENDIDO")
                put(DBHelper.COL_PEDIDO_FECHA_ATENCION, fechaActual)
            }
            val filas = db.update(DBHelper.TABLE_PEDIDO, cvPedido, "${DBHelper.COL_PEDIDO_ID} = ?", arrayOf(idPedido.toString()))

            if (filas > 0) {
                db.setTransactionSuccessful()
                return Pair(true, "")
            } else {
                return Pair(false, "No se pudo actualizar el pedido")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, e.message ?: "Error desconocido")
        } finally {
            db.endTransaction()
        }
    }
}