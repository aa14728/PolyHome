package com.albert.polyhome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.androidtp2.Api
import kotlinx.coroutines.MainScope


class LoginActivity : AppCompatActivity() {
    private val mainScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeButtons()
    }

    public fun registerNewAccount()
    {
        val intent = Intent(this, RegisterActivity::class.java);
        startActivity(intent);
    }

    private fun initializeButtons()
    {
        val goToRegisterButton = findViewById<Button>(R.id.btnGoToRegister);
        goToRegisterButton.setOnClickListener {
            registerNewAccount()
        }
    }

    public fun login(view: View){
        val txtValueConnexionLogin = findViewById<TextView>(R.id.txtLogin)
        val txtValueConnexionPassword = findViewById<TextView>(R.id.txtPassword)

        val loginData = LoginData(txtValueConnexionLogin.text.toString(), txtValueConnexionPassword.text.toString())
//        val loginData = LoginData("ioplkm", "123")

        Api().post<LoginData, LoginResponse>("https://polyhome.lesmoulinsdudev.com/api/users/auth", loginData, ::loginSuccess)
    }

    public fun loginSuccess(responseCode: Int, loginResponse: LoginResponse?){
        runOnUiThread {
            try {
                if(responseCode == 200 && loginResponse != null){
                    Toast.makeText(this, "Connexion réussie" , Toast.LENGTH_SHORT).show();
                    val intent = Intent(this, Test::class.java);
                    intent.putExtra("logtoken", loginResponse.token);
                    startActivity(intent);
                    finish();
                }
                else if(responseCode == 404)
                    Toast.makeText(this, "Aucun utilisateur ne correspond aux identifiants donnés" , Toast.LENGTH_SHORT).show();
                else if(responseCode == 500)
                    Toast.makeText(this, " Une erreur s’est produite au niveau du serveur" , Toast.LENGTH_SHORT).show();

            }catch (e: Exception){
                Toast.makeText(this, e.message , Toast.LENGTH_SHORT).show();
            }
        }
    }
}