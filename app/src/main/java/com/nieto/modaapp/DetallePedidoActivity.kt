package com.nieto.modaapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nieto.modaapp.adapters.DetallePedidoAdapter
import com.nieto.modaapp.data.PedidoDao
import com.nieto.modaapp.databinding.ActivityDetallePedidoBinding
import com.nieto.modaapp.model.PedidoInfo

class DetallePedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetallePedidoBinding
    private lateinit var pedidoDao: PedidoDao
    private var idPedido: Int = -1
    private var pedidoInfo: PedidoInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallePedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)
        idPedido = intent.getIntExtra("id_pedido", -1)

        setupListeners()
        cargarDatosPedido()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnLlamar.setOnClickListener {
            if (pedidoInfo != null) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${pedidoInfo!!.telefonoCliente}"))
                startActivity(intent)
            }
        }

        binding.btnMarcarAtendido.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Atender Pedido")
                .setMessage("¿Confirmar que este pedido ha sido atendido?")
                .setPositiveButton("Sí, atender") { _, _ ->
                    val (exito, mensaje) = pedidoDao.atender(idPedido)
                    if (exito) {
                        Toast.makeText(this, "Pedido marcado como ATENDIDO", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error: $mensaje", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun cargarDatosPedido() {
        if (idPedido == -1) return

        pedidoInfo = pedidoDao.obtenerPedido(idPedido)
        if (pedidoInfo != null) {
            binding.tvClienteNombre.text = "Cliente: ${pedidoInfo!!.nombresCliente} ${pedidoInfo!!.apellidosCliente}"
            binding.tvClienteTelefono.text = "Teléfono: ${pedidoInfo!!.telefonoCliente}"
            binding.tvTotalDetalle.text = "S/. %.2f".format(pedidoInfo!!.total)

            if (pedidoInfo!!.estado == "ATENDIDO") {
                binding.btnMarcarAtendido.visibility = View.GONE
                binding.toolbar.subtitle = "Estado: ATENDIDO (${pedidoInfo!!.fechaAtencion ?: ""})"
            } else {
                binding.btnMarcarAtendido.visibility = View.VISIBLE
                binding.toolbar.subtitle = "Estado: PENDIENTE"
            }
        }

        val detalles = pedidoDao.obtenerDetalles(idPedido)
        binding.rvDetallePedido.layoutManager = LinearLayoutManager(this)
        binding.rvDetallePedido.adapter = DetallePedidoAdapter(detalles)
    }
}