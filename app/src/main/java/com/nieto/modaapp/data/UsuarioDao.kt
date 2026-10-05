package com.nieto.modaapp.data

import android.content.Context
import android.database.Cursor

class UsuarioDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun validarUsuario(usuarioInput: String, claveInput: String): Usuario? {
        val db = dbHelper.readableDatabase
        val sql = "SELECT * FROM ${DBHelper.TABLE_USUARIO} WHERE ${DBHelper.COL_USUARIO_NOMBRE} = ? AND ${DBHelper.COL_USUARIO_CLAVE} = ?"
        val cursor: Cursor = db.rawQuery(sql, arrayOf(usuarioInput, claveInput))

        var usuarioEncontrado: Usuario? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USUARIO_ID))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USUARIO_NOMBRE))
            val clave = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USUARIO_CLAVE))
            val rol = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USUARIO_ROL))
            val telefono = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USUARIO_TELEFONO))

            usuarioEncontrado = Usuario(id, nombre, clave, rol, telefono)
        }

        cursor.close()
        return usuarioEncontrado
    }

    fun obtenerTelefonoAdmin(): String {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT ${DBHelper.COL_USUARIO_TELEFONO} FROM ${DBHelper.TABLE_USUARIO} WHERE ${DBHelper.COL_USUARIO_ROL} = 'ADMIN' LIMIT 1", null)
        var tel = "987654321"
        if (cursor.moveToFirst()) {
            tel = cursor.getString(0) ?: "987654321"
        }
        cursor.close()
        return tel
    }
}