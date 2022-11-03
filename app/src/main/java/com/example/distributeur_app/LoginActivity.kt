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
import com.example.distributeur_app.Api.ApiHelper
import com.example.distributeur_app.Model.User
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
            val jsonObject = (params as Map<*, *>?)?.let { it -> JSONObject(it) }

            val request = JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                { response ->
                    if(response["state"] as Boolean){
                        Log.i("(SUCCESS)Post response", response.toString())
                        var intent = Intent(this, ProfileActivity::class.java)
                        val userArray = response["user"] as JSONObject
                        intent.putExtra("User", User(
                            binding.NameLogin.text.toString(),
                            userArray["image"] as String
                        ))
                        startActivity(intent)
                        finish()
                    }
                    else{
                        binding.NameLogin.setText("")
                        binding.PasswordLogin.setText("")
                        Toast.makeText(this, "error, wrong credentials", Toast.LENGTH_SHORT).show()
                    }
                },
                { response ->
                    binding.NameLogin.setText("")
                    binding.PasswordLogin.setText("")
                    Toast.makeText(this, "error with database connexion", Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(request)
        }
    }
}
