package com.example.travelapp.user.pesanan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.authentication.PrefManager
import com.example.travelapp.databinding.FragmentPesananBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [PesananFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PesananFragment : Fragment() {
    private var _binding: FragmentPesananBinding? = null
    private lateinit var prefManager: PrefManager
    private var userId = ""
    private val firestore = FirebaseFirestore.getInstance()
    private val pesananCollectionRef = firestore.collection("pesanan")
    private val pesananListLiveData : MutableLiveData<List<Pesanan>> by lazy {
        MutableLiveData<List<Pesanan>>()
    }
    private val binding get()= _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPesananBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())
        userId = prefManager.getId()
        getAllPesananById(userId)
        observePesananById()
    }
    private fun observePesananChangesById(userId: String) {
        pesananCollectionRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.d("PesananFragment", "Error listening for pesanan changes:", error)
                }

                val pesanan = snapshots?.toObjects(Pesanan::class.java)
                if (pesanan != null) {
                    pesananListLiveData.postValue(pesanan)

                    // Move the logic that depends on the data inside the listener
                    if (pesanan.isNotEmpty()) {
                        binding.imgPesananNotfoud.visibility = View.INVISIBLE
                        binding.jmlhPesanan.visibility = View.VISIBLE
                        binding.txtJumlahTiket.text = "${pesanan.size} Tiket Dipesan"
                    } else {
                        // Handle the case when pesanan is empty
                        binding.imgPesananNotfoud.visibility = View.VISIBLE
                        binding.jmlhPesanan.visibility = View.INVISIBLE
                    }
                }
            }
    }

    private fun getAllPesananById(userId: String) {
        observePesananChangesById(userId)
    }

    private fun observePesananById() {
        pesananListLiveData.observe(viewLifecycleOwner) { pesanan ->
            val adapter = PesananTiketAdapter(
                pesanan, pesananCollectionRef
            )
            binding.rvTicket.adapter = adapter
            binding.rvTicket.layoutManager = LinearLayoutManager(requireContext())
            if (pesanan.size > 0){
                binding.imgPesananNotfoud.visibility = View.INVISIBLE
                binding.jmlhPesanan.visibility = View.VISIBLE
                binding.txtJumlahTiket.text = "${pesanan.size} Tiket Dipesan"
            }else{
                binding.imgPesananNotfoud.visibility = View.VISIBLE
            }
        }
    }
}