package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ChooseUserActivity : AppCompatActivity() {

    var chooseUserListView : ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var keys: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        chooseUserListView = findViewById(R.id.chooseUserListView)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        chooseUserListView?.adapter = adapter

        // Ambil semua email yg terkoneksi dari database Firebase
        // & tampilkan ke ListView
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(
            object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //Ambil email
                val email =  p0?.child("email")?.value as String
                emails.add(email)
                p0.key?.let { keys.add(it) }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })

        chooseUserListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, id ->
            //Transaksi Data Antar User

            //Buat mapping data baru unt ambil kiriman data dari CreateSnapActivity
            //val snapMap : Map<String,Any> = mapOf("from" to "","imageName" to "","imaneURL" to "","message" to "" )
            val snapMap : Map<String,String> = mapOf(
                "from" to FirebaseAuth.getInstance()!!.currentUser!!.email!!,
                "imageName" to intent.getStringExtra("imageName"),
                "imageURL" to intent.getStringExtra("imageURL"),
                "message" to intent.getStringExtra("message")
            )

            //Buat kolom baru (snaps) di Firebase (table users)
            //Simpan mapping data ke Firebase
            FirebaseDatabase.getInstance().getReference().child("users")
                .child(keys.get(i))
                .child("snaps")
                .push().setValue(snapMap)

            //Pindah ke SnapActivity
            val intent = Intent(this,SnapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }

    }
}