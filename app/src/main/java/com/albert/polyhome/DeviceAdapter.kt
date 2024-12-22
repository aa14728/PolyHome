package com.albert.polyhome

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class DeviceAdapter(
    private val context: Context,
    private val deviceSource: ArrayList<DeviceData>,
    private val onActionClick: (String, String) -> Unit
): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;

    override fun getCount(): Int {
        return deviceSource.size;
    }

    override fun getItem(position: Int): Any {
        return deviceSource[position];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.device_list_item, parent, false)

        val txtDeviceID = view.findViewById<TextView>(R.id.deviceId)
        val txtStatut = view.findViewById<TextView>(R.id.deviceStatut)
        val btnOpen = view.findViewById<Button>(R.id.btnOpen)
        val btnClose = view.findViewById<Button>(R.id.btnClose)
        val btnStop = view.findViewById<Button>(R.id.btnStop)
        val btnTurnOn = view.findViewById<Button>(R.id.btnTurnOn)
        val btnTurnOff = view.findViewById<Button>(R.id.btnTurnOff)

        val device = getItem(position) as DeviceData
        txtDeviceID.text = device.id

        if (device.type == "light") {
            if(device.power == 0) {
                txtStatut.text = "TURN OFF"
                txtStatut.setTextColor(Color.parseColor("#b20000"))
            }
            else if(device.power == 1) {
                txtStatut.text = "TURN ON"
                txtStatut.setTextColor(Color.parseColor("#008000"))
            }

            btnOpen.visibility = View.GONE
            btnClose.visibility = View.GONE
            btnStop.visibility = View.GONE
            btnTurnOn.visibility = View.VISIBLE
            btnTurnOff.visibility = View.VISIBLE

            btnTurnOn.setOnClickListener { onActionClick("TURN ON", device.id) }
            btnTurnOff.setOnClickListener { onActionClick("TURN OFF", device.id) }
        } else {
            if (device.openingMode == 0) {
                txtStatut.text = "OPEN"
                txtStatut.setTextColor(Color.parseColor("#008000"))
            }
            else if (device.openingMode == 1) {
                txtStatut.text = "CLOSE"
                txtStatut.setTextColor(Color.parseColor("#b20000"))
            }
            else {
                txtStatut.text = "STOP"
                txtStatut.setTextColor(Color.parseColor("#595959"))
            }

            btnOpen.visibility = View.VISIBLE
            btnClose.visibility = View.VISIBLE
            btnStop.visibility = View.VISIBLE
            btnTurnOn.visibility = View.GONE
            btnTurnOff.visibility = View.GONE

            btnOpen.setOnClickListener { onActionClick("OPEN", device.id) }
            btnClose.setOnClickListener { onActionClick("CLOSE", device.id) }
            btnStop.setOnClickListener { onActionClick("STOP", device.id) }
        }

        return view
    }

}