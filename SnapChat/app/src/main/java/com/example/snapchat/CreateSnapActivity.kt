package com.example.snapchat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    var createSnapImageView : ImageView? = null
    var messageEditText : EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createSnapImageView = findViewById(R.id.CreateSnapimageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    //Ambil foto dari device user
    fun getPhoto(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,1)
    }

    fun chooseimageClicked(view : View){
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{getPhoto()}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)
            }catch (e:Exception){e.printStackTrace()}
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto()
            }
        }
    }

    //Ketika tombol Next di klik -> upload ke firebase
    fun nextClicked(view : View) {
        // Get the data from an ImageView as bytes
        // Get the data from an ImageView as bytes
        createSnapImageView?.setDrawingCacheEnabled(true)
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        //Membuat folder & menyimpan gambar di-dalamnya
        val ref = FirebaseStorage.getInstance().getReference().child("images").child(imageName)
        val uploadTask: UploadTask = ref.putBytes(data)
        uploadTask.addOnFailureListener {
            //1.Klo upload image gagal
            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener() {

            //2.Klo upload image berhasil
            //Ambil url image yg di-simpan
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) { task.exception?.let { throw it } }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                    //Log.i("URL",downloadUri)
                    //Pindah ke ChooseUserActivity
                    val intent = Intent(this,ChooseUserActivity::class.java)
                    intent.putExtra("imageURL",downloadUri.toString())
                    intent.putExtra("imageName",imageName)
                    intent.putExtra("message",messageEditText?.text.toString())
                    startActivity(intent)

                } else { Log.i("Failed File URL",it.toString()) }
            }



            }

        }

}
