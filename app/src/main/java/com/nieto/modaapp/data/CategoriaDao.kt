package com.nieto.modaapp.data

import android.content.Context
import android.database.Cursor

data class Categoria(val id: Int, val nombre: String)

class CategoriaDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun listar(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${DBHelper.TABLE_CATEGORIA}", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORIA_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORIA_NOMBRE))
                lista.add(Categoria(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}