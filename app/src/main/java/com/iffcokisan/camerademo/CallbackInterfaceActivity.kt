package com.iffcokisan.camerademo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.iffcokisan.camerademo.interfaces.UpdateNotification

class CallbackInterfaceActivity : AppCompatActivity() {

    val mainActivity = MainActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_callback_interface)

        findViewById<Button>(R.id.button6).setOnClickListener {
            /* mainActivity.(true)
             finish()*/
        }
    }

}
