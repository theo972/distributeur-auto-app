package com.example.distributeur_app

import android.R.attr.maxLength
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.distributeur_app.databinding.ProfileBinding
import java.io.FileDescriptor
import java.io.IOException


class ProfileActivity: AppCompatActivity(){
    private lateinit var binding: ProfileBinding
    var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }

        binding.walletButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add money to your wallet")
            builder.setMessage("How much money do you want to add ?")

            val input = EditText(this)
            input.setInputType(InputType.TYPE_CLASS_NUMBER)
            input.setRawInputType(Configuration.KEYBOARD_12KEY)
            input.setFilters(arrayOf<InputFilter>(LengthFilter(3)))

            builder.setView(input)

            builder.setPositiveButton(R.string.add) { dialog, which ->
                //TO DO : envoyer la valeur de la thune sur la bdd et mettre à jour le l'affiche de la thune
                val reg = "^[0-9]*".toRegex()
                val clearedCurrencyText = reg.find(binding.credit.text.toString())
                if (clearedCurrencyText != null) {
                    binding.credit.text = (clearedCurrencyText.value.toInt() + input.text.toString().toInt()).toString() +".00 €"
                }
                else{
                    binding.credit.text = input.text.toString().toInt().toString() + ".00 €"
                }
            }

            builder.setNegativeButton(R.string.cancel) { dialog, which ->

            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        binding.photoButton.setOnClickListener {
            openCamera()
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
            binding.profileIcon.setImageBitmap(bitmap)
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