package com.nieto.modaapp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "modaapp.db"
        private const val DATABASE_VERSION = 2

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

        const val TABLE_CLIENTE = "cliente"
        const val COL_CLIENTE_ID = "id"
        const val COL_CLIENTE_TELEFONO = "telefono"
        const val COL_CLIENTE_NOMBRES = "nombres"
        const val COL_CLIENTE_APELLIDOS = "apellidos"
        const val COL_CLIENTE_FECHA_REGISTRO = "fecha_registro"

        const val TABLE_PEDIDO = "pedido"
        const val COL_PEDIDO_ID = "id"
        const val COL_PEDIDO_ID_CLIENTE = "id_cliente"
        const val COL_PEDIDO_FECHA = "fecha"
        const val COL_PEDIDO_TOTAL = "total"
        const val COL_PEDIDO_ESTADO = "estado"
        const val COL_PEDIDO_FECHA_ATENCION = "fecha_atencion"

        const val TABLE_DETALLE_PEDIDO = "detalle_pedido"
        const val COL_DETALLE_ID = "id"
        const val COL_DETALLE_ID_PEDIDO = "id_pedido"
        const val COL_DETALLE_ID_ROPA = "id_ropa"
        const val COL_DETALLE_CANTIDAD = "cantidad"
        const val COL_DETALLE_PRECIO_UNIT = "precio_unit"
        const val COL_DETALLE_SUBTOTAL = "subtotal"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        crearTodasLasTablas(db)
        insertarDatosSemilla(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        crearTodasLasTablas(db)
    }

    private fun crearTodasLasTablas(db: SQLiteDatabase) {
        val createTableUsuario = """
            CREATE TABLE IF NOT EXISTS $TABLE_USUARIO (
                $COL_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USUARIO_NOMBRE TEXT UNIQUE NOT NULL,
                $COL_USUARIO_CLAVE TEXT NOT NULL,
                $COL_USUARIO_ROL TEXT NOT NULL,
                $COL_USUARIO_TELEFONO TEXT
            );
        """.trimIndent()

        val createTableCategoria = """
            CREATE TABLE IF NOT EXISTS $TABLE_CATEGORIA (
                $COL_CATEGORIA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CATEGORIA_NOMBRE TEXT UNIQUE NOT NULL
            );
        """.trimIndent()

        val createTableRopa = """
            CREATE TABLE IF NOT EXISTS $TABLE_ROPA (
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

        val createTableCliente = """
            CREATE TABLE IF NOT EXISTS $TABLE_CLIENTE (
                $COL_CLIENTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CLIENTE_TELEFONO TEXT UNIQUE NOT NULL,
                $COL_CLIENTE_NOMBRES TEXT NOT NULL,
                $COL_CLIENTE_APELLIDOS TEXT NOT NULL,
                $COL_CLIENTE_FECHA_REGISTRO TEXT
            );
        """.trimIndent()

        val createTablePedido = """
            CREATE TABLE IF NOT EXISTS $TABLE_PEDIDO (
                $COL_PEDIDO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PEDIDO_ID_CLIENTE INTEGER NOT NULL,
                $COL_PEDIDO_FECHA TEXT NOT NULL,
                $COL_PEDIDO_TOTAL REAL NOT NULL,
                $COL_PEDIDO_ESTADO TEXT NOT NULL,
                $COL_PEDIDO_FECHA_ATENCION TEXT,
                FOREIGN KEY ($COL_PEDIDO_ID_CLIENTE) REFERENCES $TABLE_CLIENTE($COL_CLIENTE_ID)
            );
        """.trimIndent()

        val createTableDetalle = """
            CREATE TABLE IF NOT EXISTS $TABLE_DETALLE_PEDIDO (
                $COL_DETALLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DETALLE_ID_PEDIDO INTEGER NOT NULL,
                $COL_DETALLE_ID_ROPA INTEGER NOT NULL,
                $COL_DETALLE_CANTIDAD INTEGER CHECK($COL_DETALLE_CANTIDAD > 0),
                $COL_DETALLE_PRECIO_UNIT REAL NOT NULL,
                $COL_DETALLE_SUBTOTAL REAL NOT NULL,
                FOREIGN KEY ($COL_DETALLE_ID_PEDIDO) REFERENCES $TABLE_PEDIDO($COL_PEDIDO_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COL_DETALLE_ID_ROPA) REFERENCES $TABLE_ROPA($COL_ROPA_ID)
            );
        """.trimIndent()

        db.execSQL(createTableUsuario)
        db.execSQL(createTableCategoria)
        db.execSQL(createTableRopa)
        db.execSQL(createTableCliente)
        db.execSQL(createTablePedido)
        db.execSQL(createTableDetalle)
    }

    private fun insertarDatosSemilla(db: SQLiteDatabase) {
        try {
            val cvAdmin = ContentValues().apply {
                put(COL_USUARIO_NOMBRE, "admin")
                put(COL_USUARIO_CLAVE, "1234")
                put(COL_USUARIO_ROL, "ADMIN")
                put(COL_USUARIO_TELEFONO, "987654321")
            }
            db.insertWithOnConflict(TABLE_USUARIO, null, cvAdmin, SQLiteDatabase.CONFLICT_IGNORE)

            val categoriasIniciales = listOf("Polos", "Pantalones", "Vestidos", "Casacas", "Zapatillas")
            for (cat in categoriasIniciales) {
                val cvCat = ContentValues().apply {
                    put(COL_CATEGORIA_NOMBRE, cat)
                }
                db.insertWithOnConflict(TABLE_CATEGORIA, null, cvCat, SQLiteDatabase.CONFLICT_IGNORE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}