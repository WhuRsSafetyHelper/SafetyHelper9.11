package com.example.lenovo.safetyhelper

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    private inner class MyButtonListener : View.OnClickListener {

        override fun onClick(view: View) {
            val intent = Intent(this@StartActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        buttonEnter.setOnClickListener(MyButtonListener())
    }
}
