package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var emailEditText : EditText? = null
    var passwordEditText : EditText? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.EmailEditText)
        passwordEditText = findViewById(R.id.PasswordEditText)

        if (mAuth.currentUser != null){
            logIn()
        }
    }

    fun goClicked(view : View){
        //Check if we can log in the user

        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        logIn()
                    } else {
                        //Sign up the user
                        mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                                .addOnCompleteListener(this){task ->
                                if (task.isSuccessful){
                                    //Add to database
                                    //Membuat folder users sesuai dengan id user saat sign-in
                                    task.result?.user?.uid?.let {
                                        FirebaseDatabase.getInstance().getReference().child("users").child(it)
                                            .child("email").setValue(emailEditText?.text.toString())
                                    };

                                    //-----------------------------
                                    logIn()
                                }else{ Toast.makeText(this,"Login Failed. Try Again",Toast.LENGTH_SHORT).show() }
                        }
                    }
                }
    }

    fun logIn(){
        //Move to Next Activity
        val intent = Intent(this,SnapActivity::class.java)
        startActivity(intent)
    }
}