package com.albert.polyhome

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class GrantAccessActivity : AppCompatActivity() {

    private var users: ArrayList<UserData> = ArrayList() // Tous les utilisateurs
    private var grantedUsers: ArrayList<GrantedUserData> = ArrayList() // Utilisateurs ayant déjà accès
    private lateinit var userAdapter: GrantAccessAdapter
    private lateinit var grantedUserAdapter: GrantedAccessAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grant)

        loadUsers()
        loadGrantedUsers()

        searchUser()

        findViewById<Button>(R.id.btnCreateUser).setOnClickListener {
            Toast.makeText(this, "Créer un utilisateur (fonctionnalité à implémenter)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViewUser(lstUsers: ArrayList<UserData>){
        val listViewUsers = findViewById<ListView>(R.id.listViewUsers)

        userAdapter = GrantAccessAdapter(this, lstUsers, ::grantAccess, ::ungrantAccess)
        listViewUsers.adapter = userAdapter
    }

    private fun initViewGrantedUser(lstGrantedUsers: ArrayList<GrantedUserData>){
        val listViewGrantedUsers = findViewById<ListView>(R.id.listViewGrantedUsers)

        grantedUserAdapter = GrantedAccessAdapter(this, lstGrantedUsers, ::ungrantAccess)
        listViewGrantedUsers.adapter = grantedUserAdapter
    }

    fun searchUser(){
        runOnUiThread {
            searchView = findViewById(R.id.searchView)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    userAdapter.filter(newText ?: "")
                    return true
                }
            })
        }
    }

    // Charger tous les utilisateurs
    private fun loadUsers() {
        val tokenValue = intent.getStringExtra("token") ?: ""
        Api().get<List<UserData>>(
            "https://polyhome.lesmoulinsdudev.com/api/users",
            ::loadUsersSucess,
            tokenValue
        )
    }

    private fun loadUsersSucess(responseCode: Int, loadedUsers: List<UserData>?){
        runOnUiThread {
            if (responseCode == 200 && loadedUsers != null) {
                users.clear()
                users.addAll(loadedUsers)
                initViewUser(users)
                userAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Erreur lors du chargement des utilisateurs.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadGrantedUsers() {
        val tokenValue = intent.getStringExtra("token") ?: ""
        val houseId = intent.getStringExtra("selectedHouseId") ?: ""
        Api().get<List<GrantedUserData>>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            ::loadGrantedUsersSuccess,
            tokenValue
        )
    }

    private fun loadGrantedUsersSuccess(responseCode: Int, loadedGrantedUsers: List<GrantedUserData>?){
        runOnUiThread {
            if (responseCode == 200 && loadedGrantedUsers != null) {
                grantedUsers.clear()
                grantedUsers.addAll(loadedGrantedUsers)
                initViewGrantedUser(grantedUsers)
                grantedUserAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Erreur $responseCode lors du chargement des utilisateurs avec accès.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Accorder l'accès à un utilisateur
    private fun grantAccess(userLogin: String) {
        val tokenValue = intent.getStringExtra("token") ?: ""
        val houseId = intent.getStringExtra("selectedHouseId") ?: ""

        Api().post<GrantAccesData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            GrantAccesData(userLogin),
            ::grantAccessSuccess,
            tokenValue
        )
    }

    // Réponse après l'octroi d'un accès
    private fun grantAccessSuccess(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Accès accordé.", Toast.LENGTH_SHORT).show()
                loadGrantedUsers() // Rafraîchir la liste des utilisateurs avec accès
            } else {
                Toast.makeText(this, "Erreur $responseCode", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Révoquer l'accès d'un utilisateur
    private fun ungrantAccess(userLogin: String) {
        val tokenValue = intent.getStringExtra("token") ?: ""
        val houseId = intent.getStringExtra("selectedHouseId") ?: ""

        Api().delete<GrantAccesData>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",
            GrantAccesData(userLogin),
            ::ungrantAccessSuccess,
            tokenValue
        )
    }

    // Réponse après la révocation d'un accès
    private fun ungrantAccessSuccess(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Accès révoqué.", Toast.LENGTH_SHORT).show()
                loadGrantedUsers() // Rafraîchir la liste des utilisateurs avec accès
            } else {
                Toast.makeText(this, "Erreur $responseCode", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
