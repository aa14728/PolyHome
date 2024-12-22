package com.albert.polyhome

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class DeviceActivity : AppCompatActivity() {

    private var devices: ArrayList<DeviceData> = ArrayList()
    private var currentFilter: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 30000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        loadDevices()

        startDeviceUpdates()
    }

    private fun loadDevices() {
        try {
            val tokenValue = intent.getStringExtra("token") ?: ""
            val houseId = intent.getStringExtra("selectedHouseId") ?: ""
            val txtMainTitle = findViewById<TextView>(R.id.mainTitle)
            runOnUiThread {
                txtMainTitle.text = "Chalet $houseId"
            }

            if (tokenValue.isBlank() || houseId.isBlank()) {
                Toast.makeText(this, "Token ou ID de maison manquant.", Toast.LENGTH_SHORT).show()
                return
            }

            Api().get<DeviceList>(
                "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
                ::deviceSuccess,
                tokenValue
            )
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deviceSuccess(responseCode: Int, loadedDevices: DeviceList?) {
        runOnUiThread {
            when (responseCode) {
                200 -> {
                    if (loadedDevices != null) {
                        devices.clear()
                        devices.addAll(loadedDevices.devices!!)
                        filterDevicesByType(currentFilter)
                    }
                }
                403 -> Toast.makeText(this, "Accès interdit.", Toast.LENGTH_SHORT).show()
                500 -> Toast.makeText(this, "Erreur serveur.", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Erreur inconnue : $responseCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendCommandToDevice(action: String, deviceId: String) {
        val tokenValue = intent.getStringExtra("token") ?: ""
        val houseId = intent.getStringExtra("selectedHouseId") ?: ""

        if (tokenValue.isBlank() || houseId.isBlank() || deviceId.isBlank()) {
            Toast.makeText(this, "Données manquantes pour effectuer l'action.", Toast.LENGTH_SHORT).show()
            return
        }

        val commandData = CommandData(action)

        Api().post<CommandData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", commandData, ::sendCommandToDeviceSuccess, tokenValue)
    }


    private fun sendCommandToDeviceSuccess(responseCode: Int?) {
        runOnUiThread {
            android.util.Log.d("DEBUG", "Response Code: $responseCode")

            when (responseCode) {
                200 -> {
                    Toast.makeText(this, "Commande exécutée avec succès pour l'appareil", Toast.LENGTH_SHORT).show()
                    loadDevices()
                }
                400 -> Toast.makeText(this, "Erreur 400 : Requête invalide. Vérifiez les données envoyées.", Toast.LENGTH_SHORT).show()
                403 -> Toast.makeText(this, "Accès interdit. Vérifiez votre token.", Toast.LENGTH_SHORT).show()
                500 -> Toast.makeText(this, "Erreur serveur. Réessayez plus tard.", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Erreur inconnue : $responseCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun onClickUsers(view: View){
        val tokenValue = intent.getStringExtra("token") ?: ""
        val houseId = intent.getStringExtra("selectedHouseId") ?: ""

        if (tokenValue.isNotBlank() && houseId.isNotBlank()) {
            val intent = Intent(this, GrantAccessActivity::class.java)
            intent.putExtra("token", tokenValue)
            intent.putExtra("selectedHouseId", houseId)
            startActivity(intent)
        } else {
            runOnUiThread {
                Toast.makeText(this, "Token ou ID de maison manquant.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    public fun onClickOpenAll(view: View) {
        if (devices.isNotEmpty()) {
            val filteredDevices = devices.filter { it.id.startsWith(currentFilter) }
            if(currentFilter == "L"){
                for (device in filteredDevices) {
                    sendCommandToDevice("TURN ON", device.id)
                }
            } else {
                for (device in filteredDevices) {
                    sendCommandToDevice("OPEN", device.id)
                }
            }
        } else {
            Toast.makeText(this, "Aucun appareil correspondant au filtre.", Toast.LENGTH_SHORT).show()
        }
    }

    public fun onClickCloseAll(view: View) {
        if (devices.isNotEmpty()) {
            val filteredDevices = devices.filter { it.id.startsWith(currentFilter) }
            if(currentFilter == "L"){
                for (device in filteredDevices) {
                    sendCommandToDevice("TURN OFF", device.id)
                }
            } else {
                for (device in filteredDevices) {
                    sendCommandToDevice("CLOSE", device.id)
                }
            }
        } else {
            Toast.makeText(this, "Aucun appareil correspondant au filtre.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initDevicesListView(lstDevices: ArrayList<DeviceData>) {
        val listView = findViewById<ListView>(R.id.listViewDevice)
        listView.adapter = DeviceAdapter(this, lstDevices) { action, deviceId ->
            sendCommandToDevice(action, deviceId)
        }
    }

    private fun filterDevicesByType(type: String) {
        currentFilter = type
        val filteredDevices = devices.filter { it.id.startsWith(type) }
        initDevicesListView(ArrayList(filteredDevices))
    }

    fun onClickShutterFilter(view: View) = updateFilterUI(view, "S")
    fun onClickGarageFilter(view: View) = updateFilterUI(view, "G")
    fun onClickLightFilter(view: View) = updateFilterUI(view, "L")

    private fun updateFilterUI(view: View, type: String) {
        val btnShutter = findViewById<Button>(R.id.btnShutterFilter)
        val btnGarage = findViewById<Button>(R.id.btnGarageFilter)
        val btnLight = findViewById<Button>(R.id.btnLightFilter)

        listOf(btnShutter, btnGarage, btnLight).forEach {
            it.setTextColor(Color.WHITE)
            it.setBackgroundColor(Color.parseColor("#01053d"))
        }

        (view as Button).apply {
            setTextColor(Color.parseColor("#01053d"))
            setBackgroundColor(Color.WHITE)
        }

        filterDevicesByType(type)
    }

    private fun startDeviceUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                loadDevices()
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }
}
