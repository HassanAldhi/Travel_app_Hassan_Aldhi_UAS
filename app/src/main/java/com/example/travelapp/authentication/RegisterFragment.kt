package com.example.travelapp.authentication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travelapp.databinding.FragmentRegisterBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollectionRef = firestore.collection("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnRegister.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val phone = binding.edtPhone.text.toString()
            val password = binding.edtPassword.text.toString()

            if(username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()){
                showToast("Please fill all the field")
            }else{
                checkEmailAvailability(email, onSuccess = {
                    // Email belum digunakan, lanjutkan dengan registrasi pengguna
                    val newUser = Users(
                        username = username, email = email, phone = phone,
                        password = password, role = "user"
                    )
                    addUser(newUser)

                    (activity as AuthActivity).viewPager2.currentItem = 1
                    showToast("Registration Successfull")
                    setEmpty()
                }, onFailure = {
                    // Email sudah digunakan
                    showToast("Email already use. Please use another email.")
                })
            }
        }

        binding.txtLogin.setOnClickListener {
            (activity as AuthActivity).viewPager2.setCurrentItem(0)
        }

        return view
    }

    private fun showToast(message: String) {
        val context: Context? = activity
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setEmpty() {
        binding.edtUsername.setText("")
        binding.edtEmail.setText("")
        binding.edtPassword.setText("")
        binding.edtPhone.setText("")
    }

    private fun checkEmailAvailability(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        usersCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Email belum digunakan, memicu pemanggilan onSuccess
                    onSuccess.invoke()
                } else {
                    // Email sudah digunakan, memicu pemanggilan onFailure
                    onFailure.invoke()
                }
            }
            .addOnFailureListener { exception ->
                // Tangani kegagalan memeriksa ketersediaan email
                showToast("Error checking email availability: $exception")
            }
    }

    private fun addUser(user: Users) {
        userCollectionRef.add(user).addOnSuccessListener { documentReference ->
            val createUserId = documentReference.id
            user.id = createUserId
            documentReference.set(user).addOnFailureListener {
                Log.d("Register activity", "Error updating user id : ", it)
            }
        }.addOnFailureListener {
            Log.d("Register activity", "Error adding user id : ", it)
        }
    }
}