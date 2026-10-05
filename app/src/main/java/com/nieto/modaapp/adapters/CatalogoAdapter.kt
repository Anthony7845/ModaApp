package com.nieto.modaapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nieto.modaapp.data.Ropa
import com.nieto.modaapp.databinding.ItemCatalogoBinding
import com.nieto.modaapp.utils.ImageUtils

class CatalogoAdapter(
    private val listaRopa: List<Ropa>,
    private val onAgregarClick: (Ropa) -> Unit
) : RecyclerView.Adapter<CatalogoAdapter.CatalogoViewHolder>() {

    class CatalogoViewHolder(val binding: ItemCatalogoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ropa: Ropa, onAgregarClick: (Ropa) -> Unit) {
            binding.tvModelo.text = ropa.modelo
            binding.tvTalla.text = "Talla: ${ropa.talla}"
            binding.tvPrecio.text = "S/. %.2f".format(ropa.precio)

            val bitmap = ImageUtils.cargarBitmapReducido(ropa.foto, 300, 300)
            if (bitmap != null) {
                binding.imgCatalogo.setImageBitmap(bitmap)
            } else {
                binding.imgCatalogo.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.btnAgregarCarrito.setOnClickListener {
                onAgregarClick(ropa)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogoViewHolder {
        val binding = ItemCatalogoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatalogoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatalogoViewHolder, position: Int) {
        holder.bind(listaRopa[position], onAgregarClick)
    }

    override fun getItemCount(): Int = listaRopa.size
}