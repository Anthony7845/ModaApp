package com.nieto.modaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nieto.modaapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombreUsuario = intent.getStringExtra("EXTRA_USUARIO") ?: "admin"
        binding.tvWelcomeAdmin.text = getString(R.string.welcome_admin_format, nombreUsuario)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cardRopa.setOnClickListener {
            startActivity(Intent(this, RopaActivity::class.java))
        }

        binding.cardPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }

        binding.cardClientes.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }

        binding.cardReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}