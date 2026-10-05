package com.nieto.modaapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nieto.modaapp.data.Categoria
import com.nieto.modaapp.data.CategoriaDao
import com.nieto.modaapp.data.Ropa
import com.nieto.modaapp.data.RopaDao
import com.nieto.modaapp.databinding.ActivityRopaFormBinding
import com.nieto.modaapp.utils.ImageUtils

class RopaFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRopaFormBinding
    private lateinit var categoriaDao: CategoriaDao
    private lateinit var ropaDao: RopaDao

    private var categoriaList: List<Categoria> = emptyList()
    private var rutaFotoSeleccionada: String? = null
    private var idPrendaEdicion: Int = -1

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            val path = ImageUtils.guardarImagenInterna(this, uri)
            if (path != null) {
                rutaFotoSeleccionada = path
                val bitmap = ImageUtils.cargarBitmapReducido(path, 300, 300)
                binding.imgFotoPrenda.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRopaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoriaDao = CategoriaDao(this)
        ropaDao = RopaDao(this)

        idPrendaEdicion = intent.getIntExtra("id_ropa", -1)

        setupSpinners()
        setupModoEdicion()
        setupListeners()
    }

    private fun setupSpinners() {
        categoriaList = categoriaDao.listar()
        val nombresCategorias = categoriaList.map { it.nombre }
        val adapterCat = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresCategorias)
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapterCat

        val tallas = arrayOf("XS", "S", "M", "L", "XL")
        val adapterTalla = ArrayAdapter(this, android.R.layout.simple_spinner_item, tallas)
        adapterTalla.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTalla.adapter = adapterTalla
    }

    private fun setupModoEdicion() {
        if (idPrendaEdicion != -1) {
            binding.toolbar.title = "Editar Prenda"
            binding.btnGuardar.text = "Actualizar Prenda"
            binding.btnEliminar.visibility = View.VISIBLE

            val ropa = ropaDao.obtener(idPrendaEdicion)
            if (ropa != null) {
                binding.etModelo.setText(ropa.modelo)
                binding.etMarca.setText(ropa.marca ?: "")
                binding.etColor.setText(ropa.color ?: "")
                binding.etPrecio.setText(ropa.precio.toString())
                binding.etCantidad.setText(ropa.cantidad.toString())
                rutaFotoSeleccionada = ropa.foto

                val bitmap = ImageUtils.cargarBitmapReducido(ropa.foto, 300, 300)
                if (bitmap != null) {
                    binding.imgFotoPrenda.setImageBitmap(bitmap)
                }

                val catIndex = categoriaList.indexOfFirst { it.id == ropa.idCategoria }
                if (catIndex != -1) {
                    binding.spinnerCategoria.setSelection(catIndex)
                }

                val tallas = arrayOf("XS", "S", "M", "L", "XL")
                val tallaIndex = tallas.indexOf(ropa.talla)
                if (tallaIndex != -1) {
                    binding.spinnerTalla.setSelection(tallaIndex)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnElegirFoto.setOnClickListener {
            pickImageLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.btnGuardar.setOnClickListener {
            if (validarYGuardar()) {
                val mensaje = if (idPrendaEdicion == -1) "Prenda registrada exitosamente" else "Prenda actualizada exitosamente"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar Prenda")
                .setMessage("¿Estás seguro de eliminar esta prenda?")
                .setPositiveButton("Sí, eliminar") { _, _ ->
                    val eliminado = ropaDao.eliminar(idPrendaEdicion)
                    if (eliminado) {
                        Toast.makeText(this, "Prenda eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "No se puede eliminar: tiene pedidos asociados", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun validarYGuardar(): Boolean {
        val modelo = binding.etModelo.text.toString().trim()
        val marca = binding.etMarca.text.toString().trim()
        val color = binding.etColor.text.toString().trim()
        val precioStr = binding.etPrecio.text.toString().trim()
        val cantidadStr = binding.etCantidad.text.toString().trim()

        if (rutaFotoSeleccionada == null) {
            Toast.makeText(this, "Debe seleccionar una foto", Toast.LENGTH_SHORT).show()
            return false
        }

        if (modelo.isEmpty()) {
            binding.etModelo.error = "Ingrese el modelo"
            return false
        }

        if (precioStr.isEmpty()) {
            binding.etPrecio.error = "Ingrese el precio"
            return false
        }

        val precio = precioStr.toDoubleOrNull() ?: 0.0
        if (precio <= 0) {
            binding.etPrecio.error = "El precio debe ser mayor a 0"
            return false
        }

        if (cantidadStr.isEmpty()) {
            binding.etCantidad.error = "Ingrese la cantidad"
            return false
        }

        val cantidad = cantidadStr.toIntOrNull() ?: -1
        if (cantidad < 0) {
            binding.etCantidad.error = "La cantidad no puede ser negativa"
            return false
        }

        val categoriaSeleccionada = categoriaList[binding.spinnerCategoria.selectedItemPosition]
        val tallaSeleccionada = binding.spinnerTalla.selectedItem.toString()

        val ropa = Ropa(
            id = if (idPrendaEdicion == -1) 0 else idPrendaEdicion,
            modelo = modelo,
            idCategoria = categoriaSeleccionada.id,
            talla = tallaSeleccionada,
            marca = if (marca.isEmpty()) null else marca,
            color = if (color.isEmpty()) null else color,
            precio = precio,
            cantidad = cantidad,
            foto = rutaFotoSeleccionada!!
        )

        return if (idPrendaEdicion == -1) {
            ropaDao.insertar(ropa) > 0
        } else {
            ropaDao.actualizar(ropa) > 0
        }
    }
}