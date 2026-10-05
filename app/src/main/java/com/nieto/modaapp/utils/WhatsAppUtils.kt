package com.nieto.modaapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object WhatsAppUtils {

    fun abrirWhatsApp(context: Context, telefono: String, mensaje: String) {
        try {
            val url = "https://wa.me/51$telefono?text=${Uri.encode(mensaje)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}