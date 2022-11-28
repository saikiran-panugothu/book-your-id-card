package com.example.bookidcard

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class home : AppCompatActivity() {
    lateinit var drawerLayout : DrawerLayout
    lateinit var  navigationView : NavigationView
    lateinit var toolbar : Toolbar
    lateinit var alertdialog :  AlertDialog.Builder
    lateinit var databaseReference : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawablelayout)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerlayout)
        navigationView = findViewById<NavigationView>(R.id.navigationView)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.opendrawer,
            R.string.closedrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
       alertdialog = AlertDialog.Builder(this@home)
        alertdialog.setIcon(R.drawable.ic_baseline_info_24)
        alertdialog.setTitle("Confirm!")
        alertdialog.setMessage("are you sure want to logout?")
        alertdialog.setPositiveButton("Yes") { dialog, which ->
            startActivity(Intent(applicationContext, login::class.java))
            finish()
        }
        alertdialog.setNegativeButton("No", null)
        val intent = intent
        val userid = intent.getStringExtra("userid")
        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val id = item.itemId
            if (id == R.id.home) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else if (id == R.id.ordersdetails) {
                val intent1 = Intent(applicationContext, studentorders::class.java)
                intent1.putExtra("userid", userid)
                startActivity(intent1)
            } else {
                alertdialog.show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        })

       val  studentname = findViewById<TextView>(R.id.stuname)
     val   rollNo = findViewById<TextView>(R.id.rollno)
       val courseBranch = findViewById<TextView>(R.id.course_branch)
       val studentbatch = findViewById<TextView>(R.id.batch)
       val studentporfile = findViewById<ImageView>(R.id.stu_img)


        databaseReference = FirebaseDatabase.getInstance().getReference("Students")
        databaseReference.child(userid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                val batch = snapshot.child("batch").getValue(String::class.java)
                val coursebranch = snapshot.child("coursebranch").getValue(
                    String::class.java
                )
                val id = snapshot.child("id").getValue(String::class.java)
                val img = snapshot.child("pimage").getValue(String::class.java)
                Picasso.get().load(img).into(studentporfile)
                studentname.setText(name)
                studentbatch.setText(batch)
                courseBranch.setText(coursebranch)
                rollNo.setText(id)
                val profilePic = findViewById<ImageView>(R.id.profilepic)
                val profileName = findViewById<TextView>(R.id.profilename)
                val profileId = findViewById<TextView>(R.id.profileid)
                Picasso.get().load(img).into(profilePic)
               profileId.text=id
                profileName.text=name
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@home, "failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
      val  book = findViewById<Button>(R.id.bookbutton)
        book.setOnClickListener(View.OnClickListener {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Students")
            val orderDetails = OrderDetails(userid, "idcard", "true", "false", "false", "false")
            databaseReference.child(userid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("orders")) {
                            val dialog = Dialog(this@home)
                            dialog.setContentView(R.layout.alreadyplaced)
                            val Abtnok = dialog.findViewById<Button>(R.id.aokbtn)
                            Abtnok.setOnClickListener { dialog.dismiss()}
                            dialog.show()
                        } else {
                            databaseReference.child(userid).child("orders").setValue(orderDetails)
                                .addOnSuccessListener {
                                    val dialog = Dialog(this@home)
                                    dialog.setContentView(R.layout.ordersplaceddailog)
                                    val btnok = dialog.findViewById<Button>(R.id.okbtn)
                                    btnok.setOnClickListener { dialog.dismiss() }
                                    dialog.show()
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@home, "failed", Toast.LENGTH_SHORT).show()
                    }
                })
        })
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
