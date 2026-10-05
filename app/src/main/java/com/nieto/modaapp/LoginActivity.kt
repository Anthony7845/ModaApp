package com.nieto.modaapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nieto.modaapp.data.UsuarioDao
import com.nieto.modaapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao

    companion object {
        private const val PREF_NAME = "ModaAppPrefs"
        private const val KEY_USUARIO = "key_usuario"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val usuarioGuardado = sharedPrefs.getString(KEY_USUARIO, null)
        if (usuarioGuardado != null) {
            val intent = Intent(this, MenuActivity::class.java).apply {
                putExtra("EXTRA_USUARIO", usuarioGuardado)
                putExtra("EXTRA_ROL", "ADMIN")
            }
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDao = UsuarioDao(this)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLoginAdmin.setOnClickListener {
            if (validateFields()) {
                val username = binding.etUsername.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()

                val usuario = usuarioDao.validarUsuario(username, password)

                if (usuario != null && usuario.rol == "ADMIN") {
                    val sharedPrefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    sharedPrefs.edit().putString(KEY_USUARIO, usuario.usuario).apply()

                    val intent = Intent(this, MenuActivity::class.java).apply {
                        putExtra("EXTRA_USUARIO", usuario.usuario)
                        putExtra("EXTRA_ROL", usuario.rol)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.login_failed_toast), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnClientCatalog.setOnClickListener {
            val intent = Intent(this, CatalogoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.error_empty_username)
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }
}