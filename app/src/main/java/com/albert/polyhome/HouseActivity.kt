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
import androidx.appcompat.app.AppCompatActivity

class HouseActivity : AppCompatActivity() {

    private var houses: ArrayList<HouseData> = ArrayList();
    private lateinit var housesAdapter: ArrayAdapter<HouseData>

    private var devices: ArrayList<DeviceData> = ArrayList();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        loadHouses()
        housesAdapter = ArrayAdapter<HouseData>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, houses)
        //initDevicesListView()
//        initializeSpinners()
    }

    public fun goBackToLogin(view: View){
        finish()
    }

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
//            val houseId = houses.first().houseId;
            Api().get<List<DeviceData>>(
                " https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
                ::deviceSuccess,
                tokenValue
            )
        }catch (e: Exception){
            Toast.makeText(this, e.message , Toast.LENGTH_SHORT).show();
        }

    }

    private fun deviceSuccess(responseCode: Int, loadedDevices: List<DeviceData>?) {
        if(responseCode == 200 && loadedDevices != null){
            for(device in loadedDevices)
                devices.add(device)
        }
        runOnUiThread{
            initDevicesListView();
            updateListDevices();
        }

    }

    private fun updateListDevices() {
        DeviceAdapter(this, devices).notifyDataSetChanged();
    }
    private fun initDevicesListView(){
        val listView = findViewById<ListView>(R.id.listViewDevice);
        listView.adapter = DeviceAdapter(this, devices);
    }


//    private fun initializeSpinners(){
//        val lstSpinHouse = findViewById<Spinner>(R.id.spinHouse)
//        lstSpinHouse.adapter = housesAdapter;
//    }
}