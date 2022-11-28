package com.example.bookidcard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.database.*

class studentorders : AppCompatActivity() {
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studentorders)

        val intent = intent
        val userid = intent.getStringExtra("userid")
        setContentView(R.layout.studentorders)
      val  accept = findViewById<CardView>(R.id.acceptedcard)
      val  print = findViewById<CardView>(R.id.printedcard)
     val   deliver = findViewById<CardView>(R.id.deliveredcard)
      val  nooreders = findViewById<TextView>(R.id.no)
        databaseReference = FirebaseDatabase.getInstance().getReference("Students")
        databaseReference.child(userid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("orders")) {
                        val fbaccepted = snapshot.child("orders").child("accepted").getValue(
                            String::class.java
                        )
                        val fbprinted = snapshot.child("orders").child("printed").getValue(
                            String::class.java
                        )
                        val fbdelivered = snapshot.child("orders").child("delivered").getValue(
                            String::class.java
                        )
                        nooreders.visibility = View.INVISIBLE
                        if (fbaccepted == "true") {
                            accept.visibility = View.VISIBLE
                        }
                        if (fbprinted == "true") {
                            print.visibility = View.VISIBLE
                        }
                        if (fbdelivered == "true") {
                            deliver.visibility = View.VISIBLE
                        }
                    } else {
                        accept.visibility = View.INVISIBLE
                        print.visibility = View.INVISIBLE
                        deliver.visibility = View.INVISIBLE
                        nooreders.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@studentorders, "Failed", Toast.LENGTH_SHORT).show()
                }
            })
    }
}