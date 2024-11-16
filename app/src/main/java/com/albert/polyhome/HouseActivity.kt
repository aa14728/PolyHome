package com.albert.polyhome

import android.bluetooth.BluetoothClass.Device
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HouseActivity : AppCompatActivity() {

    private var houses: ArrayList<HouseData> = ArrayList();
    private lateinit var housesAdapter: ArrayAdapter<HouseData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house)

        loadHouses()
        housesAdapter = ArrayAdapter<HouseData>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, houses)
        initializeSpinners()
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
        if(responseCode == 200 && loadedHouses != null){
            for(house in loadedHouses)
                houses.add(house)
        }

    }

    private fun initializeSpinners(){
        val lstSpinHouse = findViewById<Spinner>(R.id.spinHouse)
        lstSpinHouse.adapter = housesAdapter;
    }
}