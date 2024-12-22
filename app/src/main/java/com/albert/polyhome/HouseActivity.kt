package com.albert.polyhome

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class HouseActivity : AppCompatActivity() {
    private var houses: ArrayList<HouseData> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_houses)

        loadHouses()
        runOnUiThread{
            initHousesListView(houses)
        }
    }

    private fun loadHouses() {
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
            for(house in loadedHouses)
                houses.add(house)
        }
    }

    private fun performActionOnDevice(house: HouseData){
        val dataValue = intent.getStringExtra("logtoken");
        val intent = Intent(this, DeviceActivity::class.java)

        intent.putExtra("selectedHouseId", house.houseId.toString());
        intent.putExtra("token", dataValue)

        startActivity(intent)
    }

    private fun initHousesListView(lstHouses: ArrayList<HouseData>){
        val listView = findViewById<ListView>(R.id.listViewHouse);

        listView.adapter = HouseAdapter(this, lstHouses);

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedHouse = listView.adapter.getItem(position) as HouseData;
            performActionOnDevice(selectedHouse);

        }
    }
}