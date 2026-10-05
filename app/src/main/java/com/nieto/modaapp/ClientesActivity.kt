package com.nieto.modaapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.nieto.modaapp.adapters.ClienteReporteAdapter
import com.nieto.modaapp.data.ReportesDao
import com.nieto.modaapp.databinding.ActivityClientesBinding

class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private lateinit var reportesDao: ReportesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reportesDao = ReportesDao(this)

        setupListeners()
        setupBuscador()
        cargarClientes(null)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupBuscador() {
        binding.etBuscadorCliente.doAfterTextChanged { text ->
            cargarClientes(text?.toString())
        }
    }

    private fun cargarClientes(filtro: String?) {
        val lista = reportesDao.clientesConPedidos(filtro)
        if (lista.isEmpty()) {
            binding.rvClientes.visibility = View.GONE
            binding.tvEmptyClientes.visibility = View.VISIBLE
        } else {
            binding.rvClientes.visibility = View.VISIBLE
            binding.tvEmptyClientes.visibility = View.GONE
            binding.rvClientes.layoutManager = LinearLayoutManager(this)
            binding.rvClientes.adapter = ClienteReporteAdapter(lista)
        }
    }
}