package com.demirli.a14helloexample

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var username: String? = null
    private var password: String? = null

    private var testUserName = "test"
    private var testPassword = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUi()
    }

    fun setupUi(){

        login_btn.setOnClickListener {

            if (username_et.text.toString() == ""){
                Toast.makeText(this,"Username field is empty", Toast.LENGTH_SHORT).show()
                username_et.setBackgroundColor(Color.YELLOW)
            }else{
                username = username_et.text.toString()
            }
            if (password_et.text.toString() == ""){
                Toast.makeText(this,"Password field is empty", Toast.LENGTH_SHORT).show()
                password_et.setBackgroundColor(Color.YELLOW)
            }else{
                password = password_et.text.toString()
            }
            if(username_et.text.toString() != "" &&  password_et.text.toString() != ""){
                check()
            }

        }

        clear_btn.setOnClickListener {

            username_et.setText("")
            username_et.setBackgroundColor(Color.WHITE)

            password_et.setText("")
            password_et.setBackgroundColor(Color.WHITE)

        }

    }

    fun check(){

        if (username == testUserName && password == testPassword){
            Toast.makeText(this,"Login successful", Toast.LENGTH_SHORT).show()
            username_et.setBackgroundColor(Color.BLUE)
            password_et.setBackgroundColor(Color.BLUE)

            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(Constants.INTENT_EXTRA_NAME, username)
            startActivity(intent)

            //login area

        }else if(username == testUserName && password != testPassword){
            Toast.makeText(this,"Wrong password.", Toast.LENGTH_SHORT).show()
            password_et.setBackgroundColor(Color.RED)
        }else if(username != testUserName){
            Toast.makeText(this,"Login failed.", Toast.LENGTH_SHORT).show()
            username_et.setBackgroundColor(Color.RED)
            password_et.setBackgroundColor(Color.RED)
        }
    }


}
