package com.example.travelapp.user.favorit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.authentication.PrefManager
import com.example.travelapp.databinding.FragmentFavoritBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoritFragment : Fragment() {
    lateinit var mFavoritDao : FavoritDao
    lateinit var executorService: ExecutorService
    private var _binding: FragmentFavoritBinding? = null
    private lateinit var prefManager: PrefManager
    private var uid =""

    private val binding get()= _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoritBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())
        executorService = Executors.newSingleThreadExecutor()
        prefManager = PrefManager.getInstance(requireContext())
        val db = FavoritRoomDatabase.getDatabase(requireContext())
        mFavoritDao = db!!.favoritDao()!!

        uid = prefManager.getId()
    }

    override fun onResume() {
        super.onResume()
        mFavoritDao.getFavoritsByUserId(uid).observe(viewLifecycleOwner) { favorit ->
            with(binding) {
                if (favorit.isNotEmpty()) {
                    jmlhFavorit.visibility = View.VISIBLE
                    imgFavoritNotfoud.visibility = View.INVISIBLE
                } else {
                    jmlhFavorit.visibility = View.INVISIBLE
                    imgFavoritNotfoud.visibility = View.VISIBLE
                }
            }
        }
        getAllFavorit()
    }


    private fun getAllFavorit() {
        mFavoritDao.getFavoritsByUserId(uid).observe(this){
                favorit ->
            val favoritAdapter = FavoritAdapter(
                favorit,
                mFavoritDao = mFavoritDao,
                executorService = executorService
            )
            binding.rvTicket.adapter = favoritAdapter
            binding.rvTicket.layoutManager = LinearLayoutManager(requireContext())
            binding.txtJumlahFavorit.text = "${favorit.size} favorit"
        }
    }
}