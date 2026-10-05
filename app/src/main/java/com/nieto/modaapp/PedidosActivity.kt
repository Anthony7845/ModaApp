package com.nieto.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.nieto.modaapp.adapters.PedidoAdapter
import com.nieto.modaapp.data.PedidoDao
import com.nieto.modaapp.databinding.ActivityPedidosBinding

class PedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidosBinding
    private lateinit var pedidoDao: PedidoDao
    private var estadoActual = "PENDIENTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarPedidos(estadoActual)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.tabLayoutPedidos.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                estadoActual = if (tab?.position == 0) "PENDIENTE" else "ATENDIDO"
                cargarPedidos(estadoActual)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun cargarPedidos(estado: String) {
        val lista = pedidoDao.listarPorEstado(estado)
        if (lista.isEmpty()) {
            binding.rvPedidos.visibility = View.GONE
            binding.tvEmptyPedidos.visibility = View.VISIBLE
        } else {
            binding.rvPedidos.visibility = View.VISIBLE
            binding.tvEmptyPedidos.visibility = View.GONE
            binding.rvPedidos.layoutManager = LinearLayoutManager(this)
            binding.rvPedidos.adapter = PedidoAdapter(lista) { pedido ->
                val intent = Intent(this, DetallePedidoActivity::class.java).apply {
                    putExtra("id_pedido", pedido.id)
                }
                startActivity(intent)
            }
        }
    }
}