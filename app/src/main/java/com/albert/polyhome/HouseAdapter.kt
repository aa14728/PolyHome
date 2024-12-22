package com.albert.polyhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class HouseAdapter(
    private val context: Context,
    private val houseSource: ArrayList<HouseData>
): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;

    override fun getCount(): Int {
        return houseSource.size;
    }

    override fun getItem(position: Int): Any {
        return houseSource[position];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = inflater.inflate(R.layout.house_list_item, parent, false);

        val txtHouseID = view.findViewById<TextView>(R.id.housId);

        val house = getItem(position) as HouseData;

        txtHouseID.text = "Chalet " + house.houseId.toString();

        return view;
    }
}