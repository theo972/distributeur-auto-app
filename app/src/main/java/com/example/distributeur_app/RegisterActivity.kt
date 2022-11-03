package com.example.distributeur_app // ktlint-disable package-name

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.distributeur_app.databinding.ActivityRegisterBinding
import com.example.distributeur_app.Api.ApiHelper
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val queue = Volley.newRequestQueue(this)
        val url = ApiHelper.registerUser

        binding.btnPicture.setOnClickListener {
            openCamera()
        }

        binding.registerButton.setOnClickListener {
            if (binding.passwordTextInput.text.toString() != binding.confirmPasswordTextInput.text.toString()) {
                binding.passwordTextInput.setText("")
                binding.confirmPasswordTextInput.setText("")
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            } else {
                val params = HashMap<String, String>()

                val stream = ByteArrayOutputStream()
                uriToBitmap(image_uri!!)?.compress(Bitmap.CompressFormat.PNG, 90, stream)
                val image = stream.toByteArray()
                val encodedImage = Base64.encodeToString(image, Base64.DEFAULT)

                params["name"] = binding.nameTextInput.text.toString()
                params["password"] = binding.passwordTextInput.text.toString()
                params["image"] = encodedImage

                val jsonObject = (params as Map<*, *>?)?.let { it1 -> JSONObject(it1) }
                val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    { response ->
                        Log.i("(SUCCESS)Post response", response.toString())
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    { response ->
                        binding.passwordTextInput.setText("")
                        binding.confirmPasswordTextInput.setText("")
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                        Log.i("error register", response.toString())
                    }
                )
                queue.add(request)
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        resultLauncher.launch(cameraIntent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val bitmap = uriToBitmap(image_uri!!)
            binding.btnPicture.setImageBitmap(bitmap)
        }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}
