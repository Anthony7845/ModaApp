package com.nieto.modaapp.data

import android.content.Context
import com.nieto.modaapp.model.ClienteReporte
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportesDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun atendidosDelMes(): Pair<Int, Double> {
        val db = dbHelper.readableDatabase
        val mesActual = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date()) // Ej: "2026-10"

        val sql = """
            SELECT COUNT(*), SUM(${DBHelper.COL_PEDIDO_TOTAL})
            FROM ${DBHelper.TABLE_PEDIDO}
            WHERE ${DBHelper.COL_PEDIDO_ESTADO} = 'ATENDIDO' 
            AND ${DBHelper.COL_PEDIDO_FECHA_ATENCION} LIKE ?
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf("$mesActual%"))
        var count = 0
        var total = 0.0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
            total = cursor.getDouble(1)
        }
        cursor.close()
        return Pair(count, total)
    }

    fun clientesConPedidos(filtro: String? = null): List<ClienteReporte> {
        val lista = mutableListOf<ClienteReporte>()
        val db = dbHelper.readableDatabase

        val selection = if (!filtro.isNullOrBlank()) {
            "WHERE c.${DBHelper.COL_CLIENTE_NOMBRES} LIKE ? OR c.${DBHelper.COL_CLIENTE_APELLIDOS} LIKE ? OR c.${DBHelper.COL_CLIENTE_TELEFONO} LIKE ?"
        } else {
            ""
        }

        val sql = """
            SELECT c.${DBHelper.COL_CLIENTE_NOMBRES} as nombres, c.${DBHelper.COL_CLIENTE_APELLIDOS} as apellidos, c.${DBHelper.COL_CLIENTE_TELEFONO} as telefono, COUNT(p.${DBHelper.COL_PEDIDO_ID}) as total_pedidos
            FROM ${DBHelper.TABLE_CLIENTE} c
            LEFT JOIN ${DBHelper.TABLE_PEDIDO} p ON c.${DBHelper.COL_CLIENTE_ID} = p.${DBHelper.COL_PEDIDO_ID_CLIENTE}
            $selection
            GROUP BY c.${DBHelper.COL_CLIENTE_ID}
            ORDER BY total_pedidos DESC
        """.trimIndent()

        val args = if (!filtro.isNullOrBlank()) {
            val patron = "%$filtro%"
            arrayOf(patron, patron, patron)
        } else {
            null
        }

        val cursor = db.rawQuery(sql, args)
        if (cursor.moveToFirst()) {
            do {
                val nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"))
                val apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                val totalPedidos = cursor.getInt(cursor.getColumnIndexOrThrow("total_pedidos"))

                lista.add(ClienteReporte(nombres, apellidos, telefono, totalPedidos))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}