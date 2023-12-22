package com.example.travelapp.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.travelapp.authentication.PrefManager
import com.example.travelapp.databinding.ActivityDashboardBinding
import com.google.firebase.FirebaseApp

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollectionRef = firestore.collection("users")
    private val tripCollectionRef = firestore.collection("trips")
    private val tripsListLiveData : MutableLiveData<List<Trips>> by lazy {
        MutableLiveData<List<Trips>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        prefManager = PrefManager.getInstance(this)

        binding.txtName.setText(prefManager.getUsername())

        binding.btnAdd.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        binding.actionLogout.setOnClickListener {
            prefManager.setLoggedIn(false)
            finish()  // Mengakhiri DashboardActivity
        }

        observeTrips()
        getAllTrips()
    }
    private fun getAllTrips(){
        observeTripChanges();
    }
    private fun observeTripChanges(){
        tripCollectionRef.addSnapshotListener{ snapshots, error ->
            if (error != null){
                Log.d("MainActivity",
                    "Error listening for budget changes:", error)
            }
            val contacts = snapshots?.toObjects(Trips::class.java)
            if (contacts != null ){
                tripsListLiveData.postValue((contacts))
            }
        }

    }
    private fun observeTrips() {
        tripsListLiveData.observe(this) { trips ->
            val adapter = TripAdapter(trips,
                onClickTrip = { trips ->
                    Toast.makeText(this@DashboardActivity, "${trips.stasiun_asal} - ${trips.stasiun_tujuan}",
                        Toast.LENGTH_SHORT).show()
                }, tripCollectionRef)
            binding.rvTicket.adapter = adapter
            binding.rvTicket.layoutManager = LinearLayoutManager(this@DashboardActivity)
            binding.txtTotalTicket.text = "${trips.size} ticket"
        }

    }
}
