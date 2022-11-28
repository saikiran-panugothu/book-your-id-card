package com.example.bookidcard

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

class login : AppCompatActivity() {
    lateinit var alertdialog : AlertDialog.Builder
lateinit var databaseReference : DatabaseReference
lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
     val    btnlogin = findViewById<Button>(R.id.btnlogin)
      val   etpassword = findViewById<TextInputLayout>(R.id.etpassword)
       val  etusername = findViewById<TextInputLayout>(R.id.edusername)
        btnlogin.setOnClickListener {
            val password = etpassword.editText!!.text.toString()
            val username = etusername.editText!!.text.toString()
            if (!username.isEmpty()) {
                etusername.error = null
                etusername.isErrorEnabled = false
                if (!password.isEmpty()) {
                    etpassword.error = null
                    etpassword.isErrorEnabled = false
                    progressDialog = ProgressDialog(this@login)
                    progressDialog.setTitle("logging...........")
                    progressDialog.show()
                    readData(password, username)
                } else {
                    etpassword.error = "enter password"
                }
            } else {
                etusername.error = "enter username"
            }
        }
    }
    private fun readData(password: String, username: String) {
       databaseReference = FirebaseDatabase.getInstance().getReference("Students")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(username)) {
                    val fbpassword = snapshot.child(username).child("password").getValue(
                        String::class.java
                    )
                    if (password == fbpassword) {
                        progressDialog.cancel()
                        Toast.makeText(this@login, "login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@login, home::class.java)
                        intent.putExtra("userid", username)
                        startActivity(intent)
                        finish()
                    } else {
                        progressDialog.cancel()
                        Toast.makeText(this@login, "Wrong password", Toast.LENGTH_LONG).show()
                    }
                } else {
                    progressDialog.cancel()
                    Toast.makeText(this@login, "Student not found", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.cancel()

                Toast.makeText(this@login, "Failed to fetch from Database", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBackPressed() {
        alertdialog = AlertDialog.Builder(this@login)
        alertdialog.setIcon(R.drawable.ic_baseline_info_24)
        alertdialog.setTitle("Confirm!")
        alertdialog.setMessage("are you sure want exit?")
        alertdialog.setPositiveButton("Yes") { dialog, which ->
            super.onBackPressed()
            finish()
        }
        alertdialog.setNegativeButton("No", null)
        alertdialog.show()
    }
}
