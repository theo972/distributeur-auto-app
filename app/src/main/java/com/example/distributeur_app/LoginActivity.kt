package com.example.distributeur_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.distributeur_app.databinding.ActivityLoginBinding
import com.example.minstalesapp.Api.ApiHelper
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val queue = Volley.newRequestQueue(this)
        val url = ApiHelper.logUser

        binding.login.setOnClickListener {
            val params = HashMap<String, String>()
            params["name"] = binding.NameLogin.text.toString()
            params["password"] = binding.PasswordLogin.text.toString()
            val jsonObject = (params as Map<*, *>?)?.let { it1 -> JSONObject(it1) }

            val request = JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                { response ->
                    Log.i("(SUCCESS)Post response", response.toString())
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                },
                { response ->
                    binding.NameLogin.setText("")
                    binding.PasswordLogin.setText("")
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            )

            queue.add(request)

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
