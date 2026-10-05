package com.nieto.modaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nieto.modaapp.databinding.ItemDetallePedidoBinding
import com.nieto.modaapp.model.DetallePedidoItem
import com.nieto.modaapp.utils.ImageUtils

class DetallePedidoAdapter(private val listaItems: List<DetallePedidoItem>) : RecyclerView.Adapter<DetallePedidoAdapter.DetalleViewHolder>() {

    class DetalleViewHolder(val binding: ItemDetallePedidoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DetallePedidoItem) {
            binding.tvModelo.text = item.modeloRopa
            binding.tvDetallePrenda.text = "Talla: ${item.tallaRopa} • Color: ${item.colorRopa ?: "N/D"} • Cant: ${item.cantidad}"
            binding.tvSubtotal.text = "Subtotal: S/. %.2f".format(item.subtotal)

            val bitmap = ImageUtils.cargarBitmapReducido(item.fotoRopa, 150, 150)
            if (bitmap != null) {
                binding.imgMiniaturaDetalle.setImageBitmap(bitmap)
            } else {
                binding.imgMiniaturaDetalle.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ItemDetallePedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetalleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        holder.bind(listaItems[position])
    }

    override fun getItemCount(): Int = listaItems.size
}