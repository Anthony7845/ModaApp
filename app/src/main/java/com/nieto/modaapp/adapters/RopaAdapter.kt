package com.nieto.modaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nieto.modaapp.data.Ropa
import com.nieto.modaapp.databinding.ItemRopaAdminBinding
import com.nieto.modaapp.utils.ImageUtils

class RopaAdapter(private val listaRopa: List<Ropa>) : RecyclerView.Adapter<RopaAdapter.RopaViewHolder>() {

    inner class RopaViewHolder(private val binding: ItemRopaAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ropa: Ropa) {
            binding.tvModelo.text = ropa.modelo
            binding.tvCategoriaTalla.text = "${ropa.nombreCategoria} • Talla ${ropa.talla}"
            binding.tvPrecioCantidad.text = "S/. %.2f • Stock: %d".format(ropa.precio, ropa.cantidad)

            // Cargar imagen reducida para evitar OutOfMemoryError
            val bitmap = ImageUtils.cargarBitmapReducido(ropa.foto, 150, 150)
            if (bitmap != null) {
                binding.imgMiniatura.setImageBitmap(bitmap)
            } else {
                binding.imgMiniatura.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RopaViewHolder {
        val binding = ItemRopaAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RopaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RopaViewHolder, position: Int) {
        holder.bind(listaRopa[position])
    }

    override fun getItemCount(): Int = listaRopa.size
}