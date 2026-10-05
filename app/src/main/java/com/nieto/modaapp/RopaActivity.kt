package com.nieto.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
    }

    override fun onResume() {
        super.onResume()
        cargarListaRopa()
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

    private fun cargarListaRopa() {
        val lista = ropaDao.listar()
        if (lista.isEmpty()) {
            binding.rvRopa.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvRopa.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
            binding.rvRopa.layoutManager = LinearLayoutManager(this)
            binding.rvRopa.adapter = RopaAdapter(lista)
        }
    }
}