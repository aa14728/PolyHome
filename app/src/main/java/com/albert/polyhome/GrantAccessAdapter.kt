package com.albert.polyhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import java.util.Locale

class GrantAccessAdapter(
    private val context: Context,
    private var userSource: ArrayList<UserData>,
    private val onGrant: (String) -> Unit
) : BaseAdapter() {

    private var filteredUserList: ArrayList<UserData> = ArrayList(userSource)

    override fun getCount(): Int = filteredUserList.size
    override fun getItem(position: Int): Any = filteredUserList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false)

        val user = getItem(position) as UserData
        val txtUserLogin = view.findViewById<TextView>(R.id.userLogin)
        val btnGrantUser = view.findViewById<Button>(R.id.btnGrantUser)

        txtUserLogin.text = user.login

        btnGrantUser.setOnClickListener {
            onGrant(user.login)
        }

        return view
    }

    fun filter(query: String) {
        val normalizedQuery = query.trim().lowercase(Locale.ROOT)
        filteredUserList = if (normalizedQuery.isEmpty()) {
            ArrayList(userSource)
        } else {
            ArrayList(userSource.filter {
                it.login.trim().lowercase(Locale.ROOT).contains(normalizedQuery)
            }.sortedWith(compareBy {
                if (it.login.trim().lowercase(Locale.ROOT).startsWith(normalizedQuery)) 0 else 1
            }))
        }
        notifyDataSetChanged()
    }

}

