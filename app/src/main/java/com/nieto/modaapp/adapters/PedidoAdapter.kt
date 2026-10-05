package com.nieto.modaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nieto.modaapp.databinding.ItemPedidoBinding
import com.nieto.modaapp.model.PedidoInfo

class PedidoAdapter(
    private val listaPedidos: List<PedidoInfo>,
    private val onClick: (PedidoInfo) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pedido: PedidoInfo, onClick: (PedidoInfo) -> Unit) {
            binding.tvPedidoIdFecha.text = "Pedido #${pedido.id} • ${pedido.fecha}"
            binding.tvEstado.text = pedido.estado
            binding.tvClienteInfo.text = "Cliente: ${pedido.nombresCliente} ${pedido.apellidosCliente} (${pedido.telefonoCliente})"
            binding.tvTotal.text = "Total: S/. %.2f".format(pedido.total)

            binding.root.setOnClickListener {
                onClick(pedido)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(listaPedidos[position], onClick)
    }

    override fun getItemCount(): Int = listaPedidos.size
}