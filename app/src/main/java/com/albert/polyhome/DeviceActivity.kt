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
    private var currentFilter: String = ""  // Variable pour garder le filtre actuel
    private val handler = Handler(Looper.getMainLooper()) // Handler pour exécuter les actions périodiquement
    private val updateInterval = 30000L // Intervalle de 30 secondes pour récupérer les appareils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        // Charger les appareils au démarrage
        loadDevices()

        // Démarrer la mise à jour périodique
        startDeviceUpdates()
    }

    // Charger les appareils via l'API
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

    // Gestion de la réponse pour le chargement des appareils
    private fun deviceSuccess(responseCode: Int, loadedDevices: DeviceList?) {
        runOnUiThread {
            when (responseCode) {
                200 -> {
                    if (loadedDevices != null) {
                        devices.clear()
                        devices.addAll(loadedDevices.devices!!)
                        // Appliquer le filtre après avoir chargé les appareils
                        filterDevicesByType(currentFilter)  // Appliquer le filtre actuel
                    }
                }
                403 -> Toast.makeText(this, "Accès interdit.", Toast.LENGTH_SHORT).show()
                500 -> Toast.makeText(this, "Erreur serveur.", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Erreur inconnue : $responseCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun sendCommandToDevice(action: String, deviceId: String) {
        val tokenValue = intent.getStringExtra("token") ?: ""
        val houseId = intent.getStringExtra("selectedHouseId") ?: ""

        if (tokenValue.isBlank() || houseId.isBlank() || deviceId.isBlank()) {
            Toast.makeText(this, "Données manquantes pour effectuer l'action.", Toast.LENGTH_SHORT).show()
            return
        }

        val commandData = CommandData(action)

        Api().post<CommandData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", commandData, ::sendCommandToDeviceSuccess, tokenValue)
    }

    // Actualiser les appareils après l'envoi de la commande
    public fun sendCommandToDeviceSuccess(responseCode: Int?) {
        runOnUiThread {
            android.util.Log.d("DEBUG", "Response Code: $responseCode")

            when (responseCode) {
                200 -> {
                    Toast.makeText(this, "Commande exécutée avec succès pour l'appareil", Toast.LENGTH_SHORT).show()
                    // Rafraîchir la liste des appareils après une commande
                    loadDevices()  // Recharger les appareils et appliquer immédiatement le filtre
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


    // Initialiser la liste des appareils
    private fun initDevicesListView(lstDevices: ArrayList<DeviceData>) {
        val listView = findViewById<ListView>(R.id.listViewDevice)
        listView.adapter = DeviceAdapter(this, lstDevices) { action, deviceId ->
            sendCommandToDevice(action, deviceId)
        }
    }

    // Méthode pour filtrer les appareils par type
    private fun filterDevicesByType(type: String) {
        // Conserver le filtre actuel pour pouvoir le réappliquer après mise à jour
        currentFilter = type
        val filteredDevices = devices.filter { it.id.startsWith(type) }
        initDevicesListView(ArrayList(filteredDevices))
    }

    // Gestion des filtres
    fun onClickShutterFilter(view: View) = updateFilterUI(view, "S")
    fun onClickGarageFilter(view: View) = updateFilterUI(view, "G")
    fun onClickLightFilter(view: View) = updateFilterUI(view, "L")

    private fun updateFilterUI(view: View, type: String) {
        val btnShutter = findViewById<Button>(R.id.btnShutterFilter)
        val btnGarage = findViewById<Button>(R.id.btnGarageFilter)
        val btnLight = findViewById<Button>(R.id.btnLightFilter)

        // Réinitialiser les couleurs
        listOf(btnShutter, btnGarage, btnLight).forEach {
            it.setTextColor(Color.WHITE)
            it.setBackgroundColor(Color.parseColor("#01053d"))
        }

        // Appliquer les styles sur le bouton sélectionné
        (view as Button).apply {
            setTextColor(Color.parseColor("#01053d"))
            setBackgroundColor(Color.WHITE)
        }

        // Filtrer les appareils
        filterDevicesByType(type)
    }

    // Démarrer la mise à jour périodique des appareils
    private fun startDeviceUpdates() {
        // Appeler loadDevices() périodiquement
        handler.postDelayed(object : Runnable {
            override fun run() {
                loadDevices()
                handler.postDelayed(this, updateInterval)  // Re-planifier le prochain appel
            }
        }, updateInterval)
    }
}
