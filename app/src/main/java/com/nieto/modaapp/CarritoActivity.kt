package com.nieto.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nieto.modaapp.adapters.CarritoAdapter
import com.nieto.modaapp.databinding.ActivityCarritoBinding
import com.nieto.modaapp.model.Carrito

class CarritoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarCarrito()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnHacerPedido.setOnClickListener {
            if (Carrito.obtenerItems().isNotEmpty()) {
                val intent = Intent(this, PedidoActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarCarrito() {
        val items = Carrito.obtenerItems()
        if (items.isEmpty()) {
            binding.rvCarrito.visibility = View.GONE
            binding.tvEmptyCarrito.visibility = View.VISIBLE
            binding.btnHacerPedido.isEnabled = false
            binding.btnHacerPedido.alpha = 0.5f
            binding.tvTotal.text = "S/. 0.00"
        } else {
            binding.rvCarrito.visibility = View.VISIBLE
            binding.tvEmptyCarrito.visibility = View.GONE
            binding.btnHacerPedido.isEnabled = true
            binding.btnHacerPedido.alpha = 1.0f

            binding.rvCarrito.layoutManager = LinearLayoutManager(this)
            binding.rvCarrito.adapter = CarritoAdapter(items) { item ->
                AlertDialog.Builder(this)
                    .setTitle("Quitar del carrito")
                    .setMessage("¿Deseas quitar ${item.ropa.modelo} del carrito?")
                    .setPositiveButton("Sí, quitar") { _, _ ->
                        Carrito.quitar(item.ropa.id)
                        cargarCarrito()
                        Toast.makeText(this, "Ítem removido", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            val total = Carrito.calcularTotal()
            binding.tvTotal.text = "S/. %.2f".format(total)
        }
    }
}