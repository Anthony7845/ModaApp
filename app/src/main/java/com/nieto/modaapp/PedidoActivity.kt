package com.nieto.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.nieto.modaapp.data.ClienteDao
import com.nieto.modaapp.data.PedidoDao
import com.nieto.modaapp.databinding.ActivityPedidoBinding
import com.nieto.modaapp.model.Cliente
import com.nieto.modaapp.model.Carrito

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private lateinit var clienteDao: ClienteDao
    private lateinit var pedidoDao: PedidoDao

    private var clienteEncontrado: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteDao = ClienteDao(this)
        pedidoDao = PedidoDao(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.etTelefono.doAfterTextChanged { text ->
            val telefono = text?.toString()?.trim() ?: ""
            if (telefono.length == 9) {
                clienteEncontrado = clienteDao.buscarPorTelefono(telefono)
                if (clienteEncontrado != null) {
                    binding.tvEstadoCliente.text = "Hola, ${clienteEncontrado!!.nombres} ${clienteEncontrado!!.apellidos}"
                    binding.tvEstadoCliente.setTextColor(resources.getColor(R.color.pink_primary, null))
                    binding.layoutNuevoCliente.visibility = View.GONE
                } else {
                    binding.tvEstadoCliente.text = "Cliente nuevo. Por favor ingrese sus datos:"
                    binding.tvEstadoCliente.setTextColor(resources.getColor(R.color.dark_black, null))
                    binding.layoutNuevoCliente.visibility = View.VISIBLE
                }
            } else {
                clienteEncontrado = null
                binding.tvEstadoCliente.text = "Ingrese un número de teléfono válido de 9 dígitos"
                binding.tvEstadoCliente.setTextColor(resources.getColor(R.color.gray_text, null))
                binding.layoutNuevoCliente.visibility = View.GONE
            }
        }

        binding.btnConfirmarPedido.setOnClickListener {
            procesarPedido()
        }
    }

    private fun procesarPedido() {
        val telefono = binding.etTelefono.text.toString().trim()
        if (telefono.length != 9) {
            binding.etTelefono.error = "Ingrese un teléfono de 9 dígitos"
            return
        }

        var idCliente: Long = -1

        if (clienteEncontrado != null) {
            idCliente = clienteEncontrado!!.id.toLong()
        } else {
            val nombres = binding.etNombres.text.toString().trim()
            val apellidos = binding.etApellidos.text.toString().trim()

            if (nombres.isEmpty()) {
                binding.etNombres.error = "Ingrese sus nombres"
                return
            }
            if (apellidos.isEmpty()) {
                binding.etApellidos.error = "Ingrese sus apellidos"
                return
            }

            val nuevoCliente = Cliente(telefono = telefono, nombres = nombres, apellidos = apellidos)
            idCliente = clienteDao.insertar(nuevoCliente)
            if (idCliente == -1L) {
                Toast.makeText(this, "Error al registrar cliente", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val items = Carrito.obtenerItems()
        if (items.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val pedidoId = pedidoDao.registrar(idCliente, items)
        if (pedidoId != -1L) {
            Carrito.vaciar()
            AlertDialog.Builder(this)
                .setTitle("¡Pedido Exitoso!")
                .setMessage("Tu pedido #$pedidoId ha sido registrado correctamente. ¡Gracias por tu compra!")
                .setPositiveButton("Aceptar") { _, _ ->
                    val intent = Intent(this, CatalogoActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                }
                .setCancelable(false)
                .show()
        } else {
            Toast.makeText(this, "Error al registrar el pedido", Toast.LENGTH_LONG).show()
        }
    }
}