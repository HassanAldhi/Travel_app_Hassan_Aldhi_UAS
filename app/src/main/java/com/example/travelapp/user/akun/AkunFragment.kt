package com.example.travelapp.user.akun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.travelapp.authentication.PrefManager
import com.example.travelapp.databinding.FragmentAkunBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [AkunFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AkunFragment : Fragment() {
    private var _binding: FragmentAkunBinding? = null
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())

        with(binding){
            txtNama.text = prefManager.getUsername()
            txtEmail.text = prefManager.getEmail()
            txtPhone.text = prefManager.getPhone()
            btnLogout.setOnClickListener {
                prefManager.setLoggedIn(false)
                requireActivity().finish()
            }
        }
    }
}