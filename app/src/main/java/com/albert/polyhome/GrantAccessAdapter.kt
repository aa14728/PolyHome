package com.albert.polyhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class GrantAccessAdapter(
    private val context: Context,
    private var userSource: ArrayList<UserData>,
    private val onGrant: (String) -> Unit,
    private val onUnGrant: (String) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = userSource.size
    override fun getItem(position: Int): Any = userSource[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false)

        val user = getItem(position) as UserData
        val txtUserLogin = view.findViewById<TextView>(R.id.userLogin)
        val btnGrantUser = view.findViewById<Button>(R.id.btnGrantUser)

        txtUserLogin.text = user.login

        // Gestion des clics sur GRANT
        btnGrantUser.setOnClickListener {
            onGrant(user.login)
        }

        return view
    }

    // Méthode pour filtrer la liste en fonction du texte recherché
    fun filter(query: String) {
        userSource = userSource.filter { it.login.contains(query, true) } as ArrayList<UserData>
        notifyDataSetChanged()
    }
}
