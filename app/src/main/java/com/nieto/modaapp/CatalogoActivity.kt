package com.nieto.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.nieto.modaapp.adapters.CatalogoAdapter
import com.nieto.modaapp.data.Categoria
import com.nieto.modaapp.data.CategoriaDao
import com.nieto.modaapp.data.RopaDao
import com.nieto.modaapp.databinding.ActivityCatalogoBinding

class CatalogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoBinding
    private lateinit var ropaDao: RopaDao
    private lateinit var categoriaDao: CategoriaDao
    private var categoriaList: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ropaDao = RopaDao(this)
        categoriaDao = CategoriaDao(this)

        setupListeners()
        cargarChipsCategorias()
        cargarCatalogo(null) // null = Todas
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarChipsCategorias() {
        categoriaList = categoriaDao.listar()

        // Chip "Todas"
        val chipTodas = Chip(this).apply {
            text = "Todas"
            isCheckable = true
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cargarCatalogo(null)
                }
            }
        }
        binding.chipGroupCategorias.addView(chipTodas)

        for (cat in categoriaList) {
            val chip = Chip(this).apply {
                text = cat.nombre
                isCheckable = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        cargarCatalogo(cat.id)
                    }
                }
            }
            binding.chipGroupCategorias.addView(chip)
        }
    }

    private fun cargarCatalogo(idCategoria: Int?) {
        val lista = ropaDao.listarDisponibles(idCategoria)
        if (lista.isEmpty()) {
            binding.rvCatalogo.visibility = View.GONE
            binding.tvEmptyCatalog.visibility = View.VISIBLE
        } else {
            binding.rvCatalogo.visibility = View.VISIBLE
            binding.tvEmptyCatalog.visibility = View.GONE
            binding.rvCatalogo.layoutManager = GridLayoutManager(this, 2)
            binding.rvCatalogo.adapter = CatalogoAdapter(lista)
        }
    }
}