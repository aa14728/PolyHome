package com.albert.polyhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class GrantedAccessAdapter(
    private val context: Context,
    private var userSource: ArrayList<GrantedUserData>,
    private val onUnGrant: (String) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = userSource.size
    override fun getItem(position: Int): Any = userSource[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.granted_user_list_item, parent, false)

        val grantedUser = getItem(position) as GrantedUserData
        val txtGrantedUserLogin = view.findViewById<TextView>(R.id.grantedUserLogin)
        val btnUngrantedGrantedUser = view.findViewById<Button>(R.id.btnUngrantedGrantedUser)

        txtGrantedUserLogin.text = grantedUser.userLogin

        btnUngrantedGrantedUser.setOnClickListener {
            onUnGrant(grantedUser.userLogin)
        }

        return view
    }
}