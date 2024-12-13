package com.albert.polyhome

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class Test : AppCompatActivity() {
    private var houses: ArrayList<HouseData> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_houses)

        //deviceAdapter = DeviceAdapter(this, devices);

        loadHouses()
        runOnUiThread{
            initHousesListView(houses)
        }
//        housesAdapter = ArrayAdapter<HouseData>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, houses)
        //initDevicesListView()
//        initializeSpinners()
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

//            runOnUiThread {
//                txtTitle.text = "PolyHome Chalet";
//            }
        }
    }

    private fun initHousesListView(lstHouses: ArrayList<HouseData>){
        val listView = findViewById<ListView>(R.id.listViewHouse);

//        listView.setOnItemClickListener { parent, view, position, id ->
//            val selectedDevice = devices[position];
//            performActionOnDevice(selectedDevice);
//
//        }

        listView.adapter = HouseAdapter(this, lstHouses);
    }
}