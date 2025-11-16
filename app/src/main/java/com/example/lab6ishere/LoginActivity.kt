package com.example.lab6ishere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lab6ishere.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextLogin = findViewById<EditText>(R.id.editTextLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        val dao = AppDatabase.getDatabase(application).userDao()

        buttonLogin.setOnClickListener {
            val login = editTextLogin.text.toString()
            val password = editTextPassword.text.toString()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Поля не можуть бути порожніми", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val user = dao.getUser(login, password)

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Неправильний логін або пароль", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}