package com.albert.polyhome

import android.content.Context
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
        val view = inflater.inflate(R.layout.device_list_item, parent, false)

        val txtDeviceID = view.findViewById<TextView>(R.id.deviceId)
        val txtStatut = view.findViewById<TextView>(R.id.deviceStatut)
        val btnOpen = view.findViewById<Button>(R.id.btnOpen)
        val btnClose = view.findViewById<Button>(R.id.btnClose)
        val btnStop = view.findViewById<Button>(R.id.btnStop)
        val btnTurnOn = view.findViewById<Button>(R.id.btnTurnOn)
        val btnTurnOff = view.findViewById<Button>(R.id.btnTurnOff)

        // Récupérer le device actuel
        val device = getItem(position) as DeviceData
        txtDeviceID.text = device.id

        // Cacher/afficher les boutons selon le type d'appareil
        if (device.type == "light") {
            if(device.power == 0)
                txtStatut.text = "TURN OFF"
            else if(device.power == 1)
                txtStatut.text = "TURN ON"
            // Si c'est une light, cacher les boutons OPEN, CLOSE, STOP et afficher TURN ON / TURN OFF
            btnOpen.visibility = View.GONE
            btnClose.visibility = View.GONE
            btnStop.visibility = View.GONE
            btnTurnOn.visibility = View.VISIBLE
            btnTurnOff.visibility = View.VISIBLE

            // Ajouter les listeners pour les boutons de lumière
            btnTurnOn.setOnClickListener { onActionClick("TURN ON", device.id) }
            btnTurnOff.setOnClickListener { onActionClick("TURN OFF", device.id) }
        } else {
            if (device.openingMode == 0) txtStatut.text = "OPEN"
            else if (device.openingMode == 1) txtStatut.text = "CLOSE"
            else txtStatut.text = "STOP"

            // Sinon, c'est un appareil de type Shutter ou Garage, on garde OPEN, CLOSE et STOP
            btnOpen.visibility = View.VISIBLE
            btnClose.visibility = View.VISIBLE
            btnStop.visibility = View.VISIBLE
            btnTurnOn.visibility = View.GONE
            btnTurnOff.visibility = View.GONE

            // Ajouter les listeners pour les autres appareils
            btnOpen.setOnClickListener { onActionClick("OPEN", device.id) }
            btnClose.setOnClickListener { onActionClick("CLOSE", device.id) }
            btnStop.setOnClickListener { onActionClick("STOP", device.id) }
        }

        return view
    }

}