package com.nieto.modaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nieto.modaapp.databinding.ItemClienteReporteBinding
import com.nieto.modaapp.model.ClienteReporte

class ClienteReporteAdapter(private val listaClientes: List<ClienteReporte>) : RecyclerView.Adapter<ClienteReporteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(val binding: ItemClienteReporteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cliente: ClienteReporte) {
            binding.tvClienteNombre.text = "${cliente.nombres} ${cliente.apellidos}"
            binding.tvClienteTelefono.text = "Tel: ${cliente.telefono}"
            binding.tvTotalPedidos.text = "${cliente.totalPedidos} pedidos"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteReporteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(listaClientes[position])
    }

    override fun getItemCount(): Int = listaClientes.size
}