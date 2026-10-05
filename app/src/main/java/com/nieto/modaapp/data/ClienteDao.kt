package com.nieto.modaapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.nieto.modaapp.model.Cliente
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClienteDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun buscarPorTelefono(telefono: String): Cliente? {
        val db = dbHelper.readableDatabase
        val sql = "SELECT * FROM ${DBHelper.TABLE_CLIENTE} WHERE ${DBHelper.COL_CLIENTE_TELEFONO} = ?"
        val cursor: Cursor = db.rawQuery(sql, arrayOf(telefono))

        var cliente: Cliente? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CLIENTE_ID))
            val tel = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CLIENTE_TELEFONO))
            val nombres = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CLIENTE_NOMBRES))
            val apellidos = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CLIENTE_APELLIDOS))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CLIENTE_FECHA_REGISTRO))
            cliente = Cliente(id, tel, nombres, apellidos, fecha)
        }
        cursor.close()
        return cliente
    }

    fun insertar(cliente: Cliente): Long {
        val db = dbHelper.writableDatabase
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(DBHelper.COL_CLIENTE_TELEFONO, cliente.telefono)
            put(DBHelper.COL_CLIENTE_NOMBRES, cliente.nombres)
            put(DBHelper.COL_CLIENTE_APELLIDOS, cliente.apellidos)
            put(DBHelper.COL_CLIENTE_FECHA_REGISTRO, fechaActual)
        }

        val id = db.insertWithOnConflict(DBHelper.TABLE_CLIENTE, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
        return if (id == -1L) {
            buscarPorTelefono(cliente.telefono)?.id?.toLong() ?: -1L
        } else {
            id
        }
    }
}