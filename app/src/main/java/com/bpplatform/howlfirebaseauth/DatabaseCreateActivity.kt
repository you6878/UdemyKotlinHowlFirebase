package com.bpplatform.howlfirebaseauth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_database_create.*

class DatabaseCreateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_create)
        button_database_create_input.setOnClickListener {
            createData()
        }
    }
    fun createData(){
        var userDTO = UserDTO(editText_database_name.text.toString(),editText_database_age.text.toString().toInt(),editText_database_city.text.toString())
        FirebaseFirestore.getInstance().collection("users").document().set(userDTO).addOnSuccessListener {
            Toast.makeText(this,"데이터 입력이 성공하였습니다.",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            exception ->
            Toast.makeText(this,exception.toString(),Toast.LENGTH_LONG).show()
        }
    }
}
