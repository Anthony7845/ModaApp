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
import com.nieto.modaapp.data.UsuarioDao
import com.nieto.modaapp.databinding.ActivityPedidoBinding
import com.nieto.modaapp.model.Cliente
import com.nieto.modaapp.model.Carrito
import com.nieto.modaapp.utils.WhatsAppUtils

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private lateinit var clienteDao: ClienteDao
    private lateinit var pedidoDao: PedidoDao
    private lateinit var usuarioDao: UsuarioDao

    private var clienteEncontrado: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteDao = ClienteDao(this)
        pedidoDao = PedidoDao(this)
        usuarioDao = UsuarioDao(this)

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
        var nombresCliente = ""
        var apellidosCliente = ""

        if (clienteEncontrado != null) {
            idCliente = clienteEncontrado!!.id.toLong()
            nombresCliente = clienteEncontrado!!.nombres
            apellidosCliente = clienteEncontrado!!.apellidos
        } else {
            nombresCliente = binding.etNombres.text.toString().trim()
            apellidosCliente = binding.etApellidos.text.toString().trim()

            if (nombresCliente.isEmpty()) {
                binding.etNombres.error = "Ingrese sus nombres"
                return
            }
            if (apellidosCliente.isEmpty()) {
                binding.etApellidos.error = "Ingrese sus apellidos"
                return
            }

            val nuevoCliente = Cliente(telefono = telefono, nombres = nombresCliente, apellidos = apellidosCliente)
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
            val total = Carrito.calcularTotal()
            val mensajeCliente = buildString {
                append("¡Hola $nombresCliente! Su pedido #$pedidoId en Boutique Moda Urbana ha sido registrado:\n")
                for (item in items) {
                    append("- ${item.ropa.modelo} (Talla: ${item.ropa.talla}) x${item.cantidad} = S/. %.2f\n".format(item.ropa.precio * item.cantidad))
                }
                append("Total: S/. %.2f\nEstado: PENDIENTE".format(total))
            }

            val adminTelefono = usuarioDao.obtenerTelefonoAdmin()
            val mensajeAdmin = buildString {
                append("Nuevo pedido #$pedidoId de $nombresCliente $apellidosCliente (Tel: $telefono):\n")
                for (item in items) {
                    append("- ${item.ropa.modelo} (Talla: ${item.ropa.talla}) x${item.cantidad}\n")
                }
                append("Total: S/. %.2f".format(total))
            }

            Carrito.vaciar()

            val dialogView = layoutInflater.inflate(R.layout.dialog_pedido_exitoso, null)
            val dialog = AlertDialog.Builder(this)
                .setTitle("¡Pedido #$pedidoId Exitoso!")
                .setView(dialogView)
                .setCancelable(false)
                .create()

            dialogView.findViewById<View>(R.id.btnEnviarCliente)?.setOnClickListener {
                WhatsAppUtils.abrirWhatsApp(this, telefono, mensajeCliente)
            }

            dialogView.findViewById<View>(R.id.btnEnviarTienda)?.setOnClickListener {
                WhatsAppUtils.abrirWhatsApp(this, adminTelefono, mensajeAdmin)
            }

            dialogView.findViewById<View>(R.id.btnAceptarFinal)?.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this, CatalogoActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            }

            dialog.show()
        } else {
            Toast.makeText(this, "Error al registrar el pedido", Toast.LENGTH_LONG).show()
        }
    }
}