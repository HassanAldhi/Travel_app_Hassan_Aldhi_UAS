package com.example.travelapp.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.authentication.PrefManager
import com.example.travelapp.authentication.Users
import com.example.travelapp.databinding.ActivityAddAdminBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class AddAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAdminBinding
    private lateinit var prefManager: PrefManager
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollectionRef = firestore.collection("users")
    private val usersListLiveData : MutableLiveData<List<Users>> by lazy {
        MutableLiveData<List<Users>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityAddAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        prefManager = PrefManager.getInstance(this)


        binding.btnCancel.setOnClickListener{
            finish()
        }

        observeUsers()
        getAllUsers()
    }
    private fun getAllUsers(){
        observeUserChanges();
    }
    private fun observeUserChanges(){
        userCollectionRef.addSnapshotListener{ snapshots, error ->
            if (error != null){
                Log.d("MainActivity",
                    "Error listening for budget changes:", error)
            }
            val users = snapshots?.toObjects(Users::class.java)
            if (users != null ){
                usersListLiveData.postValue((users))
            }
        }

    }
    private fun observeUsers() {
        usersListLiveData.observe(this) { users ->
            val adapter = AddAdminAdapter(users, userCollectionRef)
            binding.rvUser.adapter = adapter
            val numberOfColumns = 2
            binding.rvUser.layoutManager = GridLayoutManager(this@AddAdminActivity, numberOfColumns)
        }

    }
}