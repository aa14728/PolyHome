package com.albert.polyhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.androidtp2.Api
import java.sql.Struct

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    public fun goToLogin(view: View)
    {
        finish();
    }

    public fun register(view: View){
        val txtValueRegisterName = findViewById<TextView>(R.id.txtRegisterName)
        val txtValueRegisterPassword = findViewById<TextView>(R.id.txtRegisterPassword)

        val rgstData = RegisterData(txtValueRegisterName.text.toString(), txtValueRegisterPassword.text.toString())

        Api().post<RegisterData>("https://polyhome.lesmoulinsdudev.com/api/users/register", rgstData, ::registerSuccess)
    }

    public fun registerSuccess(responseCode: Int){
        if(responseCode == 200){
            Toast.makeText(this, "Votre compte à bien été créé" , Toast.LENGTH_SHORT).show() // in Activity
            finish()
        }

    }
}