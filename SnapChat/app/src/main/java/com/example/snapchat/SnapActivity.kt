package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_snap.*

class SnapActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    var snapsListView : ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps : ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)

        //Tampilkan semua snaps tiap user
        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        snapsListView?.adapter = adapter

        //Ambil data dari Firebase ke snapsListView
        mAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference()
                .child("users").child(it).child("snaps")
                .addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        val email =  p0?.child("from")?.value as String
                        emails.add(email)
                        snaps.add(p0!!)
                        adapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                    override fun onChildRemoved(p0: DataSnapshot) {
                        //Check klo snap yg di delete ada di list / tidak
                        var index = 0
                        for (snap : DataSnapshot in snaps){
                            if (snap.key == p0?.key){
                                snaps.removeAt(index)
                                emails.removeAt(index)
                            }
                            index++
                        }
                        adapter.notifyDataSetChanged()
                    }
                })

            //Saat salah satu snap di-klik akan pindah ke ViewSnapActivity
            snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, id ->
                val snapshot = snaps.get(i)
                val intent = Intent(this,ViewSnapActivity::class.java)
                intent.putExtra("snapKey",snapshot.key)
                intent.putExtra("from",snapshot.child("from").value as String)
                intent.putExtra("imageName",snapshot.child("imageName").value as String)
                intent.putExtra("imageURL",snapshot.child("imageURL").value as String)
                intent.putExtra("message",snapshot.child("message").value as String)
                startActivity(intent)
            }
        }

    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId == R.id.createSnap){
            //Pindah ke CreateSnap Activity
            val intent = Intent(this,CreateSnapActivity::class.java)
            startActivity(intent)

        }else if (item?.itemId == R.id.logout){
            mAuth.signOut() //signout firebase
            finish() //out from app
        }
        return super.onOptionsItemSelected(item)
    }

    //Saat user menekan back
    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut() //signout firebase
    }
}