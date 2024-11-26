package com.albert.polyhome

import android.bluetooth.BluetoothClass.Device
import android.content.Intent
import android.devicelock.DeviceId
import android.graphics.Color
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
import com.example.androidtp2.Api

class HouseActivity : AppCompatActivity() {

    private var houses: ArrayList<HouseData> = ArrayList();
//    private lateinit var housesAdapter: ArrayAdapter<HouseData>

    private var devices: ArrayList<DeviceData> = ArrayList();
    private lateinit var deviceAdapter: DeviceAdapter;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)

        //deviceAdapter = DeviceAdapter(this, devices);

        loadHouses()
//        housesAdapter = ArrayAdapter<HouseData>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, houses)
        //initDevicesListView()
//        initializeSpinners()
    }

//    public fun goBackToLogin(view: View){
//        finish()
//    }

    public fun onClickShutterFilter(view: View){

        val lstShutters:  ArrayList<DeviceData> = ArrayList();


        val btnGarages = findViewById<Button>(R.id.btnGarageFilter);
        val btnLights = findViewById<Button>(R.id.btnLightFilter);
        val btnShutter = findViewById<Button>(R.id.btnShutterFilter);

        btnGarages.setTextColor(Color.parseColor("#ffffff"))
        btnGarages.setBackgroundColor(Color.parseColor("#01053d"))

        btnLights.setTextColor(Color.parseColor("#ffffff"))
        btnLights.setBackgroundColor(Color.parseColor("#01053d"))

        btnShutter.setTextColor(Color.parseColor("#01053d"))
        btnShutter.setBackgroundColor(Color.parseColor("#ffffff"))

        for(device in devices){
            if(device.id.startsWith("S")){
                lstShutters.add(device);
            }
        }

        runOnUiThread{
            updateListDevices();
            initDevicesListView(lstShutters);
            updateListDevices();
        }

    }

    public fun onClickGarageFilter(view: View){

        val lstGarages:  ArrayList<DeviceData> = ArrayList();


        val btnLights = findViewById<Button>(R.id.btnLightFilter);
        val btnShutter = findViewById<Button>(R.id.btnShutterFilter);
        val btnGarages = findViewById<Button>(R.id.btnGarageFilter);

        btnLights.setTextColor(Color.parseColor("#ffffff"))
        btnLights.setBackgroundColor(Color.parseColor("#01053d"))

        btnShutter.setTextColor(Color.parseColor("#ffffff"))
        btnShutter.setBackgroundColor(Color.parseColor("#01053d"))

        btnGarages.setTextColor(Color.parseColor("#01053d"))
        btnGarages.setBackgroundColor(Color.parseColor("#ffffff"))

        for(device in devices){
            if(device.id.startsWith("G")){
                lstGarages.add(device);
            }
        }

        runOnUiThread{
            updateListDevices();
            initDevicesListView(lstGarages);
            updateListDevices();
        }

    }

    public fun onClickLightFilter(view: View){

        val lstLights:  ArrayList<DeviceData> = ArrayList();


        val btnShutter = findViewById<Button>(R.id.btnShutterFilter);
        val btnGarages = findViewById<Button>(R.id.btnGarageFilter);
        val btnLights = findViewById<Button>(R.id.btnLightFilter);


        btnGarages.setTextColor(Color.parseColor("#ffffff"))
        btnGarages.setBackgroundColor(Color.parseColor("#01053d"))

        btnShutter.setTextColor(Color.parseColor("#ffffff"))
        btnShutter.setBackgroundColor(Color.parseColor("#01053d"))

        btnLights.setTextColor(Color.parseColor("#01053d"))
        btnLights.setBackgroundColor(Color.parseColor("#ffffff"))

        for(device in devices){
            if(device.id.startsWith("L")){
                lstLights.add(device);
            }
        }

        runOnUiThread{
            clearListView();
            initDevicesListView(lstLights);
            updateListDevices();
        }

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
//                txtTitle.text = "PolyHome Chalet " + houses.first().houseId.toString()
//            }
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

//        runOnUiThread{
//            initDevicesListView();
//            initializeSpinners()
//            updateListDevices();
//        }

    }


    private fun updateListDevices() {
        DeviceAdapter(this, devices).notifyDataSetChanged();
    }
    private fun initDevicesListView(lstDevices: ArrayList<DeviceData>){
        val listView = findViewById<ListView>(R.id.listViewDevice);

//        listView.setOnItemClickListener { parent, view, position, id ->
//            val selectedDevice = devices[position];
//            performActionOnDevice(selectedDevice);
//
//        }

        listView.adapter = DeviceAdapter(this, lstDevices);
    }

    private fun performActionOnDevice(device: DeviceData) {
        // Exemple d'action : ouvrir une nouvelle activité ou exécuter une commande API
        Toast.makeText(this, "Action sur l'appareil ${device.id}", Toast.LENGTH_SHORT).show()


    }

    private fun clearListView() {
        runOnUiThread {
            val listView = findViewById<ListView>(R.id.listViewDevice)
            val emptyList = ArrayList<DeviceData>() // Crée une liste vide
            listView.adapter = DeviceAdapter(this, emptyList) // Associe l'adaptateur à la liste vide
            Toast.makeText(this, "Liste vidée", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun initializeSpinners(){
//        val lstSpinHouse = findViewById<Spinner>(R.id.spinHouse)
//
//        lstSpinHouse.adapter = DeviceAdapter(this, devices);
//    }
}