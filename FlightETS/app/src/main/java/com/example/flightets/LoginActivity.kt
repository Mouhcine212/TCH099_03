package com.example.flightets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flightets.LoginRequest
import com.example.flightets.LoginResponse
import com.example.flightets.AuthApiService
import com.example.flightets.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        val request = LoginRequest(email, password)

        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val role = response.body()?.role
                    Toast.makeText(this@LoginActivity, "Bienvenue ${response.body()?.email}", Toast.LENGTH_SHORT).show()

                    if (role == "admin") {
                        startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, SearchFlightActivity::class.java))
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Identifiants invalides", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erreur: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
