package com.nieto.modaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nieto.modaapp.databinding.ItemCarritoBinding
import com.nieto.modaapp.model.ItemCarrito
import com.nieto.modaapp.utils.ImageUtils

class CarritoAdapter(
    private val listaItems: List<ItemCarrito>,
    private val onLongClick: (ItemCarrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(val binding: ItemCarritoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemCarrito, onLongClick: (ItemCarrito) -> Unit) {
            binding.tvModelo.text = item.ropa.modelo
            binding.tvDetalle.text = "Talla: ${item.ropa.talla} • Cant: ${item.cantidad}"
            val subtotal = item.ropa.precio * item.cantidad
            binding.tvSubtotal.text = "Subtotal: S/. %.2f".format(subtotal)

            val bitmap = ImageUtils.cargarBitmapReducido(item.ropa.foto, 150, 150)
            if (bitmap != null) {
                binding.imgMiniaturaCarrito.setImageBitmap(bitmap)
            } else {
                binding.imgMiniaturaCarrito.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        holder.bind(listaItems[position], onLongClick)
    }

    override fun getItemCount(): Int = listaItems.size
}