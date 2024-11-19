package com.albert.polyhome

import android.bluetooth.BluetoothClass.Device
import android.content.Intent
import android.devicelock.DeviceId
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HouseActivity : AppCompatActivity() {

    private var houses: ArrayList<HouseData> = ArrayList();
//    private lateinit var housesAdapter: ArrayAdapter<HouseData>

    private var devices: ArrayList<DeviceData> = ArrayList();
    private lateinit var deviceAdapter: DeviceAdapter;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        deviceAdapter = DeviceAdapter(this, devices);

        loadHouses()
//        housesAdapter = ArrayAdapter<HouseData>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, houses)
        //initDevicesListView()
//        initializeSpinners()
    }

//    public fun goBackToLogin(view: View){
//        finish()
//    }

    public

    public fun loadHouses() {
        try {
            val tokenValue = intent.getStringExtra("logtoken");
            Api().get<List<HouseData>>(
                " https://polyhome.lesmoulinsdudev.com/api/houses",
                ::houseSuccess,
                tokenValue
            )
        }catch (e: Exception){
            Toast.makeText(this, e.message , Toast.LENGTH_SHORT).show();
        }

    }

    private fun houseSuccess(responseCode: Int, loadedHouses: List<HouseData>?) {
        if (responseCode == 200 && loadedHouses != null) {
            val txtTitle = findViewById<TextView>(R.id.houseId)
            for(house in loadedHouses)
                houses.add(house)

            runOnUiThread {
                txtTitle.text = "PolyHome Chalet " + houses.first().houseId.toString()
            }
            loadDevices(houses.first().houseId)
        }
    }


    public fun loadDevices(houseId: Int) {
        try {
            val tokenValue = intent.getStringExtra("logtoken");
            Api().get<DeviceList>(
                "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
                ::deviceSuccess,
                tokenValue
            )
        }catch (e: Exception){
            Toast.makeText(this, e.message , Toast.LENGTH_SHORT).show();
        }

    }

    private fun deviceSuccess(responseCode: Int, loadedDevices: DeviceList?) {
        runOnUiThread {
            if(responseCode == 200 && loadedDevices != null){
                Toast.makeText(this, responseCode.toString(), Toast.LENGTH_SHORT).show();
                for(device in loadedDevices.devices!!)
                    devices.add(device)
            }
            else if(responseCode == 403)
                Toast.makeText(this, "Accès interdit (token invalide ou ne correspondant pas au\n" +
                        " propriétaire de la maison ou à un tiers ayant accès)" , Toast.LENGTH_SHORT).show();
            else if(responseCode == 500)
                Toast.makeText(this, " Une erreur s’est produite au niveau du serveur" , Toast.LENGTH_SHORT).show();
        }

        runOnUiThread{
            initDevicesListView();
            initializeSpinners()
            updateListDevices();
        }

    }


    private fun updateListDevices() {
        DeviceAdapter(this, devices).notifyDataSetChanged();
    }
    private fun initDevicesListView(){
        val listView = findViewById<ListView>(R.id.listViewDevice);

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedDevice = devices[position];
            performActionOnDevice(selectedDevice);

        }

        listView.adapter = deviceAdapter;
    }

    private fun performActionOnDevice(device: DeviceData) {
        // Exemple d'action : ouvrir une nouvelle activité ou exécuter une commande API
        Toast.makeText(this, "Action sur l'appareil ${device.id}", Toast.LENGTH_SHORT).show()

        if()
    }

    private fun initializeSpinners(){
        val lstSpinHouse = findViewById<Spinner>(R.id.spinHouse)

        lstSpinHouse.adapter = deviceAdapter;
    }
}