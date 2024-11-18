package com.albert.polyhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class DeviceAdapter(
    private val context: Context,
    private val deviceSource: ArrayList<DeviceData>
): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;

    override fun getCount(): Int {
//        TODO("Not yet implemented")
        return deviceSource.size;
    }

    override fun getItem(position: Int): Any {
//        TODO("Not yet implemented")
        return deviceSource[position];
    }

    override fun getItemId(position: Int): Long {
//        TODO("Not yet implemented")
        return position.toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        TODO("Not yet implemented")

        val view = inflater.inflate(R.layout.device_list_item, parent, false);

        val txtDeviceID = view.findViewById<TextView>(R.id.deviceId);

        val device = getItem(position) as DeviceData;

        txtDeviceID.text = device.id;

        return view;
    }
}