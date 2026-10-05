package com.nieto.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.nieto.modaapp.adapters.RopaAdapter
import com.nieto.modaapp.data.RopaDao
import com.nieto.modaapp.databinding.ActivityRopaBinding

class RopaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRopaBinding
    private lateinit var ropaDao: RopaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRopaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ropaDao = RopaDao(this)

        setupListeners()
        setupBuscador()
    }

    override fun onResume() {
        super.onResume()
        cargarListaRopa(null)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, RopaFormActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBuscador() {
        binding.etBuscador.doAfterTextChanged { text ->
            cargarListaRopa(text?.toString())
        }
    }

    private fun cargarListaRopa(filtro: String?) {
        val lista = ropaDao.listar(filtro)
        if (lista.isEmpty()) {
            binding.rvRopa.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvRopa.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            binding.rvRopa.layoutManager = LinearLayoutManager(this)
            binding.rvRopa.adapter = RopaAdapter(lista) { ropa ->
                val intent = Intent(this, RopaFormActivity::class.java).apply {
                    putExtra("id_ropa", ropa.id)
                }
                startActivity(intent)
            }
        }
    }
}