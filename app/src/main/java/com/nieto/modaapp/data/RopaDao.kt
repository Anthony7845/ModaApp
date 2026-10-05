package com.nieto.modaapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class RopaDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun insertar(ropa: Ropa): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(DBHelper.COL_ROPA_MODELO, ropa.modelo)
            put(DBHelper.COL_ROPA_ID_CATEGORIA, ropa.idCategoria)
            put(DBHelper.COL_ROPA_TALLA, ropa.talla)
            put(DBHelper.COL_ROPA_MARCA, ropa.marca)
            put(DBHelper.COL_ROPA_COLOR, ropa.color)
            put(DBHelper.COL_ROPA_PRECIO, ropa.precio)
            put(DBHelper.COL_ROPA_CANTIDAD, ropa.cantidad)
            put(DBHelper.COL_ROPA_FOTO, ropa.foto)
        }
        return db.insert(DBHelper.TABLE_ROPA, null, cv)
    }

    fun listar(): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase

        val sql = """
            SELECT r.*, c.${DBHelper.COL_CATEGORIA_NOMBRE} as nombre_categoria 
            FROM ${DBHelper.TABLE_ROPA} r
            INNER JOIN ${DBHelper.TABLE_CATEGORIA} c ON r.${DBHelper.COL_ROPA_ID_CATEGORIA} = c.${DBHelper.COL_CATEGORIA_ID}
            ORDER BY r.${DBHelper.COL_ROPA_ID} DESC
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(sql, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_ID))
                val modelo = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_MODELO))
                val idCat = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_ID_CATEGORIA))
                val nombreCat = cursor.getString(cursor.getColumnIndexOrThrow("nombre_categoria"))
                val talla = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_TALLA))
                val marca = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_MARCA))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_COLOR))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_PRECIO))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_CANTIDAD))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_FOTO))

                lista.add(Ropa(id, modelo, idCat, nombreCat, talla, marca, color, precio, cantidad, foto))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun listarDisponibles(idCategoria: Int? = null): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase

        val selection = if (idCategoria != null && idCategoria > 0) {
            "WHERE r.${DBHelper.COL_ROPA_CANTIDAD} > 0 AND r.${DBHelper.COL_ROPA_ID_CATEGORIA} = ?"
        } else {
            "WHERE r.${DBHelper.COL_ROPA_CANTIDAD} > 0"
        }

        val sql = """
            SELECT r.*, c.${DBHelper.COL_CATEGORIA_NOMBRE} as nombre_categoria 
            FROM ${DBHelper.TABLE_ROPA} r
            INNER JOIN ${DBHelper.TABLE_CATEGORIA} c ON r.${DBHelper.COL_ROPA_ID_CATEGORIA} = c.${DBHelper.COL_CATEGORIA_ID}
            $selection
            ORDER BY r.${DBHelper.COL_ROPA_ID} DESC
        """.trimIndent()

        val args = if (idCategoria != null && idCategoria > 0) arrayOf(idCategoria.toString()) else null
        val cursor: Cursor = db.rawQuery(sql, args)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_ID))
                val modelo = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_MODELO))
                val idCat = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_ID_CATEGORIA))
                val nombreCat = cursor.getString(cursor.getColumnIndexOrThrow("nombre_categoria"))
                val talla = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_TALLA))
                val marca = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_MARCA))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_COLOR))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_PRECIO))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_CANTIDAD))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROPA_FOTO))

                lista.add(Ropa(id, modelo, idCat, nombreCat, talla, marca, color, precio, cantidad, foto))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }
}