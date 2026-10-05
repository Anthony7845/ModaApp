package com.nieto.modaapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "modaapp.db"
        private const val DATABASE_VERSION = 1

        // Tabla usuario
        const val TABLE_USUARIO = "usuario"
        const val COL_USUARIO_ID = "id"
        const val COL_USUARIO_NOMBRE = "usuario"
        const val COL_USUARIO_CLAVE = "clave"
        const val COL_USUARIO_ROL = "rol"
        const val COL_USUARIO_TELEFONO = "telefono"

        // Tabla categoria
        const val TABLE_CATEGORIA = "categoria"
        const val COL_CATEGORIA_ID = "id"
        const val COL_CATEGORIA_NOMBRE = "nombre"

        // Tabla ropa
        const val TABLE_ROPA = "ropa"
        const val COL_ROPA_ID = "id"
        const val COL_ROPA_MODELO = "modelo"
        const val COL_ROPA_ID_CATEGORIA = "id_categoria"
        const val COL_ROPA_TALLA = "talla"
        const val COL_ROPA_MARCA = "marca"
        const val COL_ROPA_COLOR = "color"
        const val COL_ROPA_PRECIO = "precio"
        const val COL_ROPA_CANTIDAD = "cantidad"
        const val COL_ROPA_FOTO = "foto"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                $COL_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USUARIO_NOMBRE TEXT UNIQUE NOT NULL,
                $COL_USUARIO_CLAVE TEXT NOT NULL,
                $COL_USUARIO_ROL TEXT NOT NULL,
                $COL_USUARIO_TELEFONO TEXT
            );
        """.trimIndent()

        val createTableCategoria = """
            CREATE TABLE $TABLE_CATEGORIA (
                $COL_CATEGORIA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CATEGORIA_NOMBRE TEXT UNIQUE NOT NULL
            );
        """.trimIndent()

        val createTableRopa = """
            CREATE TABLE $TABLE_ROPA (
                $COL_ROPA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ROPA_MODELO TEXT NOT NULL,
                $COL_ROPA_ID_CATEGORIA INTEGER NOT NULL,
                $COL_ROPA_TALLA TEXT NOT NULL,
                $COL_ROPA_MARCA TEXT,
                $COL_ROPA_COLOR TEXT,
                $COL_ROPA_PRECIO REAL CHECK($COL_ROPA_PRECIO > 0),
                $COL_ROPA_CANTIDAD INTEGER CHECK($COL_ROPA_CANTIDAD >= 0),
                $COL_ROPA_FOTO TEXT NOT NULL,
                FOREIGN KEY ($COL_ROPA_ID_CATEGORIA) REFERENCES $TABLE_CATEGORIA($COL_CATEGORIA_ID) ON DELETE RESTRICT
            );
        """.trimIndent()

        db.execSQL(createTableUsuario)
        db.execSQL(createTableCategoria)
        db.execSQL(createTableRopa)

        val cvAdmin = ContentValues().apply {
            put(COL_USUARIO_NOMBRE, "admin")
            put(COL_USUARIO_CLAVE, "1234")
            put(COL_USUARIO_ROL, "ADMIN")
            put(COL_USUARIO_TELEFONO, "987654321")
        }
        db.insert(TABLE_USUARIO, null, cvAdmin)

        val categoriasIniciales = listOf("Polos", "Pantalones", "Vestidos", "Casacas", "Zapatillas")
        for (cat in categoriasIniciales) {
            val cvCat = ContentValues().apply {
                put(COL_CATEGORIA_NOMBRE, cat)
            }
            db.insert(TABLE_CATEGORIA, null, cvCat)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ROPA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        onCreate(db)
    }
}