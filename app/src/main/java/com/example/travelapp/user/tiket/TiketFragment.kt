package com.example.travelapp.user.tiket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.R
import com.example.travelapp.admin.Trips
import com.example.travelapp.authentication.PrefManager
import com.example.travelapp.databinding.FragmentTiketBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [TicketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TiketFragment : Fragment() {
    private var _binding: FragmentTiketBinding? = null
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val tripCollectionRef = firestore.collection("trips")
    private var selectedKotaAsal = ""
    private var selectedKotaTujuan = ""
    private val tripsListLiveData : MutableLiveData<List<Trips>> by lazy {
        MutableLiveData<List<Trips>>()
    }
    private val binding get()= _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())
        var username = prefManager.getUsername()

        binding.txtUsername.setText("Halo $username ")

        with(binding){
            val ktAsal = resources.getStringArray(R.array.ktAsal)
            val adapterKtAsal = ArrayAdapter(requireContext(), R.layout.dropdown_menu, ktAsal)
            edtKtAsal.setAdapter(adapterKtAsal)

            val ktTujuan = resources.getStringArray(R.array.ktTujuan)
            val adapterKtTujuan = ArrayAdapter(requireContext(), R.layout.dropdown_menu, ktTujuan)
            edtKtTujuan.setAdapter(adapterKtTujuan)

            edtKtAsal.setOnItemClickListener { _, _, position, _ ->
                selectedKotaAsal = edtKtAsal.text.toString()
            }

            edtKtTujuan.setOnItemClickListener { _, _, position, _ ->
                selectedKotaTujuan = edtKtTujuan.text.toString()
            }

            btnCari.setOnClickListener{
                if(edtKtAsal.text.isNotEmpty() && edtKtTujuan.text.isNotEmpty()){
                    observeTripsByLocation(selectedKotaAsal, selectedKotaTujuan)
                    getAllTripsByLocation(selectedKotaAsal, selectedKotaTujuan)
                    edtKtAsal.setText("")
                    edtKtTujuan.setText("")
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Lengkapi informasi lokasi !!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    }
    private fun observeTripChangesByLocation(ktAsal: String, ktTujuan: String) {
        tripCollectionRef
            .whereEqualTo("kota_Asal", ktAsal)
            .whereEqualTo("kota_tujuan", ktTujuan)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.d("TiketFragment", "Error listening for trip changes:", error)
                }

                val trips = snapshots?.toObjects(Trips::class.java)
                if (trips != null) {
                    tripsListLiveData.postValue(trips)
                }
            }
    }

    private fun getAllTripsByLocation(ktAsal: String, ktTujuan: String) {
        observeTripChangesByLocation(ktAsal, ktTujuan)
    }

    private fun observeTripsByLocation(ktAsal: String, ktTujuan: String) {
        tripsListLiveData.observe(viewLifecycleOwner) { trips ->
            val adapter = TripTicketAdapter(
                trips,
                onClickTrip = { trip ->
                    Toast.makeText(
                        requireContext(),
                        "${trip.stasiun_asal} - ${trip.stasiun_tujuan}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                tripCollectionRef
            )
            binding.rvTicket.adapter = adapter
            binding.rvTicket.layoutManager = LinearLayoutManager(requireContext())
            binding.txtJumlahTiket.text = "${trips.size} Tiket $selectedKotaAsal - $selectedKotaTujuan Ditemukan "
            if (trips.size == 0){
                Toast.makeText(
                    requireContext(),
                    "Tiket tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                Toast.makeText(
                    requireContext(),
                    "${trips.size} Tiket Ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}