package com.bpplatform.howlfirebaseauth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_database.*

class DatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
        button_database_create.setOnClickListener {
            startActivity(Intent(this,DatabaseCreateActivity::class.java))
        }
        button_database_read.setOnClickListener {
            startActivity(Intent(this,ReadDatabaseActivity::class.java))
        }
        button_database_read_orderby.setOnClickListener {
            startActivity(Intent(this,ReadOrderbyDatabaseActivity::class.java))
        }
        button_database_update_delete.setOnClickListener {
            startActivity(Intent(this,MotifyDatabaseActivity::class.java))
        }
    }
}
