package com.example.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    var idTextView : TextView? = null
    var emailTextView : TextView? = null
    var messageTextView : TextView? = null
    var imageNameTextView : TextView? = null
    var snapImageView : ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        idTextView = findViewById(R.id.idTextView)
        emailTextView = findViewById(R.id.emailTextView)
        messageTextView = findViewById(R.id.messageTextView)
        imageNameTextView = findViewById(R.id.imageNameTextView)
        snapImageView = findViewById(R.id.SnapImageView)

        idTextView?.text = intent.getStringExtra("snapKey")
        emailTextView?.text = intent.getStringExtra("from")
        messageTextView?.text = intent.getStringExtra("message")
        imageNameTextView?.text = intent.getStringExtra("imageName")



        //Setting imageView dari function ImageDownloader
        val task = ImageDownloader()
        val myImage : Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            snapImageView?.setImageBitmap(myImage)
        }catch (e:Exception){ e.printStackTrace() }
    }

    //Function unt download image dari ImageURL
    inner class ImageDownloader : AsyncTask<String,Void, Bitmap>(){
        override fun doInBackground(vararg urls: String?): Bitmap? {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
             return BitmapFactory.decodeStream(`in`)
            }catch (e:Exception){
                e.printStackTrace()
                return null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //1.Database : delete data snaps
        mAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference()
                .child("users").child(it)
                .child("snaps")
                .child(intent.getStringExtra("snapKey")).removeValue()
        }

        //2.Storage : delete data image
        FirebaseStorage.getInstance().getReference()
            .child("images")
            .child(intent.getStringExtra("imageName")).delete()

        //3.Update isi ListView yg ada di SnapActivity
        //  Pada function onChildRemoved
    }
}