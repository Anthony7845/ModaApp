package com.nieto.modaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nieto.modaapp.adapters.RopaAdapter
import com.nieto.modaapp.data.ReportesDao
import com.nieto.modaapp.data.RopaDao
import com.nieto.modaapp.databinding.ActivityReportesBinding

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var reportesDao: ReportesDao
    private lateinit var ropaDao: RopaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reportesDao = ReportesDao(this)
        ropaDao = RopaDao(this)

        setupListeners()
        cargarReportes()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun cargarReportes() {
        val (count, total) = reportesDao.atendidosDelMes()
        binding.tvPedidosAtendidosMes.text = "Pedidos atendidos: $count"
        binding.tvMontoVendidoMes.text = "Total vendido: S/. %.2f".format(total)

        val ropaList = ropaDao.listar().filter { it.cantidad <= 3 }
        binding.rvStockBajo.layoutManager = LinearLayoutManager(this)
        binding.rvStockBajo.adapter = RopaAdapter(ropaList) { _ ->
        }
    }
}