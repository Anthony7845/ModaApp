package com.nieto.modaapp

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

        setupSpinners()
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
                Toast.makeText(this, "Prenda guardada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
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

        val nuevaRopa = Ropa(
            modelo = modelo,
            idCategoria = categoriaSeleccionada.id,
            talla = tallaSeleccionada,
            marca = if (marca.isEmpty()) null else marca,
            color = if (color.isEmpty()) null else color,
            precio = precio,
            cantidad = cantidad,
            foto = rutaFotoSeleccionada!!
        )

        val resultado = ropaDao.insertar(nuevaRopa)
        return resultado > 0
    }
}