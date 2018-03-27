package com.bpplatform.howlfirebaseauth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_read_orderby_database.*

class ReadOrderbyDatabaseActivity : AppCompatActivity() {
    var direction: Query.Direction? = null
    var startAge: String? = null
    var endAge: String? = null
    var limit: String? = null
    var getList: ArrayList<UserDTO> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_orderby_database)

        read_orderby_database_recyclerview.adapter = ReadDatabaseActivity.ReadRecyclderViewAdapter(getList)
        read_orderby_database_recyclerview.layoutManager = LinearLayoutManager(this)

        read_orderby_database_spinner_direction.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var selectString = p0!!.getItemAtPosition(p2) as String
                if (selectString == "ASC") {
                    direction = Query.Direction.ASCENDING
                } else {
                    direction = Query.Direction.DESCENDING
                }
                getListFromFireStore()
            }

        }
        read_orderby_database_spinner_age_start.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                startAge = p0!!.getItemAtPosition(p2) as String
                getListFromFireStore()
            }

        }
        read_orderby_database_spinner_age_end.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                endAge = p0!!.getItemAtPosition(p2) as String
                getListFromFireStore()
            }

        }
        read_orderby_database_spinner_limit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                limit = p0!!.getItemAtPosition(p2) as String
                getListFromFireStore()
            }

        }
    }

    fun getListFromFireStore() {
        if (direction != null && startAge != null && endAge != null && limit != null) {


            FirebaseFirestore.getInstance().collection("users")
                    .orderBy("age", direction!!)
                    .startAt(startAge!!.toInt())
                    .endAt(endAge!!.toInt())
                    .limit(limit!!.toLong())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        getList.clear()
                        for (item in querySnapshot.documents) {
                            var userDTO = item.toObject(UserDTO::class.java)
                            getList.add(userDTO)
                        }
                        read_orderby_database_recyclerview.adapter.notifyDataSetChanged()

                    }
        }
    }
}
