package com.bpplatform.howlfirebaseauth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_read_database.*

class ReadDatabaseActivity : AppCompatActivity() {
    var realTimeArrayList = arrayListOf<UserDTO>()
    var realTimeKeyArrayList = arrayListOf<String>()
    var getArrayList = arrayListOf<UserDTO>()
    var listForFilter : ArrayList<UserDTO>? = null
    var city: String? = null
    var age: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_database)
        var userDTO = UserDTO("하울", 30, "송도")


        recyclerview_read_database.adapter = ReadRecyclderViewAdapter(getArrayList)
        recyclerview_read_database.layoutManager = LinearLayoutManager(this)


        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { querySnapshot ->
            for (item in querySnapshot.documents) {
                var userDTO = item.toObject(UserDTO::class.java)
                getArrayList.add(userDTO)
            }
            recyclerview_read_database.adapter.notifyDataSetChanged()

        }
        recyclerview_read_database_realtime.adapter = ReadRecyclderViewAdapter(realTimeArrayList)
        recyclerview_read_database_realtime.layoutManager = LinearLayoutManager(this)

//        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//
//            realTimeArrayList.clear()
//            for (item in querySnapshot.documents){
//                var userDTO = item.toObject(UserDTO::class.java)
//                realTimeArrayList.add(userDTO)
//            }
//            //Recyclerview 새로고침 코드
//            recyclerview_read_database_realtime.adapter.notifyDataSetChanged()
//
//        }
        FirebaseFirestore.getInstance().collection("users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->


            for (item in querySnapshot.documentChanges) {
                when (item.type) {
                    DocumentChange.Type.ADDED -> {

                        realTimeArrayList.add(item.document.toObject(UserDTO::class.java))
                        realTimeKeyArrayList.add(item.document.id)
                    }
                    DocumentChange.Type.MODIFIED -> motifyItem(item.document.id, item.document.toObject(UserDTO::class.java))
                    DocumentChange.Type.REMOVED -> deleteItem(item.document.id)
                }


            }
            //Recyclerview 새로고침 코드
            recyclerview_read_database_realtime.adapter.notifyDataSetChanged()

        }
        read_database_activity_spinner_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                city = p0!!.getItemAtPosition(p2) as String
                listBySpinner()
            }

        }

        read_database_activity_spinner_age.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                age = p0!!.getItemAtPosition(p2) as String
                listBySpinner()
            }

        }
        read_database_activity_edittext.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                searchList(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }
    fun searchList(filterString:String){
        var filterList = listForFilter!!.filter { userDTO ->
            //userDTO.name!!.contains(filterString)
            checkCharacter(userDTO.name!!,filterString)
        }
        getArrayList.clear()
        getArrayList.addAll(filterList)
        recyclerview_read_database.adapter.notifyDataSetChanged()

    }

    fun checkCharacter(name:String,searchString:String) : Boolean{
        //John Sophia => arrayOf("John","Sophia")
        var array = searchString.split(" ")
        for(item in array){
            if(name.contains(item))
                return true
        }
        return false

    }

    fun listBySpinner() {

        if (city != null && age != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .whereEqualTo("city", city)
                    .whereGreaterThanOrEqualTo("age", age!!.toInt())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        getArrayList.clear()
                        for (item in querySnapshot.documents) {
                            var userDTO = item.toObject(UserDTO::class.java)
                            getArrayList.add(userDTO)
                        }
                        listForFilter = getArrayList.clone() as ArrayList<UserDTO>
                        recyclerview_read_database.adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        println(exception.toString())
                    }
        }

    }

    fun motifyItem(motifyItem: String, userDTO: UserDTO) {
        for ((position, item) in realTimeKeyArrayList.withIndex()) {
            if (item == motifyItem) {
                realTimeArrayList[position] = userDTO
            }
        }

    }

    fun deleteItem(deleteKey: String) {

        for ((position, item) in realTimeKeyArrayList.withIndex())
            if (deleteKey == item) {
                realTimeArrayList.removeAt(position)
            }

    }

    class ReadRecyclderViewAdapter(initList: ArrayList<UserDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var list: ArrayList<UserDTO>? = initList

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_recyclerview, parent, false)
            return CustomViewHolder(view)
        }

        class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
            var textview_name = view!!.findViewById<TextView>(R.id.textView_name)
            var textview_age = view!!.findViewById<TextView>(R.id.textView_age)
            var textview_city = view!!.findViewById<TextView>(R.id.textView_city)

        }

        override fun getItemCount(): Int {
            return list!!.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            var customViewHolder = holder as CustomViewHolder
            customViewHolder.textview_name.text = list!!.get(position).name
            customViewHolder.textview_age.text = list!!.get(position).age.toString()
            customViewHolder.textview_city.text = list!!.get(position).city
        }

    }
}
